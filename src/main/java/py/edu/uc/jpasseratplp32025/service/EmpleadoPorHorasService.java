package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;
import py.edu.uc.jpasseratplp32025.repository.EmpleadoPorHorasRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EmpleadoPorHorasService {

    private static final Logger log = LoggerFactory.getLogger(EmpleadoPorHorasService.class);

    private final EmpleadoPorHorasRepository repository;

    @Autowired
    public EmpleadoPorHorasService(EmpleadoPorHorasRepository repository) {
        this.repository = repository;
        log.info("EmpleadoPorHorasService inicializado.");
    }

    // CRUD: Encontrar todos
    public List<EmpleadoPorHora> findAll() {
        log.debug("Buscando todos los empleados por hora.");
        return repository.findAll();
    }

    // CRUD: Encontrar por ID
    public Optional<EmpleadoPorHora> findById(Long id) {
        log.debug("Buscando empleado por hora por ID: {}", id);
        return repository.findById(id);
    }

    // CRUD: Guardar/Actualizar
    public EmpleadoPorHora save(EmpleadoPorHora empleado) {
        log.info("Guardando/actualizando empleado por hora con C.I.: {}", empleado.getNumeroDeCedula());
        if (!empleado.validarDatosEspecificos()) {
            log.error("Fallo de validación para empleado por hora con C.I. {}.", empleado.getNumeroDeCedula());
            throw new IllegalArgumentException("Datos del empleado inválidos. Verifique Tarifa y Horas trabajadas (1-80).");
        }

        // CORRECCIÓN CLAVE: Forzar el cálculo y la persistencia del salario antes de guardar.
        BigDecimal salarioCalculado = empleado.calcularSalario();
        empleado.setSalarioMensual(salarioCalculado);

        EmpleadoPorHora savedEmpleado = repository.save(empleado);
        log.info("Empleado por hora guardado exitosamente con ID: {}", savedEmpleado.getId());
        return savedEmpleado;
    }

    // CRUD: Eliminar por ID
    public void deleteById(Long id) {
        log.warn("Eliminando empleado por hora con ID: {}", id);
        repository.deleteById(id);
    }

    // MÉTODO DE CONSULTA ESPECÍFICA: Buscar empleados con más de X horas
    public List<EmpleadoPorHora> buscarConMasDeHoras(Integer horas) {
        if (horas == null || horas < 0) {
            log.warn("Solicitud de búsqueda por horas inválida: {}", horas);
            throw new IllegalArgumentException("El número de horas debe ser un valor positivo.");
        }
        log.info("Buscando empleados con más de {} horas trabajadas.", horas);
        // Asumiendo que EmpleadoPorHorasRepository tiene el método:
        // List<EmpleadoPorHora> findByHorasTrabajadasGreaterThan(Integer horas);
        return repository.findByHorasTrabajadasGreaterThan(horas);
    }

    // MÉTODO DE CONSULTA ESPECÍFICA: Buscar contratos vigentes
    public List<EmpleadoPorHora> buscarContratosVigentes() {
        log.info("Buscando empleados por hora con contratos vigentes");
        // El filtro de vigencia se basa en la fechaFinContrato, si es un campo de EmpleadoPorHora
        // y su lógica de contratoVigente() está en Empleado.
        // Asumiendo que EmpleadoPorHorasRepository tiene el método:
        // List<EmpleadoPorHora> findByFechaFinContratoAfter(LocalDate hoy);
        return repository.findAll().stream()
                .filter(empleado -> empleado.getFechaFinContrato() != null && empleado.getFechaFinContrato().isAfter(java.time.LocalDate.now()))
                .filter(empleado -> empleado.validarDatosEspecificos())
                .toList();
    }

    // ========================================================================
    // NUEVA FUNCIONALIDAD: CÁLCULO DE NÓMINA TOTAL (por tipo)
    // ========================================================================

    /**
     * Calcula la suma total de salarios brutos (salarioMensual) para los EmpleadosPorHora.
     * @return Map<String, BigDecimal> donde la clave es "EmpleadoPorHora" y el valor es la nómina total.
     */
    public Map<String, BigDecimal> calcularNominaTotal() {
        log.info("Iniciando cálculo de nómina total para Empleados Por Hora."); // CORRECCIÓN de log

        List<EmpleadoPorHora> empleados = repository.findAll();
        BigDecimal sumaTotalSalarios = BigDecimal.ZERO;

        for (EmpleadoPorHora empleado : empleados) {
            // Utilizamos getSalarioMensual(), que retorna el valor persistido/calculado
            if (empleado.validarDatosEspecificos() && empleado.getSalarioMensual() != null) {
                sumaTotalSalarios = sumaTotalSalarios.add(empleado.getSalarioMensual());
            } else {
                log.warn("Empleado ID {} (C.I. {}) excluido de la nómina por datos inválidos o salario nulo.",
                        empleado.getId(), empleado.getNumeroDeCedula());
            }
        }

        String nombreClase = EmpleadoPorHora.class.getSimpleName(); // CORRECCIÓN de clase
        Map<String, BigDecimal> resultado = new HashMap<>();
        resultado.put(nombreClase, sumaTotalSalarios.setScale(2, BigDecimal.ROUND_HALF_UP));

        log.info("Nómina total calculada para {}: {}", nombreClase, sumaTotalSalarios);
        return resultado;
    }
}