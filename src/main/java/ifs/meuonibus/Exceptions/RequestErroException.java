package ifs.meuonibus.Exceptions;

public class RequestErroException extends RuntimeException{
    public RequestErroException(){
        super("Erro ao processar o request.");
    }
    public RequestErroException(String mensagem){
        super(mensagem);
    }
}
