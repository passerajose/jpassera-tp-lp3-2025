package py.edu.uc.jpasseratplp32025.exception;

/**
 * Excepción checked que indica que no hay suficientes días disponibles.
 */
public class DiasInsuficientesException extends Exception {
    public DiasInsuficientesException(String message) {
        super(message);
    }

    public DiasInsuficientesException(String message, Throwable cause) {
        super(message, cause);
    }
}