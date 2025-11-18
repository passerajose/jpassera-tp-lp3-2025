package py.edu.uc.jpasseratplp32025.interfaces;

import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;

import java.time.LocalDate;

/**
 * Interfaz para la gestión de solicitudes de permisos y vacaciones
 * según las normativas laborales de Paraguay.
 */
public interface Permisionable {

    /**
     * Solicita días de permiso, que pueden ser vacaciones o días libres especiales.
     * La implementación debe verificar la antigüedad y los días disponibles.
     *
     * @param fechaInicio La fecha de inicio del permiso o vacación.
     * @param fechaFin La fecha de finalización del permiso o vacación.
     * @param tipoPermiso Una cadena que identifica el tipo de permiso (ej: "VACACIONES", "MATRIMONIO").
     * @param codigoEmpleado El identificador único del empleado que solicita el permiso.
     * @return true si la solicitud es aprobada y registrada.
     * @throws PermisoNoConcedidoException Si el permiso es denegado por cualquier motivo (ej: falta de antigüedad o saldo).
     * @throws DiasInsuficientesException Si el empleado regular solicita un total de más de 20 días de vacaciones en el año.
     */
    boolean solicitarPermiso(LocalDate fechaInicio,
                             LocalDate fechaFin,
                             String tipoPermiso,
                             String codigoEmpleado)
            throws PermisoNoConcedidoException, DiasInsuficientesException;

    /**
     * Consulta la cantidad de días de vacaciones disponibles para un empleado.
     * @param codigoEmpleado El identificador único del empleado.
     * @return El número de días de vacaciones disponibles.
     */
    int consultarDiasVacacionesDisponibles(String codigoEmpleado);

    /**
     * Valida si un empleado tiene la antigüedad mínima requerida para solicitar vacaciones.
     * En Paraguay, se requiere un año de servicio (Art. 44 - Ley N° 213/93).
     * @param codigoEmpleado El identificador único del empleado.
     * @return true si tiene la antigüedad mínima.
     */
    boolean tieneAntiguedadParaVacaciones(String codigoEmpleado);

    // NOTA: El método 'procesarAprobacionGerencial' ha sido movido a la interfaz AprobadorGerencial.
}