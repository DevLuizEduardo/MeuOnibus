package ifs.meuonibus.Exceptions;

public class NoExistsUserException extends RuntimeException{
    public NoExistsUserException(){
        super("Usuário não existe!");
    }
    public NoExistsUserException(String message){
        super(message);
    }
}
