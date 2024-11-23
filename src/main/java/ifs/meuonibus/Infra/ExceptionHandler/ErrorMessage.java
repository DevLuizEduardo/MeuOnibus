package ifs.meuonibus.Infra.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;


@AllArgsConstructor
@Data
public class ErrorMessage {
    private HttpStatus status;
    private String message;
}
