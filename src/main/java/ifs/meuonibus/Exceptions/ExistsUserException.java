package ifs.meuonibus.Exceptions;

public class ExistsUserException extends RuntimeException{
    public ExistsUserException(){
        super("Usuário já cadastrado");
    }
    public ExistsUserException(String message){
        super(message);
    }
}
