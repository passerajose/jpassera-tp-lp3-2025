package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;
import py.edu.uc.jpasseratplp32025.repository.EmpleadoPorHorasRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        return repository.findByHorasTrabajadasGreaterThan(horas);
    }
}