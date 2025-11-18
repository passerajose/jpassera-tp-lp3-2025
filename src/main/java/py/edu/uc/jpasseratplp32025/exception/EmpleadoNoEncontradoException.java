package py.edu.uc.jpasseratplp32025.exception;

/**
 * RuntimeException lanzada cuando no se encuentra un empleado.
 */
public class EmpleadoNoEncontradoException extends RuntimeException {
    public EmpleadoNoEncontradoException(String message) {
        super(message);
    }

    public EmpleadoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}