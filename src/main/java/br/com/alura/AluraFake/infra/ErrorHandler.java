package br.com.alura.AluraFake.infra;

import br.com.alura.AluraFake.util.ErrorItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException exception) {
        var error = exception.getFieldErrors().get(0);
        return ResponseEntity.badRequest().body(new ErrorItemDTO(error.getField(), error.getDefaultMessage()));
    }
}
