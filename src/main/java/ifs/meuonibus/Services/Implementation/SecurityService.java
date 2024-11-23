package ifs.meuonibus.Services.Implementation;

import ifs.meuonibus.Dto.AuthenticationDTO;
import ifs.meuonibus.Dto.LoginResponseDTO;
import ifs.meuonibus.Exceptions.*;
import ifs.meuonibus.FormDTO.CadUsuarioDTO;
import ifs.meuonibus.FormDTO.EmailDTO;
import ifs.meuonibus.FormDTO.LoginResetPasswordDTO;
import ifs.meuonibus.FormDTO.RefreshTokenDTO;
import ifs.meuonibus.Models.User.UserRole;
import ifs.meuonibus.Models.User.Usuario;
import ifs.meuonibus.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class SecurityService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    // Verifica a validade do e-mail e da senha do usuário e retorna a chave de acesso
    public Object autenticar(AuthenticationDTO data) {
        try {
            //Verifica e-mail e senha passada pelo usuário através do Spring Security
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
            var auth = authenticationManager.authenticate(usernamePassword);
            Usuario user = (Usuario) auth.getPrincipal();


            //Verifica se é uma senha temporária e sua data de validade
            if (encoder.matches(data.senha(), user.getSenhaTemporaria())) {
                if (user.getSenhaTempExpiracao().isBefore(LocalDateTime.now())) {

                    throw new SenhaExpiradaException();
                }


                return tokenService.gerarTokenRedefinirSenha(user);
            }

            // Retorna token e o refreshToken
            return tokenService.obterToken(user);

        } catch (Exception ex) {
            // Retorna mensagem de erro após verificar login e senha
            throw new LoginInvalidoException();
        }
    }

    //Gera uma nova senha temporária caso o usuário não consiga ter acesso à conta
    public void resetarSenhaTemporaria(EmailDTO usuario) {
        UserDetails user = userRepository.findByUsuEmail(usuario.email());

        if (user == null) {
            throw new NoExistsUserException();
        }

        Usuario alterUser = (Usuario) user;
        String senha = UUID.randomUUID().toString().substring(0, 4);
        LocalDateTime validade = LocalDateTime.now().plusDays(2);
        String senhaCrypto = encoder.encode(senha);

        alterUser.setSenhaTemporaria(senhaCrypto);
        alterUser.setUsuSenha(senhaCrypto);
        alterUser.setSenhaTempExpiracao(validade);

        userRepository.save(alterUser);

        emailService.enviarEmail(usuario.email(), senha);


    }

  // Cria um novo usuário no Banco de Dados e enviar sua senha temporária para seu e-mail
    public void cadastrarAluno(CadUsuarioDTO cadastro) {
        if (this.userRepository.findByUsuEmail(cadastro.usuEmail()) != null) {
            throw new ExistsUserException();
        }
        //Gera Senha temporária e sua validade
        String senha = UUID.randomUUID().toString().substring(0, 4);
        LocalDateTime validade = LocalDateTime.now().plusDays(2);

        //Encriptação da Senha Temporária
        String senhaCrypt = encoder.encode(senha);

        //Pré-definindo a autoridade do usuário com a role
        UserRole role = UserRole.USER;


        Usuario user = new Usuario(cadastro.usuNome(), cadastro.usuEmail(), senhaCrypt, role, senhaCrypt, validade);
        this.userRepository.save(user);

        //Envia o e-mail para o usuário
        emailService.enviarEmail(cadastro.usuEmail(), senha);


    }

    // Atualiza o token do usuário caso tenha expirado através do RefreshToken
    public LoginResponseDTO atualizarToken(RefreshTokenDTO data) {

        tokenService.verifyRefreshToken(data.refreshToken());

        var login = tokenService.validateToken(data.refreshToken());
        UserDetails userDetails = userRepository.findByUsuEmail(login);


        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario user = (Usuario) authentication.getPrincipal();


        return tokenService.obterToken(user);
    }

    // Atualiza a senha no primeiro acesso do usuário Aluno
    public void atualizarSenha(LoginResetPasswordDTO token){

        //Verifica se o Token é valido para Redefinir a senha
        tokenService.verifyResetPassword(token.tokenResetPassword());

        var login = tokenService.validateToken(token.tokenResetPassword());
        Usuario user = (Usuario) userRepository.findByUsuEmail(login);
        String senhaCrypt =encoder.encode(token.novaSenha());
        user.setUsuSenha(senhaCrypt);
        userRepository.save(user);

    }



}
