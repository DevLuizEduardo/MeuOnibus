package ifs.meuonibus.Infra.ExceptionHandler;

import ifs.meuonibus.Exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class Handler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(LoginInvalidoException.class)
    public ResponseEntity<ErrorMessage> handleLoginInvalidoException(LoginInvalidoException ex){

        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);

    }
    @ExceptionHandler(SenhaExpiradaException.class)
    public ResponseEntity<ErrorMessage> handleSenhaExpiradaException(SenhaExpiradaException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
    @ExceptionHandler(RequestErroException.class)
    public ResponseEntity<ErrorMessage> handleRequestErroException(RequestErroException ex){

        ErrorMessage message = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);

    }
    @ExceptionHandler(NoExistsUserException.class)
    public ResponseEntity<ErrorMessage> handleVerifyUsuarioException(NoExistsUserException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);

    }
    @ExceptionHandler(ExistsUserException.class)
    public ResponseEntity<ErrorMessage> handleVerifyUsuarioException(ExistsUserException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
    @ExceptionHandler(TokenInvalidException.class)
    public ResponseEntity<ErrorMessage> handleTokenInvalidException(TokenInvalidException ex){
        ErrorMessage message = new ErrorMessage(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
    }
}
