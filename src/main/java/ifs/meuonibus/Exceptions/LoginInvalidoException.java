package ifs.meuonibus.Exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class LoginInvalidoException extends RuntimeException {

    public LoginInvalidoException() {
        super("Login ou senha invalido");
    }
    public LoginInvalidoException(String msg) {
        super(msg);
    }
}
