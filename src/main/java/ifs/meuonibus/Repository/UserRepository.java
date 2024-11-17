package ifs.meuonibus.Repository;

import ifs.meuonibus.Models.User.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserRepository extends JpaRepository<Usuario, UUID> {

    UserDetails findByUsuEmail(String email);
}
