package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;
import py.edu.uc.jpasseratplp32025.entity.Contratista;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.service.ContratistaService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit; // Importación necesaria
import java.util.HashMap; // Importación necesaria
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contratistas")
public class ContratistaController extends BaseEmpleadoController<Contratista> {

    private final ContratistaService service;

    @Autowired
    public ContratistaController(ContratistaService service) {
        this.service = service;
    }

    /**
     * Método de validación para asegurar que la fecha de nacimiento no sea futura.
     * Lanza FechaNacimientoFuturaException si la validación falla.
     */
    private void validarFechaNacimiento(Contratista contratista) {
        if (contratista.getFechaDeNacimiento() != null && contratista.getFechaDeNacimiento().isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException("La fecha de nacimiento (" + contratista.getFechaDeNacimiento() + ") no puede ser posterior a la fecha actual.");
        }
    }

    // POST /api/contratistas
    @PostMapping
    public ResponseEntity<Contratista> create(@RequestBody Contratista contratista) {
        // Se elimina el try-catch aquí para que el GlobalExceptionHandler maneje FechaNacimientoFuturaException
        validarFechaNacimiento(contratista);
        Contratista savedContratista = service.save(contratista);
        return new ResponseEntity<>(savedContratista, HttpStatus.CREATED);
    }

    // GET /api/contratistas
    @GetMapping
    public ResponseEntity<List<Contratista>> findAll() {
        List<Contratista> contratistas = service.findAll();
        if (contratistas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(contratistas, HttpStatus.OK);
    }

    // GET /api/contratistas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Contratista> findById(@PathVariable Long id) {
        // Se usa orElseThrow para lanzar EmpleadoNoEncontradoException (HTTP 404)
        return service.findById(id)
                .map(contratista -> new ResponseEntity<>(contratista, HttpStatus.OK))
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Contratista no encontrado con ID: " + id));
    }

    // DELETE /api/contratistas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        // Se valida existencia para que el Global Handler maneje el 404 si no existe
        if (!service.findById(id).isPresent()) {
            throw new EmpleadoNoEncontradoException("Contratista no encontrado con ID: " + id);
        }
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT /api/contratistas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Contratista> update(@PathVariable Long id, @RequestBody Contratista contratistaDetails) {
        // El tipo de retorno es Contratista, ya que las excepciones son manejadas globalmente
        return service.findById(id)
                .map(existingContratista -> {
                    contratistaDetails.setId(id);
                    validarFechaNacimiento(contratistaDetails);
                    // Se elimina el try-catch de IllegalArgumentException, usando el Global Handler
                    Contratista updatedContratista = service.save(contratistaDetails);
                    return new ResponseEntity<>(updatedContratista, HttpStatus.OK);
                })
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Contratista no encontrado con ID: " + id)); 
    }

    // CONSULTA ESPECÍFICA: GET /api/contratistas/vigentes
    @GetMapping("/vigentes")
    public ResponseEntity<List<Contratista>> getContratosVigentes() {
        List<Contratista> contratistas = service.buscarContratosVigentes();
        if (contratistas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(contratistas, HttpStatus.OK);
    }

    // Procesamiento de Solicitud de Permiso (FIX de Persistencia)
    @Override
    protected ResponseEntity<?> procesarSolicitudPermiso(Long id, SolicitudPermisoDto solicitud) 
            throws DiasInsuficientesException, PermisoNoConcedidoException {
        
        Contratista empleado = service.findById(id)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Contratista no encontrado con ID: " + id));
        
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
        // Se usa ChronoUnit.DAYS.between para calcular días
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