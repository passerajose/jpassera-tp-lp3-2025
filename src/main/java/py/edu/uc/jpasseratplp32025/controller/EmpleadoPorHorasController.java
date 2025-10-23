package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;
import py.edu.uc.jpasseratplp32025.service.EmpleadoPorHorasService;

import java.util.List;

@RestController
@RequestMapping("/api/empleados-por-hora")
public class EmpleadoPorHorasController {

    private final EmpleadoPorHorasService service;

    @Autowired
    public EmpleadoPorHorasController(EmpleadoPorHorasService service) {
        this.service = service;
    }

    // POST /api/empleados-por-hora
    @PostMapping
    public ResponseEntity<EmpleadoPorHora> create(@RequestBody EmpleadoPorHora empleado) {
        try {
            EmpleadoPorHora savedEmpleado = service.save(empleado);
            return new ResponseEntity<>(savedEmpleado, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // GET /api/empleados-por-hora
    @GetMapping
    public ResponseEntity<List<EmpleadoPorHora>> findAll() {
        List<EmpleadoPorHora> empleados = service.findAll();
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    // GET /api/empleados-por-hora/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoPorHora> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> new ResponseEntity<>(empleado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE /api/empleados-por-hora/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT /api/empleados-por-hora/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmpleadoPorHora empleadoDetails) {
        // NOTA: El tipo de retorno ahora es ResponseEntity<?>
        return service.findById(id)
                .map(existingEmpleado -> {
                    empleadoDetails.setId(id); // Asegura que el ID sea correcto para la actualización
                    try {
                        EmpleadoPorHora updatedEmpleado = service.save(empleadoDetails);
                        // Retorna 200 OK con el objeto EmpleadoPorHora
                        return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
                    } catch (IllegalArgumentException e) {
                        // Retorna 400 Bad Request con un mensaje de error (String)
                        return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
                    }
                })
                // Retorna 404 Not Found
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // CONSULTA ESPECÍFICA: GET /api/empleados-por-hora/consulta?horas=40
    @GetMapping("/consulta")
    public ResponseEntity<?> getByHorasTrabajadasGreaterThan(@RequestParam(name = "horas") Integer horas) {
        try {
            List<EmpleadoPorHora> empleados = service.buscarConMasDeHoras(horas);
            if (empleados.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(empleados, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}