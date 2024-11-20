package ifs.meuonibus.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import ifs.meuonibus.Dto.LoginResponseDTO;
import ifs.meuonibus.Models.User.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    @Value("${auth.jwt.token.expiration}")
    private int tempExpirationToken;
    @Value("${auth.jwt.refresh-token.expiration}")
    private int tempExpirationRefreshToken;

    public LoginResponseDTO obterToken (Usuario usuario) {

        return LoginResponseDTO.builder()
                .token(gerarToken(usuario,tempExpirationToken))
                .refreshToken(gerarToken(usuario,tempExpirationRefreshToken))
                .build();

    }
    public String gerarToken(Usuario usuario,int expiration) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth_api")
                    .withSubject(usuario.getUsuEmail())
                    .withExpiresAt(getExpirationDate(expiration))
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Erro ao gerar o token",e);

        }




    }
    public String gerarTokenRedefinirSenha(Usuario usuario) {

        try{

            Algorithm algorithm = Algorithm.HMAC256(secret);
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

    public String validateToken(String token) {

        try{
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return    JWT.require(algorithm)
                    .withIssuer("auth_api")
                    .build()
                    .verify(token)
                    .getSubject();
        }catch (JWTVerificationException e){
            return "";

        }

    }
    public boolean validarResetPassword(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
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

    private Instant getExpirationDate(int expiration) {
        return LocalDateTime.now().plusHours(expiration).toInstant(ZoneOffset.of("-03:00"));


    }
}
