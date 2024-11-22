package ifs.meuonibus.Controller;

import ifs.meuonibus.Dto.AuthenticationDTO;
import ifs.meuonibus.FormDTO.*;
import ifs.meuonibus.Infra.Security.SecurityConfigurations;
import ifs.meuonibus.Models.User.UserRole;
import ifs.meuonibus.Models.User.Usuario;
import ifs.meuonibus.Repository.UserRepository;
import ifs.meuonibus.Services.VerifyService;
import ifs.meuonibus.Services.EmailService;
import ifs.meuonibus.Services.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Cadastro e Login dos Usuários")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerifyService verifyService;


    @PostMapping("/aluno/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO conta) {

        return verifyService.autenticar(conta);

    }


    @PostMapping("/aluno/reset-senhatemp")
    public ResponseEntity refreshSenha(@RequestBody @Valid EmailDTO usuario) {
        UserDetails user = userRepository.findByUsuEmail(usuario.email());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não existe!");
        }

        Usuario alterUser = (Usuario) user;
        String senhaTemp = UUID.randomUUID().toString().substring(0, 4);
        LocalDateTime validade = LocalDateTime.now().plusDays(2);
        String encryptedPasswordTemp = new BCryptPasswordEncoder().encode(senhaTemp);

        alterUser.setSenhaTemporaria(encryptedPasswordTemp);
        alterUser.setUsuSenha(encryptedPasswordTemp);
        alterUser.setSenhaTempExpiracao(validade);

        userRepository.save(alterUser);

        emailService.enviarEmail(usuario.email(), senhaTemp);


        return ResponseEntity.ok("Senha Temporária Renovada com sucesso.");



    }

    @PostMapping("/aluno/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid CadAlunoDTO data){
        if(this.userRepository.findByUsuEmail(data.usuEmail()) != null) return ResponseEntity.badRequest().body("Usuário já Existe");
        String senhaTemporaria = UUID.randomUUID().toString().substring(0, 4);
        LocalDateTime validade = LocalDateTime.now().plusDays(2);//Gera o tempo de Validade da Senha temporária

        //Encriptação da Senha Temporária
        String encryptedPasswordTemporary = new BCryptPasswordEncoder().encode(senhaTemporaria);

        UserRole role = UserRole.USER;




        Usuario user = new Usuario(data.usuNome(),data.usuEmail(),encryptedPasswordTemporary,role,encryptedPasswordTemporary,validade);
          this.userRepository.save(user);
          emailService.enviarEmail(data.usuEmail(),senhaTemporaria);

          return ResponseEntity.ok().build();

    }


    @PostMapping("/refresh-token")
    public ResponseEntity refreshToken(@RequestBody @Valid RefreshTokenDTO data){

        var login =tokenService.validateToken(data.refreshToken());
       UserDetails userDetails = userRepository.findByUsuEmail(login);



        if(!tokenService.verifyRefreshToken(data.refreshToken())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token inválido ou expirado!");
        }

        var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Usuario user = (Usuario) authentication.getPrincipal();


        return ResponseEntity.ok(tokenService.obterToken(user));






    }


    @PostMapping("/aluno/redefinir-senha")
    public ResponseEntity RedefinirSenha(LoginResetPasswordDTO token){
          boolean verify = tokenService.verifyResetPassword(token.tokenResetPassword());

        if (!verify) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token Não Autorizado");
        }

        var login = tokenService.validateToken(token.tokenResetPassword());
        Usuario user = (Usuario) userRepository.findByUsuEmail(login);
        String encryptedPassword = new BCryptPasswordEncoder().encode(token.novaSenha());
        user.setUsuSenha(encryptedPassword);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }


/*
     @PostMapping("/aluno/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid CadAlunoDTO data){
        if(this.userRepository.findByUsuEmail(data.usuEmail()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.UsuSenha());


        Usuario user = new Usuario(data.usuNome(),data.usuEmail(),encryptedPassword,UserRole.USER);
        this.userRepository.save(user);
        return ResponseEntity.ok().build();

    }*/



}
