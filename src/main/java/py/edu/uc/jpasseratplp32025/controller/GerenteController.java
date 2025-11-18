package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.jpasseratplp32025.dto.SolicitudPermisoDto;
import py.edu.uc.jpasseratplp32025.entity.Gerente;
import py.edu.uc.jpasseratplp32025.exception.DiasInsuficientesException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.service.GerenteService;
import py.edu.uc.jpasseratplp32025.mapper.GerenteMapper;

import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/gerentes")
public class GerenteController extends BaseEmpleadoController<Gerente> {

    private final GerenteService service;
    private final GerenteMapper mapper;

    @Autowired
    public GerenteController(GerenteService service, GerenteMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    /**
     * Método de validación para asegurar que la fecha de nacimiento no sea futura.
     * Lanza FechaNacimientoFuturaException si la validación falla.
     */
    private void validarFechaNacimiento(Gerente gerente) {
        if (gerente.getFechaDeNacimiento() != null && gerente.getFechaDeNacimiento().isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException("La fecha de nacimiento (" + gerente.getFechaDeNacimiento() + ") no puede ser posterior a la fecha actual.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Gerente>> getAllGerentes() {
        List<Gerente> gerentes = service.findAll();
        return new ResponseEntity<>(gerentes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gerente> getGerenteById(@PathVariable Long id) {
        return service.findById(id)
                .map(gerente -> new ResponseEntity<>(gerente, HttpStatus.OK))
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Gerente no encontrado con ID: " + id));
    }

    @PostMapping
    public ResponseEntity<Gerente> createGerente(@RequestBody Gerente gerente) {
        // 1. Validación de la fecha de nacimiento
        validarFechaNacimiento(gerente); 
        
        Gerente savedGerente = service.save(gerente);
        return new ResponseEntity<>(savedGerente, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Gerente> updateGerente(@PathVariable Long id, @RequestBody Gerente gerente) {
        return service.findById(id)
                .map(existingGerente -> {
                    // 1. Validación de la fecha de nacimiento
                    validarFechaNacimiento(gerente); 
                    
                    gerente.setId(id);
                    Gerente updatedGerente = service.save(gerente);
                    return new ResponseEntity<>(updatedGerente, HttpStatus.OK);
                })
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Gerente no encontrado con ID: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGerente(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Implementación del método abstracto de BaseEmpleadoController
    @Override
    protected ResponseEntity<?> procesarSolicitudPermiso(Long id, SolicitudPermisoDto solicitud) 
            throws DiasInsuficientesException, PermisoNoConcedidoException {
        
        Gerente gerente = service.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Gerente no encontrado con ID: " + id));

        gerente.solicitarPermiso(
            solicitud.getFechaInicio(),
            solicitud.getFechaFin(),
            solicitud.getTipoPermiso(),
            gerente.getNumeroDeCedula()
        );

        service.save(gerente);

        // Crear respuesta con detalles del permiso aprobado
        Map<String, Object> response = new HashMap<>();
        response.put("mensaje", "Permiso aprobado exitosamente");
        response.put("empleado", gerente.getNombre() + " " + gerente.getApellido());
        response.put("fechaInicio", solicitud.getFechaInicio());
        response.put("fechaFin", solicitud.getFechaFin());
        response.put("tipoPermiso", solicitud.getTipoPermiso());
        response.put("diasSolicitados", ChronoUnit.DAYS.between(
            solicitud.getFechaInicio(), 
            solicitud.getFechaFin()) + 1
        );

        return ResponseEntity.ok(response);
    }

    // Endpoint específico para gerentes: aprobar permisos de otros empleados
    @PostMapping("/{id}/aprobar-permiso/{solicitudId}")
    public ResponseEntity<?> aprobarPermiso(
            @PathVariable Long id,
            @PathVariable Long solicitudId,
            @RequestParam boolean aprobado,
            @RequestParam(required = false) String notas) {
        
        try {
            service.aprobarPermiso(id, solicitudId, aprobado, notas);
            return ResponseEntity.ok().build();
        } catch (PermisoNoConcedidoException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para consultar nómina total del departamento a cargo
    @GetMapping("/{id}/nomina-departamento")
    public ResponseEntity<Map<String, BigDecimal>> getNominaDepartamento(@PathVariable Long id) {
        return service.findById(id)
                .map(gerente -> {
                    Map<String, BigDecimal> nominaDepartamento = service.calcularNominaDepartamento(gerente.getDepartamentoACargo());
                    return ResponseEntity.ok(nominaDepartamento);
                })
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Gerente no encontrado con ID: " + id));
    }
}