package ifs.meuonibus.Models.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID usuId;
    private String usuNome;
    private String usuEmail;
    private String usuSenha;
    private UserRole role;
    private String senhaTemporaria;
    private LocalDateTime senhaTempExpiracao;

    public Usuario(String usuNome, String usuEmail, String usuSenha, UserRole role,String senhaTemporaria,LocalDateTime senhaTempExpiracao) {
        this.usuNome = usuNome;
        this.usuEmail = usuEmail;
        this.usuSenha = usuSenha;
        this.role = role;
        this.senhaTemporaria = senhaTemporaria;
        this.senhaTempExpiracao = senhaTempExpiracao;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"),
                                                       new SimpleGrantedAuthority("ROLE_USER"),
                                                       new SimpleGrantedAuthority("ROLE_COORD"));
        else if(this.role==UserRole.COORD) return List.of(new SimpleGrantedAuthority("ROLE_COORD"));
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return usuSenha;
    }


    @Override
    public String getUsername() {
        return usuEmail;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
