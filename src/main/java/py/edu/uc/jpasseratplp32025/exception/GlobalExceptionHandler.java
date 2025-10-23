package py.edu.uc.jpasseratplp32025.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import py.edu.uc.jpasseratplp32025.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FechaNacimientoFuturaException.class)
    public ResponseEntity<ErrorResponseDto> handleFechaNacimientoFuturaException(
            FechaNacimientoFuturaException ex,
            WebRequest request) {
        
        ErrorResponseDto errorResponse = new ErrorResponseDto(
            HttpStatus.BAD_REQUEST.value(),
            "La fecha de nacimiento no puede ser en el futuro",
            request.getDescription(false),
            "Validation Error"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // =========================================================================
    // NUEVO MANEJADOR: ConstraintViolationException (para Bean Validation en JPA)
    // =========================================================================
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolationException(
            ConstraintViolationException ex,
            WebRequest request) {

        // Extraer el mensaje específico de la violación
        String violationMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .findFirst()
                .orElse("Error de validación de restricción de datos.");

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                violationMessage, // Usamos el mensaje específico de la validación (@Pattern, @Size)
                request.getDescription(false),
                "Constraint Validation Error (JPA/Hibernate)"
        );

        // Se devuelve 400 Bad Request
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        ErrorResponseDto errorResponse = new ErrorResponseDto(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Se produjo un error interno en el servidor",
            request.getDescription(false),
            ex.getClass().getSimpleName()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest req) {
        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), msg, req.getDescription(false), "JSON parse error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .findFirst().orElse("Validation failed");
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), msg, req.getDescription(false), "Validation Error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}