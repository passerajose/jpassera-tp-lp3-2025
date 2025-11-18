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
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejador para FechaNacimientoFuturaException (HTTP 400)
    @ExceptionHandler(FechaNacimientoFuturaException.class)
    public ResponseEntity<ErrorResponseDto> handleFechaNacimientoFuturaException(
            FechaNacimientoFuturaException ex,
            WebRequest request) {

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() != null ? ex.getMessage() : "La fecha de nacimiento no puede ser en el futuro",
                request.getDescription(false),
                "Validation Error"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejador para ConstraintViolationException (errores de validación JPA/Hibernate) (HTTP 400)
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
                violationMessage,
                request.getDescription(false),
                "Constraint Validation Error (JPA/Hibernate)"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Manejador para errores de JSON (malformado, tipo incorrecto) (HTTP 400)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest req) {
        String msg = ex.getCause() != null && ex.getCause().getMessage() != null ? ex.getCause().getMessage() : "Cuerpo de solicitud JSON mal formado o tipo de dato incorrecto.";
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), msg, req.getDescription(false), "JSON Parse Error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejador para errores de Bean Validation en DTOs de entrada (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest req) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst().orElse("Validation failed");
        ErrorResponseDto error = new ErrorResponseDto(HttpStatus.BAD_REQUEST.value(), msg, req.getDescription(false), "Validation Error");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejador para EmpleadoNoEncontradoException (HTTP 404 NOT FOUND)
    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDto> handleEmpleadoNoEncontrado(EmpleadoNoEncontradoException ex, WebRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage() != null ? ex.getMessage() : "Empleado no encontrado",
                request.getDescription(false),
                "EmpleadoNoEncontradoException"
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Manejador para DiasInsuficientesException (HTTP 400 BAD REQUEST)
    @ExceptionHandler(DiasInsuficientesException.class)
    public ResponseEntity<ErrorResponseDto> handleDiasInsuficientes(DiasInsuficientesException ex, WebRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() != null ? ex.getMessage() : "Días insuficientes para la operación",
                request.getDescription(false),
                "DiasInsuficientesException"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejador para PermisoNoConcedidoException (HTTP 400 BAD REQUEST)
    @ExceptionHandler(PermisoNoConcedidoException.class)
    public ResponseEntity<ErrorResponseDto> handlePermisoNoConcedido(PermisoNoConcedidoException ex, WebRequest request) {
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() != null ? ex.getMessage() : "El permiso fue denegado por políticas internas.",
                request.getDescription(false),
                "PermisoNoConcedidoException"
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Manejador Global de Fallback (captura cualquier otra Exception) (HTTP 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        // Recomendado: loggear la traza completa de la excepción aquí
        ex.printStackTrace();

        ErrorResponseDto errorResponse = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Se produjo un error interno en el servidor: " + ex.getMessage(),
                request.getRequestURI(), // Obtiene la URI de la solicitud
                ex.getClass().getSimpleName()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}