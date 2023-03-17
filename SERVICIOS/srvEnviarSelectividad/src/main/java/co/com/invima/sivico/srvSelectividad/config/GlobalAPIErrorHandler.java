package co.com.invima.sivico.srvSelectividad.config;

import co.com.invima.modelopapf.dto.generic.GenericResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalAPIErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class, JsonProcessingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GenericResponseDTO> errorPeticion(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenericResponseDTO.builder()
                        .objectResponse(ex.getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("La petición no puede ser entendida por el servidor debido a errores de sintaxis")
                        .build());
    }

    @ExceptionHandler(value = {NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<GenericResponseDTO> errorNoEncontrado(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(GenericResponseDTO.builder()
                        .objectResponse(ex.getMessage())
                        .statusCode(HttpStatus.NOT_FOUND.value())
                        .message("El recurso solicitado no pudo ser encontrado")
                        .build());
    }

    @ExceptionHandler({Exception.class, IOException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GenericResponseDTO> internalError(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponseDTO.builder()
                        .objectResponse(ex.getMessage())
                        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .message("Se presentó una condición inesperada que impidió completar la petición")
                        .build());
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(GenericResponseDTO.builder()
                        .objectResponse(ex.getMessage())
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("La petición no puede ser entendida por el servidor debido a errores de sintaxis")
                        .build());
    }

}