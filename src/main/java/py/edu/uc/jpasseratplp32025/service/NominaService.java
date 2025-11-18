package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.Empleado;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class NominaService {

    private static final Logger log = LoggerFactory.getLogger(NominaService.class);

    // Asumimos que PersonaRepository extiende JpaRepository<PersonaJpa, Long>
    // y tiene definido el método findByNumeroDeCedula(String) que devuelve directamente PersonaJpa
    private final PersonaRepository personaRepository;

    @Autowired
    public NominaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
        log.info("NominaService inicializado para gestión centralizada de reportes.");
    }

    // ========================================================================
    // Solicitud de Días de Vacaciones/Permisos
    // ========================================================================
    /**
     * Procesa la solicitud de días (vacaciones o permisos) para un empleado.
     * @param cedula Cédula del empleado.
     * @param tipoDia Tipo de solicitud ("VACACION" o "PERMISO").
     * @param fechaInicio Fecha de inicio de la solicitud.
     * @param fechaFin Fecha de fin de la solicitud.
     * @throws EmpleadoNoEncontradoException Si el empleado no existe.
     * @throws DiasInsuficientesException Si no hay días suficientes disponibles.
     * @throws PermisoNoConcedidoException Si la solicitud de permiso es rechazada.
     * @throws IllegalArgumentException Si el tipo de día o el rango de fechas es inválido.
     */
    public void solicitarDias(String cedula, String tipoDia, LocalDate fechaInicio, LocalDate fechaFin)
            throws EmpleadoNoEncontradoException, DiasInsuficientesException, PermisoNoConcedidoException {

        // 1. Buscar el empleado por cédula
        PersonaJpa empleado = personaRepository.findByNumeroDeCedula(cedula);

        if (empleado == null) {
            log.warn("Solicitud fallida: Empleado con CI {} no encontrado.", cedula);
            throw new EmpleadoNoEncontradoException("No se encontró ningún empleado con la cédula: " + cedula);
        }

        // El empleado debe ser un Empleado (o subclase) para tener la funcionalidad Permisionable
        if (!(empleado instanceof Empleado)) {
            throw new IllegalArgumentException("El empleado encontrado no es un tipo de empleado que pueda solicitar días.");
        }

        Empleado empleadoPermisionable = (Empleado) empleado;

        // 2. Lógica de Delegación y Validación (Polimorfismo)
        // Delegamos la validación de Antigüedad, Saldo y la Regla de 20 Días a la clase del empleado (Empleado o Gerente).

        if ("VACACION".equalsIgnoreCase(tipoDia)) {

            // El método solicitarPermiso en Empleado/Gerente gestionará:
            // - Cálculo de días solicitados
            // - Validación de Antigüedad
            // - Validación de Saldo de Días (lanza PermisoNoConcedidoException)
            // - Validación de Límite de 20 Días (lanza DiasInsuficientesException)

            // Nota: Pasar la cédula (codigoEmpleado) es redundante ya que el objeto tiene su propio estado,
            // pero mantenemos la firma original.
            boolean aprobado = empleadoPermisionable.solicitarPermiso(
                    fechaInicio,
                    fechaFin,
                    tipoDia,
                    cedula
            );

            if (aprobado) {
                log.info("Solicitud de vacaciones registrada y saldo debitado (Polimorfismo). Empleado: {}", empleado.getNombre());
            }

        } else if ("PERMISO".equalsIgnoreCase(tipoDia)) {

            // SIMULACIÓN DE REGLA DE NEGOCIO PARA PERMISOS (Se mantiene la lógica original del servicio,
            // ya que el método solicitarPermiso de la entidad solo maneja VACACIONES y MATRIMONIO)
            if (empleado.getNombre().toUpperCase().contains("J")) {
                log.error("Permiso denegado para {}. Regla de negocio: Nombre contiene 'J'.", empleado.getNombre());

                throw new PermisoNoConcedidoException(
                        "Solicitud de permiso rechazada.",
                        "El permiso ha sido denegado por políticas internas o falta de aprobación gerencial (Regla de nombre con 'J')."
                );
            }

            // Lógica para otros permisos (si no falló la regla 'J')
            long diasSolicitados = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            if (diasSolicitados <= 0) {
                throw new IllegalArgumentException("El rango de fechas es inválido. Asegúrese de que la fecha de fin es posterior o igual a la de inicio.");
            }

            // Si la validación pasa, aquí se registraría la solicitud de permiso.
            log.info("Permiso registrado. Empleado: {}. Fechas: {} a {}", empleado.getNombre(), fechaInicio, fechaFin);

        } else {
            throw new IllegalArgumentException("Tipo de día no válido. Use 'VACACION' o 'PERMISO'.");
        }
    }

    // ========================================================================
    // DEMOSTRACIÓN DE POLIMORFISMO (Método existente)
    // ========================================================================

    /**
     * Genera un reporte completo de todos los empleados utilizando polimorfismo.
     * @return Una lista de Strings con el reporte detallado.
     */
    public List<String> generarReporteCompleto() {
        log.info("Iniciando la generación del reporte completo de nómina.");

        // Obtener todas las personas de la tabla única.
        List<PersonaJpa> todasLasPersonas = personaRepository.findAll();
        List<String> reporte = new ArrayList<>();

        reporte.add("=====================================================================================================");
        reporte.add("                   REPORTE POLIMÓRFICO DE NÓMINA Y VALIDACIONES");
        reporte.add("=====================================================================================================");

        int totalPersonas = todasLasPersonas.size();
        if (totalPersonas == 0) {
            reporte.add("La base de datos no contiene registros de empleados.");
            return reporte;
        }

        for (PersonaJpa persona : todasLasPersonas) {
            String tipoClase = persona.getClass().getSimpleName();
            String ci = persona.getNumeroDeCedula();

            reporte.add("-----------------------------------------------------------------------------------------------------");
            reporte.add(String.format("Persona: %s (C.I.: %s, Tipo: %s)", persona.getNombre(), ci, tipoClase));

            // 1. Llamar obtenerInformacionCompleta() (Polimorfismo en acción)
            String info = persona.obtenerInformacionCompleta();
            reporte.add(String.format("  [INFO COMPLETA]: %s", info));

            // 2. Validar datos específicos (Polimorfismo en acción)
            boolean esValido = persona.validarDatosEspecificos();
            reporte.add(String.format("  [VALIDACIÓN DE DATOS]: %s", esValido ? "✅ OK" : "❌ FALLÓ (Reglas específicas no cumplidas)"));

            // 3. Calcular impuestos (Polimorfismo en acción)
            try {
                BigDecimal impuestos = persona.calcularImpuestos();
                reporte.add(String.format("  [IMPUESTOS CALCULADOS]: %s", (impuestos != null ? impuestos.setScale(2, BigDecimal.ROUND_HALF_UP).toString() : "N/A (No aplica o error de cálculo)")));
            } catch (UnsupportedOperationException e) {
                reporte.add("  [IMPUESTOS CALCULADOS]: N/A (Método no implementado o no aplica para este tipo)");
            } catch (Exception e) {
                reporte.add("  [IMPUESTOS CALCULADOS]: ERROR al calcular: " + e.getMessage());
            }

            log.info("Reporte generado para {} con C.I. {}. Validez: {}", tipoClase, ci, esValido);
        }
        reporte.add("=====================================================================================================");
        return reporte;
    }
}