package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.util.NominaUtils; // Asegúrate de que esta clase sea NominaUtils
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Servicio encargado de la gestión y generación de reportes avanzados de remuneraciones,
 * utilizando PersonaRepository y métodos estáticos de NominaUtils.
 */
@Service
public class RemuneracionesService {

    private static final Logger log = LoggerFactory.getLogger(RemuneracionesService.class);

    private final PersonaRepository personaRepository;

    @Autowired
    public RemuneracionesService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
        log.info("RemuneracionesService inicializado.");
    }

    /**
     * Calcula y retorna el total consolidado de días de vacaciones solicitados
     * por todos los empleados.
     * * @return El número total de días solicitados.
     */
    public int obtenerTotalDiasVacacionesSolicitados() {
        log.debug("Obteniendo total de días solicitados...");
        List<PersonaJpa> todos = personaRepository.findAll();

        // CORRECCIÓN: Se usa la firma existente en NominaUtils: totalDiasVacacionesSolicitados(List)
        int total = NominaUtils.totalDiasVacacionesSolicitados(todos);
        
        log.info("Total de días de vacaciones solicitados: {}", total);
        return total;
    }

    /**
     * Genera un reporte JSON de los empleados que han solicitado más de un número
     * de días especificado (umbral).
     * * @param umbral El número mínimo de días solicitados para ser incluidos en el reporte.
     * @return Un String JSON formateado con el reporte.
     */
    public String generarReporteDiasSolicitadosPorUmbral(int umbral) {
        log.debug("Generando reporte JSON para días solicitados por encima del umbral: {}", umbral);
        List<PersonaJpa> todos = personaRepository.findAll();

        // CORRECCIÓN: Se usa la firma existente en NominaUtils: generarReporteJsonPorDiasSolicitados(List, int)
        // Se asume que el método que intenta usar los días solicitados es este.
        String reporteJson = NominaUtils.generarReporteJsonPorDiasSolicitados(todos, umbral);
        
        log.info("Reporte JSON de días solicitados generado con éxito.");
        return reporteJson;
    }

    // ========================================================================
    // OTROS MÉTODOS EXISTENTES PARA DEMOSTRACIÓN
    // ========================================================================

    /**
     * Genera un reporte completo de nómina en formato JSON.
     * @return JSON con información detallada de nómina y vacaciones.
     */
    public String generarReporteCompletoNomina() {
        log.debug("Generando reporte completo de nómina...");
        List<PersonaJpa> todos = personaRepository.findAll();
        return NominaUtils.generarReporteCompleto(todos);
    }
    
    /**
     * Genera estadísticas de días disponibles en formato JSON.
     * @return JSON con estadísticas de vacaciones.
     */
    public String generarEstadisticasVacaciones() {
        log.debug("Generando estadísticas de vacaciones...");
        List<PersonaJpa> todos = personaRepository.findAll();
        return NominaUtils.generarEstadisticasVacaciones(todos);
    }
}