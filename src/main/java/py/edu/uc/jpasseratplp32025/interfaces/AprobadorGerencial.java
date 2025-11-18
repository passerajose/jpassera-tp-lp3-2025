package py.edu.uc.jpasseratplp32025.interfaces;

import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;

/**
 * Interfaz para definir el contrato de aprobación de solicitudes de permisos
 * que es exclusivo de roles con autoridad (como Gerentes o RR.HH.).
 *
 * Esta interfaz extiende Permisionable, lo que significa que un AprobadorGerencial
 * también es capaz de solicitar permisos para sí mismo.
 */
public interface AprobadorGerencial extends Permisionable {

    /**
     * Método adicional: Procesa la aprobación o rechazo formal de una solicitud pendiente.
     * Este proceso es posterior a la validación inicial.
     *
     * @param idSolicitud El ID único de la solicitud a procesar.
     * @param esAprobado Si la decisión final es "aprobado" (true) o "rechazado" (false).
     * @param notas Comentarios adicionales de Gerencia o RR.HH.
     * @throws PermisoNoConcedidoException Si se rechaza y se requiere un registro de la razón.
     * @return true si el proceso se completó.
     */
    boolean procesarAprobacionGerencial(long idSolicitud,
                                        boolean esAprobado,
                                        String notas)
            throws PermisoNoConcedidoException;
}