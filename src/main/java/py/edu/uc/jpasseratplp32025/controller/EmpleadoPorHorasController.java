package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException; // Importación necesaria
import py.edu.uc.jpasseratplp32025.service.EmpleadoPorHorasService;

import java.math.BigDecimal;
import java.time.LocalDate; // Importación necesaria
import java.time.temporal.ChronoUnit; // Importación necesaria
import java.util.HashMap; // Importación necesaria
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados-por-hora")
public class EmpleadoPorHorasController extends BaseEmpleadoController<EmpleadoPorHora> {

    private final EmpleadoPorHorasService service;

    @Autowired
    public EmpleadoPorHorasController(EmpleadoPorHorasService service) {
        this.service = service;
    }
    
    // --- LÓGICA DE VALIDACIÓN ---
    private void validarFechaNacimiento(EmpleadoPorHora empleado) {
        if (empleado.getFechaDeNacimiento() != null && empleado.getFechaDeNacimiento().isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException("La fecha de nacimiento (" + empleado.getFechaDeNacimiento() + ") no puede ser posterior a la fecha actual.");
        }
    }
    // ----------------------------

    // POST /api/empleados-por-hora
    @PostMapping
    public ResponseEntity<EmpleadoPorHora> create(@RequestBody EmpleadoPorHora empleado) {
        // Se aplica validación y se elimina try-catch para que el Global Handler actúe
        validarFechaNacimiento(empleado);
        EmpleadoPorHora savedEmpleado = service.save(empleado);
        return new ResponseEntity<>(savedEmpleado, HttpStatus.CREATED);
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
        // Se usa orElseThrow para lanzar EmpleadoNoEncontradoException (HTTP 404)
        return service.findById(id)
                .map(empleado -> new ResponseEntity<>(empleado, HttpStatus.OK))
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado por hora no encontrado con ID: " + id));
    }

    // DELETE /api/empleados-por-hora/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            throw new EmpleadoNoEncontradoException("Empleado por hora no encontrado con ID: " + id);
        }
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT /api/empleados-por-hora/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoPorHora> update(@PathVariable Long id, @RequestBody EmpleadoPorHora empleadoDetails) {
        // El tipo de retorno es EmpleadoPorHora, ya que las excepciones son manejadas globalmente
        return service.findById(id)
                .map(existingEmpleado -> {
                    empleadoDetails.setId(id);
                    validarFechaNacimiento(empleadoDetails); // Aplicar validación aquí
                    // Se elimina el try-catch, usando el Global Handler
                    EmpleadoPorHora updatedEmpleado = service.save(empleadoDetails);
                    return new ResponseEntity<>(updatedEmpleado, HttpStatus.OK);
                })
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado por hora no encontrado con ID: " + id));
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

    // CONSULTA ESPECÍFICA: GET /api/empleados-por-hora/vigentes
    @GetMapping("/vigentes")
    public ResponseEntity<List<EmpleadoPorHora>> getContratosVigentes() {
        List<EmpleadoPorHora> empleadosPorHora = service.buscarContratosVigentes();
        if (empleadosPorHora.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(empleadosPorHora, HttpStatus.OK);
    }

    // Procesamiento de Solicitud de Permiso (FIX de Persistencia)
    @Override
    protected ResponseEntity<?> procesarSolicitudPermiso(Long id, SolicitudPermisoDto solicitud) 
            throws DiasInsuficientesException, PermisoNoConcedidoException {
        
        EmpleadoPorHora empleado = service.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado por hora no encontrado con ID: " + id));
        
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

    // NUEVO ENDPOINT: CÁLCULO DE NÓMINA TOTAL POR TIPO
    @GetMapping("/nomina-total")
    public ResponseEntity<Map<String, BigDecimal>> getNominaTotal() {
        Map<String, BigDecimal> nomina = service.calcularNominaTotal();

        if (nomina.isEmpty() || nomina.values().stream().allMatch(BigDecimal.ZERO::equals)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(nomina, HttpStatus.OK);
    }
}