package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.service.RemuneracionesService;

import java.util.List;

@RestController
@RequestMapping("/api/remuneraciones")
public class RemuneracionesController {

    private final RemuneracionesService remuneracionesService;

    @Autowired
    public RemuneracionesController(RemuneracionesService remuneracionesService) {
        this.remuneracionesService = remuneracionesService;
    }

    /**
     * Obtiene una lista de todos los empleados de la jerarquía.
     * Endpoint: GET /api/remuneraciones/todos
     * @return Una lista polimórfica de todas las subclases de PersonaJpa.
     */
    @GetMapping("/todos")
    public ResponseEntity<List<EmpleadoDto>> obtenerTodosLosEmpleados() {
        List<EmpleadoDto> empleados = remuneracionesService.obtenerTodosLosEmpleados();

        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }
}