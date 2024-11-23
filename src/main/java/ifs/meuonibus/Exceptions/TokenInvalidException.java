package ifs.meuonibus.Exceptions;

public class TokenInvalidException extends RuntimeException{
    public TokenInvalidException(){
        super("token inválido ou expirado!");
    }
    public TokenInvalidException(String message){
        super(message);
    }
}
