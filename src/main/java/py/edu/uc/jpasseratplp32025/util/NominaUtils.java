package py.edu.uc.jpasseratplp32025.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.entity.Empleado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase utilitaria para operaciones relacionadas con nómina y cálculo de vacaciones.
 * Proporciona métodos estáticos para generar reportes y estadísticas de empleados.
 * * NOTA: Asume la existencia del método int consultarDiasVacacionesSolicitados() en la interfaz Empleado.
 */
public class NominaUtils {
    private static final Logger log = LoggerFactory.getLogger(NominaUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Calcula el total de días de vacaciones disponibles para todos los empleados.
     * * @param personas Lista de todas las personas
     * @return Total de días disponibles sumados
     */
    public static int totalDiasVacacionesDisponibles(List<? extends PersonaJpa> personas) {
        log.debug("Calculando total de días de vacaciones disponibles");
        
        int total = personas.stream()
                .filter(p -> p instanceof Empleado)
                .mapToInt(p -> {
                    Empleado emp = (Empleado) p;
                    // Se asume que este método consulta los días disponibles
                    return emp.consultarDiasVacacionesDisponibles(emp.getNumeroDeCedula());
                })
                .sum();
        
        log.info("Total de días de vacaciones disponibles: {}", total);
        return total;
    }
    
    /**
     * Calcula el total de días de vacaciones SOLICITADOS por todos los empleados.
     * * @param personas Lista de todas las personas
     * @return Total de días solicitados sumados
     */
    public static int totalDiasVacacionesSolicitados(List<? extends PersonaJpa> personas) {
        log.debug("Calculando total de días de vacaciones solicitados");
        
        int total = personas.stream()
                .filter(p -> p instanceof Empleado)
                .mapToInt(p -> {
                    Empleado emp = (Empleado) p;
                    // Asume la existencia de este método en la interfaz Empleado
                    return emp.consultarDiasVacacionesSolicitados();
                })
                .sum();
        
        log.info("Total de días de vacaciones solicitados: {}", total);
        return total;
    }

    /**
     * Genera un reporte JSON con empleados que tienen más de X días DISPONIBLES.
     *
     * @param personas Lista de todas las personas
     * @param umbral Número mínimo de días para incluir en el reporte
     * @return JSON formateado con el reporte
     */
    public static String generarReporteJsonPorDiasDisponibles(List<? extends PersonaJpa> personas, int umbral) {
        log.info("Generando reporte de empleados con más de {} días disponibles", umbral);
        
        ArrayNode arrayNode = MAPPER.createArrayNode();

        List<Empleado> empleadosFiltrados = personas.stream()
                .filter(p -> p instanceof Empleado)
                .map(p -> (Empleado) p)
                .filter(emp -> emp.consultarDiasVacacionesDisponibles(emp.getNumeroDeCedula()) > umbral)
                .collect(Collectors.toList());

        log.debug("Empleados encontrados con más de {} días disponibles: {}", umbral, empleadosFiltrados.size());

        for (Empleado empleado : empleadosFiltrados) {
            ObjectNode empleadoNode = MAPPER.createObjectNode();
            int diasDisponibles = empleado.consultarDiasVacacionesDisponibles(empleado.getNumeroDeCedula());

            empleadoNode.put("id", empleado.getId());
            empleadoNode.put("nombre", empleado.getNombre());
            empleadoNode.put("apellido", empleado.getApellido());
            empleadoNode.put("numeroDeCedula", empleado.getNumeroDeCedula());
            empleadoNode.put("diasVacacionesDisponibles", diasDisponibles);
            empleadoNode.put("tipoEmpleado", empleado.getClass().getSimpleName());
            empleadoNode.put("informacionCompleta", empleado.obtenerInformacionCompleta());

            arrayNode.add(empleadoNode);
        }

        log.info("Reporte de Días Disponibles generado con {} empleados", arrayNode.size());
        return arrayNode.toPrettyString();
    }
    
    /**
     * Genera un reporte JSON con empleados que han SOLICITADO más de X días.
     *
     * @param personas Lista de todas las personas
     * @param umbral Número mínimo de días solicitados para incluir en el reporte
     * @return JSON formateado con el reporte
     */
    public static String generarReporteJsonPorDiasSolicitados(List<? extends PersonaJpa> personas, int umbral) {
        log.info("Generando reporte de empleados con más de {} días solicitados", umbral);
        
        ArrayNode arrayNode = MAPPER.createArrayNode();

        List<Empleado> empleadosFiltrados = personas.stream()
                .filter(p -> p instanceof Empleado)
                .map(p -> (Empleado) p)
                // Asume la existencia de consultarDiasVacacionesSolicitados()
                .filter(emp -> emp.consultarDiasVacacionesSolicitados() > umbral) 
                .collect(Collectors.toList());

        log.debug("Empleados encontrados con más de {} días solicitados: {}", umbral, empleadosFiltrados.size());

        for (Empleado empleado : empleadosFiltrados) {
            ObjectNode empleadoNode = MAPPER.createObjectNode();
            int diasSolicitados = empleado.consultarDiasVacacionesSolicitados(); // Días solicitados

            empleadoNode.put("id", empleado.getId());
            empleadoNode.put("nombre", empleado.getNombre());
            empleadoNode.put("apellido", empleado.getApellido());
            empleadoNode.put("numeroDeCedula", empleado.getNumeroDeCedula());
            empleadoNode.put("diasVacacionesSolicitados", diasSolicitados);
            empleadoNode.put("tipoEmpleado", empleado.getClass().getSimpleName());
            empleadoNode.put("informacionCompleta", empleado.obtenerInformacionCompleta());

            arrayNode.add(empleadoNode);
        }

        log.info("Reporte de Días Solicitados generado con {} empleados", arrayNode.size());
        return arrayNode.toPrettyString();
    }


    /**
     * Calcula estadísticas de vacaciones para todos los empleados (días DISPONIBLES).
     * Incluye: total, promedio, máximo, mínimo de días disponibles.
     *
     * @param personas Lista de todas las personas
     * @return JSON con estadísticas
     */
    public static String generarEstadisticasVacaciones(List<? extends PersonaJpa> personas) {
        log.info("Generando estadísticas de vacaciones (Días Disponibles)");
        
        ObjectNode estadisticas = MAPPER.createObjectNode();

        List<Empleado> empleados = personas.stream()
                .filter(p -> p instanceof Empleado)
                .map(p -> (Empleado) p)
                .collect(Collectors.toList());

        if (empleados.isEmpty()) {
            log.warn("No hay empleados para generar estadísticas");
            estadisticas.put("mensaje", "No hay empleados registrados");
            return estadisticas.toPrettyString();
        }

        int totalEmpleados = empleados.size();
        int totalDias = empleados.stream()
                .mapToInt(e -> e.consultarDiasVacacionesDisponibles(e.getNumeroDeCedula()))
                .sum();

        double promedioDias = (double) totalDias / totalEmpleados;

        int maxDias = empleados.stream()
                .mapToInt(e -> e.consultarDiasVacacionesDisponibles(e.getNumeroDeCedula()))
                .max()
                .orElse(0);

        int minDias = empleados.stream()
                .mapToInt(e -> e.consultarDiasVacacionesDisponibles(e.getNumeroDeCedula()))
                .min()
                .orElse(0);

        estadisticas.put("totalEmpleados", totalEmpleados);
        estadisticas.put("totalDiasDisponibles", totalDias);
        estadisticas.put("promedioDiasDisponibles", Math.round(promedioDias * 100.0) / 100.0);
        estadisticas.put("maximoDiasDisponibles", maxDias);
        estadisticas.put("minimoDiasDisponibles", minDias);

        log.info("Estadísticas generadas: {} empleados, {} días totales, promedio: {}", 
                totalEmpleados, totalDias, promedioDias);

        return estadisticas.toPrettyString();
    }

    /**
     * Genera un reporte completo de nómina con información detallada de cada empleado.
     * Incluye todos los empleados y sus días de vacaciones disponibles y solicitados.
     *
     * @param personas Lista de todas las personas
     * @return JSON con reporte completo
     */
    public static String generarReporteCompleto(List<? extends PersonaJpa> personas) {
        log.info("Generando reporte completo de nómina");
        
        ObjectNode reporteCompleto = MAPPER.createObjectNode();
        ArrayNode empleadosArray = MAPPER.createArrayNode();

        List<Empleado> empleados = personas.stream()
                .filter(p -> p instanceof Empleado)
                .map(p -> (Empleado) p)
                .collect(Collectors.toList());

        for (Empleado empleado : empleados) {
            ObjectNode empleadoNode = MAPPER.createObjectNode();
            int diasDisponibles = empleado.consultarDiasVacacionesDisponibles(empleado.getNumeroDeCedula());
            int diasSolicitados = empleado.consultarDiasVacacionesSolicitados(); // Nuevo campo

            empleadoNode.put("id", empleado.getId());
            empleadoNode.put("nombre", empleado.getNombre());
            empleadoNode.put("apellido", empleado.getApellido());
            empleadoNode.put("numeroDeCedula", empleado.getNumeroDeCedula());
            empleadoNode.put("tipoEmpleado", empleado.getClass().getSimpleName());
            empleadoNode.put("diasVacacionesDisponibles", diasDisponibles);
            empleadoNode.put("diasVacacionesSolicitados", diasSolicitados); // Se añade el campo solicitado
            empleadoNode.put("informacionCompleta", empleado.obtenerInformacionCompleta());

            empleadosArray.add(empleadoNode);
        }

        int totalDiasDisponibles = NominaUtils.totalDiasVacacionesDisponibles(personas);
        int totalDiasSolicitados = NominaUtils.totalDiasVacacionesSolicitados(personas);

        reporteCompleto.put("fechaGeneracion", java.time.LocalDateTime.now().toString());
        reporteCompleto.put("totalEmpleados", empleados.size());
        reporteCompleto.put("totalDiasDisponibles", totalDiasDisponibles);
        reporteCompleto.put("totalDiasSolicitados", totalDiasSolicitados); // Se añade el total solicitado
        reporteCompleto.set("empleados", empleadosArray);

        log.info("Reporte completo generado con {} empleados", empleados.size());
        return reporteCompleto.toPrettyString();
    }
}