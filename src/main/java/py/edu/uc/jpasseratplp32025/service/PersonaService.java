package py.edu.uc.jpasseratplp32025.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    @Autowired
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Guarda una nueva persona en la base de datos.
     * @param persona La persona a guardar
     * @return La persona guardada con su ID generado
     * @throws FechaNacimientoFuturaException si la fecha de nacimiento está en el futuro
     */
    public PersonaJpa guardarPersona(PersonaJpa persona) {
        // Verificar que la fecha de nacimiento no sea en el futuro
        if (persona.getFechaDeNacimiento().isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException(
                "La fecha de nacimiento no puede ser en el futuro: " + 
                persona.getFechaDeNacimiento()
            );
        }

        return personaRepository.save(persona);
    }

    public List<PersonaJpa> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }

    public Optional<PersonaJpa> obtenerPersonaPorId(Long id) {
        return personaRepository.findById(id);
    }

    public Optional<PersonaJpa> actualizarPersona(Long id, PersonaJpa personaActualizada) {
        validarFechaNacimiento(personaActualizada.getFechaDeNacimiento());
        
        return personaRepository.findById(id)
            .map(personaExistente -> {
                personaActualizada.setId(id);
                return personaRepository.save(personaActualizada);
            });
    }

    public boolean eliminarPersona(Long id) {
        if (personaRepository.existsById(id)) {
            personaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void validarFechaNacimiento(LocalDate fechaNacimiento) {
        if (fechaNacimiento.isAfter(LocalDate.now())) {
            throw new FechaNacimientoFuturaException(
                "La fecha de nacimiento no puede ser en el futuro: " + 
                fechaNacimiento
            );
        }
    }

    // =================================================================
    // NUEVA FUNCIONALIDAD: Buscar por Nombre (Case Insensitive)
    // =================================================================
    /**
     * Busca personas por un fragmento de nombre, sin distinguir entre mayúsculas y minúsculas.
     *
     * @param nombreFragmento La cadena a buscar.
     * @return Lista de PersonaJpa que coinciden.
     */
    public List<PersonaJpa> buscarPorNombre(String nombreFragmento) {
        if (nombreFragmento == null || nombreFragmento.trim().isEmpty()) {
            return personaRepository.findAll();
        }
        return personaRepository.findByNombreContainingIgnoreCase(nombreFragmento);
    }
}
