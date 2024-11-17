package ifs.meuonibus.Controller;

import ifs.meuonibus.Dto.AuthenticationDTO;
import ifs.meuonibus.Dto.LoginResponseDTO;
import ifs.meuonibus.Form.CadAlunoDTO;
import ifs.meuonibus.Infra.Security.SecurityConfigurations;
import ifs.meuonibus.Models.User.UserRole;
import ifs.meuonibus.Models.User.Usuario;
import ifs.meuonibus.Repository.UserRepository;
import ifs.meuonibus.Services.EmailService;
import ifs.meuonibus.Services.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@Tag(name = "Aluno")
//@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private EmailService emailService;


    @PostMapping("/aluno/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(),data.senha());
        var auth = authenticationManager.authenticate(usernamePassword);
       // Usuario user = (Usuario) auth.getPrincipal();
        var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));

    }

    @PostMapping("/aluno/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid CadAlunoDTO data){
        if(this.userRepository.findByUsuEmail(data.usuEmail()) != null) return ResponseEntity.badRequest().build();
        String senhaTemporaria = this.gerarSenhaTemporaria();
        LocalDateTime validade = LocalDateTime.now().plusDays(2);//Gera o tempo de Validade da Senha temporária

        //Encriptação da Senha Temporária
        String encryptedPasswordTemporary = new BCryptPasswordEncoder().encode(senhaTemporaria);

        UserRole role = UserRole.USER;




        Usuario user = new Usuario(data.usuNome(),data.usuEmail(),encryptedPasswordTemporary,role,encryptedPasswordTemporary,validade);
          this.userRepository.save(user);
          this.enviarEmailComSenhaTemporaria(data.usuEmail(),senhaTemporaria);

          return ResponseEntity.ok().build();

    }


    private void enviarEmailComSenhaTemporaria(String destinatario, String senhaTemporaria) {
        String assunto  = "Sua senha temporária";
        String texto ="Sua senha temporária é: " + senhaTemporaria + ". Ela expira em 2 dias.";

        emailService.enviarEmail(destinatario,assunto,texto);

    }

    private String gerarSenhaTemporaria() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int tamanhoSenha = 4;
        SecureRandom random = new SecureRandom();
        StringBuilder senhaTemporaria = new StringBuilder(tamanhoSenha);

        for (int i = 0; i < tamanhoSenha; i++) {
            int index = random.nextInt(caracteres.length());
            senhaTemporaria.append(caracteres.charAt(index));
        }
        return senhaTemporaria.toString();
    }
   /* @PostMapping("/aluno/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid CadAlunoDTO data){
        if(this.userRepository.findByUsuEmail(data.usuEmail()) != null) return ResponseEntity.badRequest().build();
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.UsuSenha());


        Usuario user = new Usuario(data.usuNome(),data.usuEmail(),encryptedPassword,UserRole.USER);
        this.userRepository.save(user);
        return ResponseEntity.ok().build();

    }*/



}
