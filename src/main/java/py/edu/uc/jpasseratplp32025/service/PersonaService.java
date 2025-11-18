package py.edu.uc.jpasseratplp32025.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.repository.PersonaRepository;
import py.edu.uc.jpasseratplp32025.exception.FechaNacimientoFuturaException;
import py.edu.uc.jpasseratplp32025.exception.EmpleadoNoEncontradoException; // Importación necesaria

import java.time.LocalDate;
import java.util.List;

@Service
public class PersonaService {

    private final PersonaRepository personaRepository;

    @Autowired
    public PersonaService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Auxiliar para buscar una persona por ID o lanzar EmpleadoNoEncontradoException (404).
     * @param id ID de la persona a buscar.
     * @return PersonaJpa si existe.
     * @throws EmpleadoNoEncontradoException si no se encuentra la persona.
     */
    public PersonaJpa buscarPorIdYLanzar(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Persona no encontrada con ID: " + id));
    }

    /**
     * Guarda una nueva persona en la base de datos.
     * @param persona La persona a guardar
     * @return La persona guardada con su ID generado
     * @throws FechaNacimientoFuturaException si la fecha de nacimiento está en el futuro
     */
    public PersonaJpa guardarPersona(PersonaJpa persona) {
        validarFechaNacimiento(persona.getFechaDeNacimiento());
        return personaRepository.save(persona);
    }

    public List<PersonaJpa> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }

    /**
     * Busca una persona por ID. Utiliza el método auxiliar para asegurar que
     * si no existe, lanza una excepción (HTTP 404).
     * @param id ID de la persona a buscar.
     * @return PersonaJpa encontrada.
     * @throws EmpleadoNoEncontradoException si la persona no existe.
     */
    public PersonaJpa obtenerPersonaPorId(Long id) {
        return buscarPorIdYLanzar(id);
    }

    /**
     * Actualiza una persona existente.
     * @param id ID de la persona a actualizar.
     * @param personaActualizada Los nuevos datos.
     * @return La persona actualizada.
     * @throws EmpleadoNoEncontradoException si no se encuentra la persona.
     * @throws FechaNacimientoFuturaException si la fecha de nacimiento es inválida.
     */
    public PersonaJpa actualizarPersona(Long id, PersonaJpa personaActualizada) {
        // Lanza 404 si no existe
        PersonaJpa personaExistente = buscarPorIdYLanzar(id); 
        
        // Valida la fecha (lanza 400 si es futuro)
        validarFechaNacimiento(personaActualizada.getFechaDeNacimiento());
        
        // Mapear y guardar
        personaActualizada.setId(id);
        return personaRepository.save(personaActualizada);
    }

    /**
     * Elimina una persona existente.
     * @param id ID de la persona a eliminar.
     * @throws EmpleadoNoEncontradoException si no se encuentra la persona.
     */
    public void eliminarPersona(Long id) {
        // Lanza 404 si no existe
        PersonaJpa personaExistente = buscarPorIdYLanzar(id); 
        
        personaRepository.deleteById(id);
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
    public List<PersonaJpa> buscarPorNombre(String nombreFragmento) {
        if (nombreFragmento == null || nombreFragmento.trim().isEmpty()) {
            return personaRepository.findAll();
        }
        return personaRepository.findByNombreContainingIgnoreCase(nombreFragmento);
    }
}