package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.service.NominaService;
import py.edu.uc.jpasseratplp32025.util.NominaUtils;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.service.RemuneracionesService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nomina")
public class NominaController {

    private final NominaService nominaService;
    private final PersonaRepository personaRepository;
    private final RemuneracionesService remuneracionesService;

    @Autowired
    public NominaController(RemuneracionesService remuneracionesService, NominaService nominaService, PersonaRepository personaRepository) {
        this.remuneracionesService = remuneracionesService;
        this.nominaService = nominaService;
        this.personaRepository = personaRepository;
    }

    /**
    * Endpoint para generar un reporte completo de todos los tipos de empleados
    * demostrando polimorfismo.
    * Endpoint: GET /api/nomina/reporte
    * @return Una lista de Strings que conforman el reporte.
    */
    @GetMapping("/reporte")
    public ResponseEntity<List<String>> generarReporte() {
        List<String> reporte = nominaService.generarReporteCompleto();

        if (reporte.size() <= 3) {
            // 3 son las líneas de cabecera y cierre si no hay datos.
            return new ResponseEntity<>(reporte, HttpStatus.NO_CONTENT);
        }
        
        return new ResponseEntity<>(reporte, HttpStatus.OK);
    }

    /**
     * Genera reporte de empleados con más de X días disponibles.
     * Reemplaza la llamada con lambda por el método específico:
    * NominaUtils.generarReporteJsonPorDiasDisponibles(empleados, umbral).
     * Endpoint: GET /api/nomina/empleados-dias?umbral=0
     * @param umbral Número mínimo de días disponibles para incluir en el reporte (default: 0)
     * @return JSON con empleados filtrados
     */
    @GetMapping("/empleados-dias")
    public ResponseEntity<?> getEmpleadosPorDias(@RequestParam(defaultValue = "0") int umbral) {
        List<PersonaJpa> empleados = personaRepository.findAll();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        // Se usa la función específica para días DISPONIBLES.
        String reporteJson = NominaUtils.generarReporteJsonPorDiasDisponibles(empleados, umbral);

        return ResponseEntity.ok(reporteJson);
    }

    /**
    * Obtiene el total de días de vacaciones disponibles para todos los empleados.
    * Reemplaza la llamada con lambda por el método específico:
    * NominaUtils.totalDiasVacacionesDisponibles(empleados).
    * Endpoint: GET /api/nomina/total-dias
    * @return Map con el total de días disponibles
    */
    @GetMapping("/total-dias")
    public ResponseEntity<Map<String, Integer>> getTotalDiasDisponibles() { // Renombrado para claridad
        List<PersonaJpa> empleados = personaRepository.findAll();

        // Se usa la función específica para total de días DISPONIBLES.
        int total = NominaUtils.totalDiasVacacionesDisponibles(empleados);

        Map<String, Integer> response = new HashMap<>();
        response.put("totalDiasVacacionesDisponibles", total);
        response.put("totalEmpleados", empleados.size());

        return ResponseEntity.ok(response);
    }

    /**
    * Obtiene un reporte de días de vacaciones y permisos (estadísticas).
    * Endpoint: GET /api/nomina/reporte-dias
    * @return JSON con el reporte de días (estadísticas)
    */
    @GetMapping("/reporte-dias")
    public ResponseEntity<String> getReporteDias() {
        // SOLUCIÓN: Usar el método existente en RemuneracionesService que devuelve un String (JSON)
        // y ajustar el tipo de retorno del controlador a ResponseEntity<String>.
        return ResponseEntity.ok(remuneracionesService.generarEstadisticasVacaciones());
    }
}