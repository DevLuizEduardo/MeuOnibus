package ifs.meuonibus.Exceptions;

public class SenhaExpiradaException extends RuntimeException{

    public SenhaExpiradaException(){
        super("Senha temporária expirada.");
    }

      public SenhaExpiradaException(String message){
        super(message);
    }
}
