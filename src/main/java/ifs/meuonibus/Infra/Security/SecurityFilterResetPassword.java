package ifs.meuonibus.Infra.Security;

import ifs.meuonibus.Repository.UserRepository;
import ifs.meuonibus.Services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class SecurityFilterResetPassword extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null && tokenService.validarResetPassword(token)) {

            filterChain.doFilter(request, response);

        }else {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Chave de Acesso Negado");

        }


    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null)return null;
        return authHeader.replace("Bearer ", "");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Aplica este filtro apenas na rota de redefinição de senha
        return !request.getRequestURI().equals("/aluno/redefinir-senha");
    }
}
