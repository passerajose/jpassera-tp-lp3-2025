package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException; // Importación necesaria
import py.edu.uc.jpasseratplp32025.service.EmpleadoTiempoCompletoService;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoTiempoCompletoImpuestoDto;
import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;

import java.math.BigDecimal;
import java.time.LocalDate; // Importación necesaria
import java.time.temporal.ChronoUnit; // Importación necesaria
import java.util.HashMap; // Importación necesaria
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoTiempoCompletoController extends BaseEmpleadoController<EmpleadoTiempoCompleto> {

    @Autowired
    private EmpleadoTiempoCompletoService service;

    // --- LÓGICA DE VALIDACIÓN ---
    private void validarFechaNacimiento(EmpleadoTiempoCompleto empleado) {
        if (empleado.getFechaDeNacimiento() != null && empleado.getFechaDeNacimiento().isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException("La fecha de nacimiento (" + empleado.getFechaDeNacimiento() + ") no puede ser posterior a la fecha actual.");
        }
    }
    // ----------------------------

    // GET /api/empleados
    @GetMapping
    public ResponseEntity<List<EmpleadoTiempoCompleto>> getAllEmpleados() {
        List<EmpleadoTiempoCompleto> empleados = service.findAll();
        return new ResponseEntity<>(empleados, HttpStatus.OK);
    }

    // GET /api/empleados/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoTiempoCompleto> getEmpleadoById(@PathVariable Long id) {
        // Se usa orElseThrow para lanzar EmpleadoNoEncontradoException (HTTP 404)
        return service.findById(id)
                .map(empleado -> new ResponseEntity<>(empleado, HttpStatus.OK))
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
    }

    // POST /api/empleados (Creación unitaria)
    @PostMapping
    public ResponseEntity<EmpleadoTiempoCompleto> createEmpleado(@RequestBody EmpleadoTiempoCompleto empleado) {
        // Se aplica validación y se elimina try-catch para que el Global Handler actúe
        validarFechaNacimiento(empleado);
        EmpleadoTiempoCompleto saved = service.save(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/empleados/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoTiempoCompleto> updateEmpleado(@PathVariable Long id, @RequestBody EmpleadoTiempoCompleto empleadoDetails) {
        // El tipo de retorno es EmpleadoTiempoCompleto, ya que las excepciones son manejadas globalmente
        return service.findById(id)
                .map(existingEmpleado -> {
                    // Actualizar campos
                    validarFechaNacimiento(empleadoDetails); // Aplicar validación aquí
                    
                    existingEmpleado.setNombre(empleadoDetails.getNombre());
                    existingEmpleado.setApellido(empleadoDetails.getApellido());
                    existingEmpleado.setFechaDeNacimiento(empleadoDetails.getFechaDeNacimiento());
                    existingEmpleado.setNumeroDeCedula(empleadoDetails.getNumeroDeCedula());
                    existingEmpleado.setSalarioMensual(empleadoDetails.getSalarioMensual());
                    existingEmpleado.setDepartamento(empleadoDetails.getDepartamento());

                    // Se elimina el try-catch de IllegalArgumentException, usando el Global Handler
                    EmpleadoTiempoCompleto updatedEmpleado = service.save(existingEmpleado);
                    return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
                })
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
    }

    // DELETE /api/empleados/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmpleado(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            throw new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id);
        }
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // GET /api/empleados/{id}/salario-neto
    @GetMapping("/{id}/salario-neto")
    public ResponseEntity<BigDecimal> getSalarioNeto(@PathVariable Long id) {
        // Se reemplaza orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)) por orElseThrow
        return service.calcularSalarioNeto(id)
                .map(salarioNeto -> new ResponseEntity<>(salarioNeto, HttpStatus.OK))
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
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
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
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

    // NUEVO SERVICIO REST: Persistencia en Batch
    @PostMapping("/batch")
    public ResponseEntity<?> createEmpleadosBatch(@RequestBody List<EmpleadoTiempoCompleto> empleados) {
        // Se aplica validación de fecha de nacimiento a cada empleado en el lote
        empleados.forEach(this::validarFechaNacimiento);
        
        try {
            List<EmpleadoTiempoCompleto> savedEmpleados = service.guardarEmpleadosEnBatch(empleados);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEmpleados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en la carga masiva: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno durante la carga en lote: " + e.getMessage());
        }
    }

    // NUEVO ENDPOINT: CÁLCULO DE NÓMINA TOTAL POR TIPO
    @GetMapping("/nomina-total")
    public ResponseEntity<Map<String, BigDecimal>> getNominaTotal() {
        Map<String, BigDecimal> nomina = service.calcularNominaTotal();

        if (nomina.isEmpty() || nomina.values().stream().allMatch(BigDecimal.ZERO::equals)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(nomina, HttpStatus.OK);
    }

    // CONSULTA ESPECÍFICA: GET /api/empleados/vigentes
    @GetMapping("/vigentes")
    public ResponseEntity<List<EmpleadoTiempoCompleto>> getContratosVigentes() {
        List<EmpleadoTiempoCompleto> empleadosTiempoCompleto = service.buscarContratosVigentes();
        if (empleadosTiempoCompleto.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleadosTiempoCompleto, HttpStatus.OK);
    }

    // Procesamiento de Solicitud de Permiso (FIX de Persistencia)
    @Override
    protected ResponseEntity<?> procesarSolicitudPermiso(Long id, SolicitudPermisoDto solicitud) 
            throws DiasInsuficientesException, PermisoNoConcedidoException {
        
        EmpleadoTiempoCompleto empleado = service.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + id));
        
        // 1. Modificación en memoria
        empleado.solicitarPermiso(
            solicitud.getFechaInicio(), 
            solicitud.getFechaFin(),
            solicitud.getTipoPermiso(),
            empleado.getNumeroDeCedula());

        // 2. Persistencia obligatoria
        service.save(empleado);
            
        // Crear respuesta con detalles del permiso aprobado
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Permiso aprobado exitosamente");
        response.put("empleado", empleado.getNombre() + " " + empleado.getApellido());
        response.put("diasSolicitados", ChronoUnit.DAYS.between(solicitud.getFechaInicio(), solicitud.getFechaFin()) + 1);
        
        return ResponseEntity.ok(response);
    }
}