package py.edu.uc.jpasseratplp32025.exception;

/**
 * Excepción personalizada lanzada cuando una solicitud de permiso
 * (vacaciones o días libres) es denegada.
 */
public class PermisoNoConcedidoException extends Exception {

    // Campo para el motivo específico del rechazo
    private final String motivoRechazo;

    /**
     * Constructor 1: Sin causa raíz (para errores simples como tipo de permiso inválido).
     * @param mensaje Mensaje general de la excepción.
     * @param motivoRechazo La razón específica por la cual el permiso fue denegado.
     */
    public PermisoNoConcedidoException(String mensaje, String motivoRechazo) {
        super(mensaje);
        this.motivoRechazo = motivoRechazo;
    }

    /**
     * Constructor 2: Con causa raíz (para envolver otras excepciones o condiciones de error de límite/saldo).
     * @param mensaje Mensaje general de la excepción.
     * @param motivoRechazo La razón específica por la cual el permiso fue denegado.
     * @param causa La excepción original que causó este rechazo (opcional, para encadenamiento).
     */
    public PermisoNoConcedidoException(String mensaje, String motivoRechazo, Throwable causa) {
        super(mensaje, causa);
        this.motivoRechazo = motivoRechazo;
    }

    /**
     * Obtiene el motivo específico del rechazo.
     * @return El motivo del rechazo.
     */
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
}