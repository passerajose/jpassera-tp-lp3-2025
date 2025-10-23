package py.edu.uc.jpasseratplp32025.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RemuneracionesService {

    // Inyectamos el repositorio de la clase base (PersonaJpa)
    // Esto es CLAVE para la estrategia SINGLE_TABLE, ya que este repositorio
    // puede acceder a TODAS las filas de la tabla 'personas' e instanciar
    // la subclase correcta (polimorfismo).
    private final PersonaRepository personaRepository;

    @Autowired
    public RemuneracionesService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Obtiene una lista de todos los empleados de la jerarquía, mapeándolos a EmpleadoDto.
     * Utiliza polimorfismo al llamar obtenerInformacionCompleta() en cada objeto.
     * * @return Una lista de objetos EmpleadoDto.
     */
    public List<EmpleadoDto> obtenerTodosLosEmpleados() {
        // 1. Obtener la lista polimórfica de la DB
        List<PersonaJpa> todasLasPersonas = personaRepository.findAll();

        // 2. Mapear la lista de PersonaJpa a EmpleadoDto
        return todasLasPersonas.stream()
                .map(this::mapToEmpleadoDto)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para mapear PersonaJpa a EmpleadoDto.
     * Esta es la clave del ejercicio de mapeo polimórfico.
     */
    private EmpleadoDto mapToEmpleadoDto(PersonaJpa persona) {
        return new EmpleadoDto(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getNumeroDeCedula(),
                // Obtener el nombre de la subclase real (polimorfismo)
                persona.getClass().getSimpleName(),
                // Llamar al método polimórfico. Cada subclase retorna su información completa.
                persona.obtenerInformacionCompleta()
        );
    }
}