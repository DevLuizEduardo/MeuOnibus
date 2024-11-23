package ifs.meuonibus.Controller;

import ifs.meuonibus.Dto.AuthenticationDTO;
import ifs.meuonibus.FormDTO.*;
import ifs.meuonibus.Infra.Security.SecurityConfigurations;
import ifs.meuonibus.Repository.UserRepository;
import ifs.meuonibus.Services.Implementation.SecurityService;
import ifs.meuonibus.Services.Implementation.TokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/auth")
@Tag(name = "Cadastro e Login dos Usuários")
@SecurityRequirement(name = SecurityConfigurations.SECURITY)
public class AuthenticationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SecurityService securityService;


    @PostMapping("/aluno/login")
    public ResponseEntity<Object> login(@RequestBody @Valid AuthenticationDTO conta) {

        return ResponseEntity.ok(securityService.autenticar(conta));

    }


    @PostMapping("/aluno/reset-senha")
    public ResponseEntity<String> refreshSenha(@RequestBody @Valid EmailDTO usuario) {

        securityService.resetarSenhaTemporaria(usuario);

        return ResponseEntity.ok("Senha Temporária Renovada com sucesso.");



    }

    @PostMapping("/aluno/cadastro")
    public ResponseEntity<String> cadastro(@RequestBody @Valid CadUsuarioDTO user){

        securityService.cadastrarAluno(user);

          return ResponseEntity.ok("Cadastro Realizado com sucesso.");

    }


    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@RequestBody @Valid RefreshTokenDTO token){

        return ResponseEntity.ok(securityService.atualizarToken(token));

    }


    @PostMapping("/aluno/redefinir-senha")
    public ResponseEntity<String> RedefinirSenha(LoginResetPasswordDTO token){
         securityService.atualizarSenha(token);

        return ResponseEntity.ok("Senha Redefinida com sucesso.");
    }



}
