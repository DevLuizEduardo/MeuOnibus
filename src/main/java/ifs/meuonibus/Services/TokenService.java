package ifs.meuonibus.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ifs.meuonibus.Dto.LoginResponseDTO;
import ifs.meuonibus.Models.User.Usuario;
import ifs.meuonibus.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secretToken;
    @Value("${api.security.refresh-token.secret}")
    private String secretRefreshToken;
    @Value("${auth.jwt.token.expiration}")
    private int tempExpirationToken;
    @Value("${auth.jwt.refresh-token.expiration}")
    private int tempExpirationRefreshToken;
    @Autowired
    UserRepository userRepository;


    public LoginResponseDTO obterToken (Usuario usuario) {

        return LoginResponseDTO.builder()
                .token(gerarToken(usuario))
                .refreshToken(gerarRefreshToken(usuario))
                .build();

    }
    public String gerarToken(Usuario usuario) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretToken);
            String token = JWT.create()
                    .withIssuer("auth_api")
                    .withSubject(usuario.getUsuEmail())
                    .withExpiresAt(getExpirationDate(tempExpirationToken))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar o token", e);

        }
    }

        public String gerarRefreshToken(Usuario usuario) {
            try {
                Algorithm algorithm = Algorithm.HMAC256(secretRefreshToken);
                String token = JWT.create()
                        .withIssuer("auth_api")
                        .withSubject(usuario.getUsuEmail())
                        .withClaim("refreshToken", true)
                        .withExpiresAt(getExpirationDate(tempExpirationRefreshToken))
                        .sign(algorithm);
                return token;
            } catch (JWTCreationException e) {
                throw new RuntimeException("Erro ao gerar o token", e);


            }
        }
    public String gerarTokenRedefinirSenha(Usuario usuario) {

        try{

            Algorithm algorithm = Algorithm.HMAC256(secretToken);
            String token = JWT.create()
                    .withIssuer("auth_api")
                    .withSubject(usuario.getUsuEmail())
                    .withClaim("resetPassword", true)
                    .withExpiresAt(getExpirationDate(tempExpirationToken))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar o token",e);

        }



    }

    public String validateToken(String token){

            try {
                Algorithm algorithm = Algorithm.HMAC256(secretToken);
                return JWT.require(algorithm)
                        .withIssuer("auth_api")
                        .build()
                        .verify(token)
                        .getSubject();
            } catch (JWTVerificationException e) {
                return "";

            }
        }


    public boolean verifyResetPassword(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretToken);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth_api")
                    .withClaim("resetPassword", true) // Verifica se a claim resetPassword está presente e é true
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);



            return decodedJWT.getClaim("resetPassword").asBoolean();
        } catch (JWTVerificationException e) {
            return false; // Token inválido ou sem a claim resetPassword
        }
    }

    public boolean verifyRefreshToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretRefreshToken);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("auth_api")
                    .withClaim("refreshToken", true) // Verifica se a claim refreshToken está presente e é true
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("refreshToken").asBoolean();
        } catch (JWTVerificationException e) {
            return false; // Token inválido ou sem a claim refreshToken
        }
    }



    private Instant getExpirationDate(int expiration) {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));


    }
}
