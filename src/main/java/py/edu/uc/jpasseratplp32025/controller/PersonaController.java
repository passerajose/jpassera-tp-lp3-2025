package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import py.edu.uc.jpasseratplp32025.service.PersonaService;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService personaService;

    @Autowired
    public PersonaController(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Crea una nueva persona.
     * Propaga FechaNacimientoFuturaException (manejo global: 400 Bad Request).
     */
    @PostMapping
    public ResponseEntity<PersonaJpa> crearPersona(@RequestBody PersonaJpa persona) {
        // La excepción se lanza directamente desde el servicio si es necesario.
        PersonaJpa nuevaPersona = personaService.guardarPersona(persona);
        return new ResponseEntity<>(nuevaPersona, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PersonaJpa>> obtenerTodasLasPersonas() {
        List<PersonaJpa> personas = personaService.obtenerTodasLasPersonas();
        return ResponseEntity.ok(personas);
    }

    /**
     * Obtiene una persona por ID.
     * Propaga EmpleadoNoEncontradoException (manejo global: 404 Not Found).
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonaJpa> obtenerPersonaPorId(@PathVariable Long id) {
        // Si la persona no existe, el servicio lanza la excepción 404.
        PersonaJpa persona = personaService.obtenerPersonaPorId(id);
        return ResponseEntity.ok(persona);
    }

    /**
     * Actualiza una persona por ID.
     * Propaga EmpleadoNoEncontradoException (404) y FechaNacimientoFuturaException (400).
     */
    @PutMapping("/{id}")
    public ResponseEntity<PersonaJpa> actualizarPersona(
            @PathVariable Long id,
            @RequestBody PersonaJpa persona) {
        // Si no existe o la fecha es inválida, se lanza la excepción.
        PersonaJpa personaActualizada = personaService.actualizarPersona(id, persona);
        return ResponseEntity.ok(personaActualizada);
    }

    /**
     * Elimina una persona por ID.
     * Propaga EmpleadoNoEncontradoException (manejo global: 404 Not Found).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable Long id) {
        // Si no existe, el servicio lanza la excepción 404.
        personaService.eliminarPersona(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PersonaJpa>> buscarPorNombre(
            @RequestParam(name = "nombre") String nombreFragmento) {

        List<PersonaJpa> personas = personaService.buscarPorNombre(nombreFragmento);

        if (personas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(personas, HttpStatus.OK);
    }
}