package py.edu.uc.jpasseratplp32025.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator; // Uso correcto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;
import py.edu.uc.jpasseratplp32025.repository.EmpleadoTiempoCompletoRepository;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// ******************************************************
// IMPORTS PARA SLF4J SIMPLE (MANUAL)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ******************************************************

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

// ANOTACIÓN @Slf4j ELIMINADA
@Service
public class EmpleadoTiempoCompletoService {

    // ******************************************************
    // INICIALIZACIÓN MANUAL DEL LOGGER (En lugar de @Slf4j)
    private static final Logger log = LoggerFactory.getLogger(EmpleadoTiempoCompletoService.class);
    // ******************************************************

    private final EmpleadoTiempoCompletoRepository repository;
    private final PersonaRepository personaRepository;
    private static final int BATCH_SIZE = 100;

    // CORRECCIÓN: El campo Validator solo se declara una vez y dentro de la clase.
    private final Validator validator;

    @Autowired
    public EmpleadoTiempoCompletoService(EmpleadoTiempoCompletoRepository repository, PersonaRepository personaRepository, Validator validator) {
        this.repository = repository;
        this.personaRepository = personaRepository;
        this.validator = validator;
        log.info("EmpleadoTiempoCompletoService inicializado.");
    }

    // 1. Obtener todos los empleados
    public List<EmpleadoTiempoCompleto> findAll() {
        log.debug("Buscando todos los empleados de tiempo completo.");
        return repository.findAll();
    }

    // 2. Obtener un empleado por ID
    public Optional<EmpleadoTiempoCompleto> findById(Long id) {
        log.debug("Buscando empleado por ID: {}", id);
        return repository.findById(id);
    }

    // 3. Crear o actualizar un empleado (Con Logging)
    public EmpleadoTiempoCompleto save(EmpleadoTiempoCompleto empleado) {
        log.info("Intentando guardar/actualizar empleado con C.I.: {}", empleado.getNumeroDeCedula());

        // Opcional: Ejecutar Bean Validation manualmente antes de la lógica de negocio
        Set<ConstraintViolation<EmpleadoTiempoCompleto>> violations = validator.validate(empleado);
        if (!violations.isEmpty()) {
            String validationMessage = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            log.error("Fallo de Bean Validation en save: {}", validationMessage);
            throw new IllegalArgumentException("Fallo de Bean Validation: " + validationMessage);
        }

        if (!empleado.validarDatosEspecificos()) {
            log.error("Fallo de validación para empleado con C.I. {}. Salario Mínimo o Departamento Inválido.", empleado.getNumeroDeCedula());
            throw new IllegalArgumentException("Datos del empleado inválidos. Verifique Salario Mínimo y Departamento.");
        }

        EmpleadoTiempoCompleto savedEmpleado = repository.save(empleado);
        log.info("Empleado guardado exitosamente con ID: {}", savedEmpleado.getId());
        return savedEmpleado;
    }

    // 4. Eliminar un empleado por ID
    public void deleteById(Long id) {
        log.warn("Eliminando empleado con ID: {}", id);
        repository.deleteById(id);
    }

    // 5. Lógica de negocio específica: Calcular el salario neto (Con Logging)
    public Optional<BigDecimal> calcularSalarioNeto(Long id) {
        return repository.findById(id)
                .map(empleado -> {
                    if (!empleado.validarDatosEspecificos()) {
                        log.warn("Cálculo de salario neto omitido para empleado ID {} por datos inválidos.", id);
                        return null;
                    }
                    BigDecimal salarioNeto = empleado.calcularSalario().subtract(empleado.calcularDeducciones());
                    log.debug("Salario Neto calculado para ID {}: {}", id, salarioNeto);
                    return salarioNeto;
                });
    }

    // 6. Lógica de negocio: Calcular Impuestos
    public Optional<BigDecimal> calcularImpuestos(Long id) {
        return repository.findById(id)
                .map(empleado -> {
                    if (empleado.validarDatosEspecificos()) {
                        BigDecimal impuestos = empleado.calcularImpuestos();
                        log.debug("Impuestos calculados para ID {}: {}", id, impuestos);
                        return impuestos;
                    }
                    return null;
                });
    }

