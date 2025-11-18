package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import py.edu.uc.jpasseratplp32025.entity.Gerente;
import py.edu.uc.jpasseratplp32025.repository.GerenteRepository;
import py.edu.uc.jpasseratplp32025.exception.PermisoNoConcedidoException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GerenteService {
    
    private static final Logger log = LoggerFactory.getLogger(GerenteService.class);
    
    private final GerenteRepository repository;
    private final EmpleadoTiempoCompletoService empleadoService;

    @Autowired
    public GerenteService(GerenteRepository repository, EmpleadoTiempoCompletoService empleadoService) {
        this.repository = repository;
        this.empleadoService = empleadoService;
        log.info("GerenteService inicializado.");
    }

    // CRUD básico
    public List<Gerente> findAll() {
        return repository.findAll();
    }

    public Optional<Gerente> findById(Long id) {
        return repository.findById(id);
    }

    public Gerente save(Gerente gerente) {
        log.info("Guardando/actualizando gerente con C.I.: {}", gerente.getNumeroDeCedula());
        if (!gerente.validarDatosEspecificos()) {
            log.error("Fallo de validación para gerente con C.I. {}", gerente.getNumeroDeCedula());
            throw new IllegalArgumentException("Datos del gerente inválidos");
        }
        return repository.save(gerente);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // Métodos específicos de gerente
    public void aprobarPermiso(Long gerenteId, Long solicitudId, boolean aprobado, String notas) 
            throws PermisoNoConcedidoException {
        
        Gerente gerente = findById(gerenteId)
            .orElseThrow(() -> new EmpleadoNoEncontradoException("Gerente no encontrado"));
            
        gerente.procesarAprobacionGerencial(solicitudId, aprobado, notas);
    }

    public Map<String, BigDecimal> calcularNominaDepartamento(String departamento) {
        List<Gerente> gerentesDelDepartamento = repository.findByDepartamentoACargo(departamento);
        
        BigDecimal totalNomina = gerentesDelDepartamento.stream()
            .map(Gerente::calcularSalario)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        Map<String, BigDecimal> resultado = new HashMap<>();
        resultado.put("TOTAL_NOMINA_" + departamento, totalNomina);
        
        return resultado;
    }

    // MÉTODO DE CONSULTA ESPECÍFICA: Buscar contratos vigentes
    public List<Gerente> buscarContratosVigentes() {
        LocalDate hoy = LocalDate.now();
        log.info("Buscando contratos vigentes a partir de la fecha: {}", hoy);
        // Usa la consulta definida en el repositorio: fechaFinContrato > hoy
        return repository.findByFechaFinContratoAfter(hoy);
    }
}