package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.jpasseratplp32025.service.NominaService;

import java.util.List;

@RestController
@RequestMapping("/api/nomina")
public class NominaController {

    private final NominaService nominaService;

    @Autowired
    public NominaController(NominaService nominaService) {
        this.nominaService = nominaService;
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
}