package ifs.meuonibus.Services;

import ifs.meuonibus.Dto.AuthenticationDTO;
import ifs.meuonibus.Dto.TokenResetPasswordDTO;
import ifs.meuonibus.Models.User.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerifyService {

     @Autowired
    private  AuthenticationManager authenticationManager;
     @Autowired
    private  TokenService tokenService;
     @Autowired
     private PasswordEncoder encoder;





    public ResponseEntity autenticar(AuthenticationDTO data) {
        try {
           //Verifica e-mail e senha passada pelo usuário através do Spring Security
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(),data.senha());
            var auth = authenticationManager.authenticate(usernamePassword);
            Usuario user = (Usuario) auth.getPrincipal();

            //Verifica se é uma senha temporária e sua data de validade
            if (encoder.matches(data.senha(), user.getSenhaTemporaria())) {
                if (user.getSenhaTempExpiracao().isBefore(LocalDateTime.now())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha temporária expirada.");
                }

                var token = tokenService.gerarTokenRedefinirSenha(user);
                return ResponseEntity.ok(new TokenResetPasswordDTO(token));
            }

            // Retorna token e o refreshToken
            var token = tokenService.obterToken(user);
            return ResponseEntity.ok(token);

        } catch (BadCredentialsException ex) {
            // Retorna mensagem de erro após verificar login
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Senha ou e-mail inválido.");
        } catch (Exception ex) {
            // Tratamento de exceções genéricas
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a solicitação.");
        }
    }
}
