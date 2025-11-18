package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.Contratista;
import py.edu.uc.jpasseratplp32025.repository.ContratistaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContratistaService {

    private static final Logger log = LoggerFactory.getLogger(ContratistaService.class);

    private final ContratistaRepository repository;

    @Autowired
    public ContratistaService(ContratistaRepository repository) {
        this.repository = repository;
        log.info("ContratistaService inicializado.");
    }

    // CRUD: Encontrar todos
    public List<Contratista> findAll() {
        log.debug("Buscando todos los contratistas.");
        return repository.findAll();
    }

    // CRUD: Encontrar por ID
    public Optional<Contratista> findById(Long id) {
        log.debug("Buscando contratista por ID: {}", id);
        return repository.findById(id);
    }

    // CRUD: Guardar/Actualizar
    public Contratista save(Contratista contratista) {
        log.info("Guardando/actualizando contratista con C.I.: {}", contratista.getNumeroDeCedula());

        // 1. Validar datos específicos
        if (!contratista.validarDatosEspecificos()) {
            log.error("Fallo de validación para contratista con C.I. {}.", contratista.getNumeroDeCedula());
            throw new IllegalArgumentException("Datos del contratista inválidos. Verifique Monto y que la fecha de fin de contrato sea futura.");
        }

        // 2. CORRECCIÓN CLAVE: Forzar el cálculo y la persistencia del salario antes de guardar.
        // Esto garantiza que el campo persistido (salarioMensualPersistido) tenga un valor.
        BigDecimal salarioCalculado = contratista.calcularSalario();
        contratista.setSalarioMensualPersistido(salarioCalculado);

        // 3. Guardar
        Contratista savedContratista = repository.save(contratista);
        log.info("Contratista guardado exitosamente con ID: {}", savedContratista.getId());
        return savedContratista;
    }

    // CRUD: Eliminar por ID
    public void deleteById(Long id) {
        log.warn("Eliminando contratista con ID: {}", id);
        repository.deleteById(id);
    }

    // MÉTODO DE CONSULTA ESPECÍFICA: Buscar contratos vigentes
    public List<Contratista> buscarContratosVigentes() {
        LocalDate hoy = LocalDate.now();
        log.info("Buscando contratos vigentes a partir de la fecha: {}", hoy);
        // Usa la consulta definida en el repositorio: fechaFinContrato > hoy
        return repository.findByFechaFinContratoAfter(hoy);
    }

    // ========================================================================
    // NUEVA FUNCIONALIDAD: CÁLCULO DE NÓMINA TOTAL (por tipo)
    // ========================================================================

    /**
     * Calcula la suma total de salarios brutos (salarioMensual) para los Contratistas.
     * @return Map<String, BigDecimal> donde la clave es "Contratista" y el valor es la nómina total.
     */
    public Map<String, BigDecimal> calcularNominaTotal() {
        log.info("Iniciando cálculo de nómina total para Contratistas."); // CORRECCIÓN de log

        List<Contratista> empleados = repository.findAll();
        BigDecimal sumaTotalSalarios = BigDecimal.ZERO;

        for (Contratista empleado : empleados) {
            // Utilizamos getSalarioMensual(), que retorna el valor persistido (no nulo) o lo calcula si es necesario
            if (empleado.validarDatosEspecificos() && empleado.getSalarioMensual() != null) {
                sumaTotalSalarios = sumaTotalSalarios.add(empleado.getSalarioMensual());
            } else {
                log.warn("Contratista ID {} (C.I. {}) excluido de la nómina por datos inválidos o salario nulo.", // CORRECCIÓN de log
                        empleado.getId(), empleado.getNumeroDeCedula());
            }
        }

        String nombreClase = Contratista.class.getSimpleName();
        Map<String, BigDecimal> resultado = new HashMap<>();
        resultado.put(nombreClase, sumaTotalSalarios.setScale(2, BigDecimal.ROUND_HALF_UP));

        log.info("Nómina total calculada para {}: {}", nombreClase, sumaTotalSalarios);
        return resultado;
    }
}