    // 7. Implementación de Persistencia en Batch (Con Logging y Validaciones)
    public List<EmpleadoTiempoCompleto> guardarEmpleadosEnBatch(List<EmpleadoTiempoCompleto> empleados) {
        if (empleados == null || empleados.isEmpty()) {
            log.warn("Intentó guardar una lista de empleados vacía en batch.");
            return new ArrayList<>();
        }

        log.info("Iniciando la carga en batch de {} empleados (lotes de {}).", empleados.size(), BATCH_SIZE);
        List<EmpleadoTiempoCompleto> empleadosGuardados = new ArrayList<>();
        int n = empleados.size();

        for (int i = 0; i < n; i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, n);
            List<EmpleadoTiempoCompleto> chunk = empleados.subList(i, endIndex);

            log.debug("Validando y persistiendo Lote de empleados {}-{} de {}.", i, endIndex, n);

            // Bucle de Validación
            // Validar duplicados en el mismo chunk y existencia en BD antes de persistir
            Set<String> cedulasEnChunk = new HashSet<>();
            for (EmpleadoTiempoCompleto empleado : chunk) {

                // 1. VALIDACIÓN MANUAL DE BEAN VALIDATION (CORREGIDO)
                Set<ConstraintViolation<EmpleadoTiempoCompleto>> violations = validator.validate(empleado);

                if (!violations.isEmpty()) {
                    String validationMessage = violations.stream()
                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                            .collect(Collectors.joining(", ")); // Collectors.joining() CORREGIDO

                    log.error("Fallo de Bean Validation en lote para empleado {}: {}", empleado.getNumeroDeCedula(), validationMessage);
                    throw new IllegalArgumentException("Fallo de Bean Validation: " + validationMessage);
                }

                // 2. VALIDACIÓN DE NEGOCIO (LÓGICA EXISTENTE)
                boolean isValid = empleado.validarDatosEspecificos();
                String idDisplay = (empleado.getId() != null) ? empleado.getId().toString() : "sin ID (índice: " + chunk.indexOf(empleado) + ")";

                // Imprimir el resultado de la validación
                log.info("Validación del empleado {} (C.I.: {}): {}",
                        idDisplay,
                        empleado.getNumeroDeCedula(),
                        (isValid ? "OK" : "FALLÓ (Negocio)"));

                if (!isValid) {
                    log.error("Fallo de validación en lote para empleado {}.", idDisplay);
                    throw new IllegalArgumentException(String.format("Error de validación en el empleado %s. Verifique Salario Mínimo y Departamento.", idDisplay));
                }

                // 3. VERIFICACIÓN DE DUPLICADOS Y EXISTENCIA
                String cedula = empleado.getNumeroDeCedula();
                if (cedula == null || cedula.trim().isEmpty()) {
                    // Nota: Bean Validation ya debería atrapar esto con @Pattern o @NotBlank
                    throw new IllegalArgumentException("Cédula vacía en uno de los empleados del lote.");
                }
                if (!cedulasEnChunk.add(cedula)) {
                    throw new IllegalArgumentException("Cédula duplicada en el mismo lote: " + cedula);
                }

                // Verificar existencia previa en la base de datos
                if (personaRepository.existsByNumeroDeCedula(cedula)) {
                    throw new IllegalArgumentException("Ya existe una persona con cédula: " + cedula);
                }
            }

            try {
                // Si todas las validaciones previas pasaron, persistir.
                empleadosGuardados.addAll(repository.saveAll(chunk));
                log.info("Lote de empleados {}-{} guardado exitosamente.", i, endIndex);
            } catch (DataIntegrityViolationException dive) {
                // Este catch es para errores de unicidad o NOT NULL a nivel DB que Bean Validation no atrapó
                log.error("Error de integridad al guardar lote {}-{}: {}", i, endIndex, dive.getMessage());
                throw new IllegalArgumentException("Error de integridad al guardar lote. Verifique cédulas duplicadas o restricciones de la base de datos.");
            }
        }

        log.info("Carga en batch finalizada. Total de empleados guardados: {}", empleadosGuardados.size());
        return empleadosGuardados;
    }

    // 8. Lógica de negocio: Buscar empleado por departamento
    public List<EmpleadoTiempoCompleto> buscarPorDepartamento(String departamento) {
        if (departamento == null || departamento.trim().isEmpty()) {
            return repository.findAll();
        }
        // Llama al método del repositorio: findByDepartamento(String departamento);
        return repository.findByDepartamento(departamento);
    }

    // ========================================================================
    // NUEVA FUNCIONALIDAD: CÁLCULO DE NÓMINA TOTAL (por tipo)
    // ========================================================================

    /**
     * Calcula la suma total de salarios brutos (salarioMensual) para los EmpleadosTiempoCompleto.
     * Retorna un Map con la clave siendo el nombre de la clase (EmpleadoTiempoCompleto) y
     * el valor siendo la suma total de sus salarios.
     * * @return Map<String, BigDecimal> donde la clave es "EmpleadoTiempoCompleto" y el valor es la nómina total.
     */
    public Map<String, BigDecimal> calcularNominaTotal() {
        log.info("Iniciando cálculo de nómina total para Empleados de Tiempo Completo.");

        List<EmpleadoTiempoCompleto> empleados = repository.findAll();
        BigDecimal sumaTotalSalarios = BigDecimal.ZERO;

        for (EmpleadoTiempoCompleto empleado : empleados) {
            // Solo sumar si los datos específicos son válidos (ej. salario no es nulo y cumple el mínimo)
            if (empleado.validarDatosEspecificos() && empleado.getSalarioMensual() != null) {
                sumaTotalSalarios = sumaTotalSalarios.add(empleado.getSalarioMensual());
            } else {
                log.warn("Empleado ID {} (C.I. {}) excluido de la nómina por datos inválidos o salario nulo.",
                        empleado.getId(), empleado.getNumeroDeCedula());
            }
        }

        String nombreClase = EmpleadoTiempoCompleto.class.getSimpleName();
        Map<String, BigDecimal> resultado = new HashMap<>();
        resultado.put(nombreClase, sumaTotalSalarios.setScale(2, BigDecimal.ROUND_HALF_UP));

        log.info("Nómina total calculada para {}: {}", nombreClase, sumaTotalSalarios);
        return resultado;
    }
}