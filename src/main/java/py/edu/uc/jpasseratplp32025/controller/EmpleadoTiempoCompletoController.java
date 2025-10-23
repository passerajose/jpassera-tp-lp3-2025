package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;
import py.edu.uc.jpasseratplp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoTiempoCompletoImpuestoDto; // Importación del DTO

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; // Importación necesaria

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoTiempoCompletoController {

    @Autowired
    private EmpleadoTiempoCompletoService service;

    // GET /api/empleados
    @GetMapping
    public ResponseEntity<List<EmpleadoTiempoCompleto>> getAllEmpleados() {
        List<EmpleadoTiempoCompleto> empleados = service.findAll();
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    // GET /api/empleados/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoTiempoCompleto> getEmpleadoById(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> new ResponseEntity<>(empleado, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // POST /api/empleados (Creación unitaria)
    @PostMapping
    public ResponseEntity<EmpleadoTiempoCompleto> createEmpleado(@RequestBody EmpleadoTiempoCompleto empleado) {
        try {
            // El servicio maneja la validación y el guardado
            EmpleadoTiempoCompleto saved = service.save(empleado);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            // Captura las excepciones de validación lanzadas por el servicio/entidad
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // PUT /api/empleados/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmpleado(@PathVariable Long id, @RequestBody EmpleadoTiempoCompleto empleadoDetails) {
        return service.findById(id)
                .map(existingEmpleado -> {
                    // Actualizar campos (el servicio validará antes de guardar)
                    existingEmpleado.setNombre(empleadoDetails.getNombre());
                    existingEmpleado.setApellido(empleadoDetails.getApellido());
                    existingEmpleado.setFechaDeNacimiento(empleadoDetails.getFechaDeNacimiento());
                    existingEmpleado.setNumeroDeCedula(empleadoDetails.getNumeroDeCedula());
                    existingEmpleado.setSalarioMensual(empleadoDetails.getSalarioMensual());
                    existingEmpleado.setDepartamento(empleadoDetails.getDepartamento());

                    try {
                        EmpleadoTiempoCompleto updatedEmpleado = service.save(existingEmpleado);
                        return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
                    } catch (IllegalArgumentException e) {
                        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
                    }
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE /api/empleados/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // GET /api/empleados/{id}/salario-neto
    @GetMapping("/{id}/salario-neto")
    public ResponseEntity<BigDecimal> getSalarioNeto(@PathVariable Long id) {
        return service.calcularSalarioNeto(id)
                .map(salarioNeto -> new ResponseEntity<>(salarioNeto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET /api/empleados/{id}/impuestos
    @GetMapping("/{id}/impuestos")
    public ResponseEntity<EmpleadoTiempoCompletoImpuestoDto> getImpuestos(@PathVariable Long id) {
        return service.findById(id)
                .map(empleado -> {
                    BigDecimal montoImpuesto = empleado.calcularImpuestos();
                    boolean esValido = empleado.validarDatosEspecificos();
                    String infoCompleta = empleado.obtenerInformacionCompleta();

                    EmpleadoTiempoCompletoImpuestoDto dto = new EmpleadoTiempoCompletoImpuestoDto(
                        empleado.getId(), 
                        montoImpuesto, 
                        esValido,
                        infoCompleta
                    );
                    return new ResponseEntity<>(dto, HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // GET /api/empleados/departamento?nombre=X
    @GetMapping("/departamento")
    public ResponseEntity<List<EmpleadoTiempoCompleto>> getEmpleadosPorDepartamento(@RequestParam(name = "nombre") String departamento) {
        List<EmpleadoTiempoCompleto> empleados = service.buscarPorDepartamento(departamento);
        if (empleados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    // -------------------------------------------------------------------
    // NUEVO SERVICIO REST: Persistencia en Batch
    // -------------------------------------------------------------------
    
    /**
     * Servicio REST para persistir una lista de empleados en lotes (batch).
     * Endpoint: POST /api/empleados/batch
     * @param empleados Lista de EmpleadoTiempoCompleto a persistir.
     * @return Lista de empleados guardados o 400 si falla alguna validación.
     */
    @PostMapping("/batch")
    public ResponseEntity<?> createEmpleadosBatch(@RequestBody List<EmpleadoTiempoCompleto> empleados) {
        try {
            // El servicio maneja la división en lotes (chunks) y la validación polimórfica
            List<EmpleadoTiempoCompleto> savedEmpleados = service.guardarEmpleadosEnBatch(empleados);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmpleados);
        } catch (IllegalArgumentException e) {
            // Devuelve un mensaje de error específico si alguna entidad falla la validación
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la carga masiva: " + e.getMessage());
        } catch (Exception e) {
            // Manejo de otros errores (p. ej., problemas de conexión a DB, JSON malformado)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno durante la carga en lote: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------
    // NUEVO ENDPOINT: CÁLCULO DE NÓMINA TOTAL POR TIPO
    // -------------------------------------------------------------------

    /**
     * Servicio REST que retorna la suma total de salarios brutos (Nómina)
     * para el tipo EmpleadoTiempoCompleto.
     * Endpoint: GET /api/empleados/nomina-total
     * @return Map<String, BigDecimal> con el tipo de empleado y la suma total de salarios.
     */
    @GetMapping("/nomina-total")
    public ResponseEntity<Map<String, BigDecimal>> getNominaTotal() {
        // Llama al nuevo método del servicio
        Map<String, BigDecimal> nomina = service.calcularNominaTotal();

        if (nomina.isEmpty() || nomina.values().stream().allMatch(BigDecimal.ZERO::equals)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(nomina, HttpStatus.OK);
    }
}