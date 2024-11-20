package ifs.meuonibus.Infra.Security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@SecurityScheme(name =  SecurityConfigurations.SECURITY,type = SecuritySchemeType.HTTP,bearerFormat = "JWT",scheme = "bearer")
public class SecurityConfigurations {

    public static final String  SECURITY = "bearerAuth";

    @Autowired
    SecurityFilter securityFilter;
    @Autowired
    SecurityFilterResetPassword securityFilterResetPassword;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->authorize
                        .requestMatchers(HttpMethod.POST,"/auth/aluno/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/auth/aluno/cadastro").permitAll()
                        .requestMatchers("/v3/api-docs/**","swagger-ui/**","swagger-ui/index.html").permitAll()
                        .anyRequest().authenticated()

                )
                .addFilterBefore(securityFilterResetPassword, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(securityFilter,SecurityFilterResetPassword.class)
                .build();

    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



}
