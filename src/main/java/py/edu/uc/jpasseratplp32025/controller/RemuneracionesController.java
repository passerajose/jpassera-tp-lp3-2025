package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.service.RemuneracionesService;
import py.edu.uc.jpasseratplp32025.util.NominaUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/remuneraciones")
public class RemuneracionesController {

    private final RemuneracionesService remuneracionesService;
    private final PersonaRepository personaRepository;

    @Autowired
    public RemuneracionesController(RemuneracionesService remuneracionesService,
                                   PersonaRepository personaRepository) {
        this.remuneracionesService = remuneracionesService;
        this.personaRepository = personaRepository;
    }

    /**
     * Obtiene el total de días de vacaciones disponibles para todos los empleados.
     * Endpoint: GET /api/remuneraciones/total-dias-disponibles
     * @return Map con el total de días disponibles
     */
    @GetMapping("/total-dias-disponibles")
    public ResponseEntity<Map<String, Integer>> getTotalDiasVacacionesDisponibles() {
        List<PersonaJpa> empleados = personaRepository.findAll();
        // Se utiliza el nuevo nombre del método, que calcula los días disponibles
        int totalDias = NominaUtils.totalDiasVacacionesDisponibles(empleados); 

        Map<String, Integer> response = new HashMap<>();
        response.put("totalDiasVacacionesDisponibles", totalDias);
        response.put("totalEmpleados", empleados.size());

        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene el total de días de vacaciones SOLICITADOS por todos los empleados.
     * Endpoint: GET /api/remuneraciones/total-dias-solicitados
     * @return Map con el total de días solicitados
     */
    @GetMapping("/total-dias-solicitados")
    public ResponseEntity<Map<String, Integer>> getTotalDiasVacacionesSolicitados() {
        List<PersonaJpa> empleados = personaRepository.findAll();
        // Nuevo método para obtener días solicitados
        int totalDiasSolicitados = NominaUtils.totalDiasVacacionesSolicitados(empleados); 

        Map<String, Integer> response = new HashMap<>();
        response.put("totalDiasVacacionesSolicitados", totalDiasSolicitados);
        response.put("totalEmpleados", empleados.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Genera reporte de empleados con más de X días disponibles.
     * Endpoint: GET /api/remuneraciones/empleados-dias-disponibles?umbral=10
     * @param umbral Número mínimo de días para incluir en el reporte (default: 0)
     * @return JSON con empleados filtrados por umbral de días
     */
    @GetMapping("/empleados-dias-disponibles")
    public ResponseEntity<?> getEmpleadosPorDiasDisponibles(
            @RequestParam(name = "umbral", defaultValue = "0") int umbral) {

        List<PersonaJpa> empleados = personaRepository.findAll();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String reporteJson = NominaUtils.generarReporteJsonPorDiasDisponibles(empleados, umbral);

        return ResponseEntity.ok(reporteJson);
    }
    
    /**
     * Genera reporte de empleados que han SOLICITADO más de X días.
     * Endpoint: GET /api/remuneraciones/empleados-dias-solicitados?umbral=5
     * @param umbral Número mínimo de días solicitados para incluir en el reporte (default: 0)
     * @return JSON con empleados filtrados por umbral de días solicitados
     */
    @GetMapping("/empleados-dias-solicitados")
    public ResponseEntity<?> getEmpleadosPorDiasSolicitados(
            @RequestParam(name = "umbral", defaultValue = "0") int umbral) {

        List<PersonaJpa> empleados = personaRepository.findAll();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String reporteJson = NominaUtils.generarReporteJsonPorDiasSolicitados(empleados, umbral);

        return ResponseEntity.ok(reporteJson);
    }

    /**
     * Genera estadísticas de vacaciones de todos los empleados.
     * Endpoint: GET /api/remuneraciones/estadisticas-vacaciones
     * @return JSON con estadísticas completas de vacaciones
     */
    @GetMapping("/estadisticas-vacaciones")
    public ResponseEntity<?> getEstadisticasVacaciones() {
        List<PersonaJpa> empleados = personaRepository.findAll();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String estadisticas = NominaUtils.generarEstadisticasVacaciones(empleados);

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Genera un reporte completo de nómina con días de vacaciones y permisos.
     * Endpoint: GET /api/remuneraciones/reporte-completo
     * @return JSON con reporte completo de nómina
     */
    @GetMapping("/reporte-completo")
    public ResponseEntity<?> generarReporteCompleto() {
        List<PersonaJpa> empleados = personaRepository.findAll();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        String reporte = NominaUtils.generarReporteCompleto(empleados);

        return ResponseEntity.ok(reporte);
    }
}