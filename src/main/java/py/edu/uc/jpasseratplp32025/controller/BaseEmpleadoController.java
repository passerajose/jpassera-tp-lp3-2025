package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;

public abstract class BaseEmpleadoController<T extends PersonaJpa> {

    /**
     * Endpoint para solicitar un permiso.
     * Ya no contiene bloques try-catch, permitiendo que las excepciones
     * (DiasInsuficientesException, PermisoNoConcedidoException, etc.)
     * se propaguen automáticamente al GlobalExceptionHandler para un manejo unificado.
     * * @param id ID del empleado
     * @param solicitud DTO con los detalles de la solicitud de permiso
     * @return ResponseEntity con la respuesta del procesamiento (ej. 200 OK)
     * @throws DiasInsuficientesException si no tiene días suficientes
     * @throws PermisoNoConcedidoException si el permiso es denegado por otra razón
     */
    @PostMapping("/{id}/permisos")
    public ResponseEntity<?> solicitarPermiso(
            @PathVariable Long id,
            @RequestBody SolicitudPermisoDto solicitud)
            throws DiasInsuficientesException, PermisoNoConcedidoException {

        // El método procesarSolicitudPermiso ahora lanza la excepción directamente.
        return procesarSolicitudPermiso(id, solicitud);
    }

    // Método abstracto que cada controlador específico debe implementar
    protected abstract ResponseEntity<?> procesarSolicitudPermiso(
            Long id, SolicitudPermisoDto solicitud)
            throws DiasInsuficientesException, PermisoNoConcedidoException;
}