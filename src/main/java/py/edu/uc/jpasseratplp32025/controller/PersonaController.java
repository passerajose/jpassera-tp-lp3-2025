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

    @PostMapping
    public ResponseEntity<PersonaJpa> crearPersona(@RequestBody PersonaJpa persona) {
        PersonaJpa nuevaPersona = personaService.guardarPersona(persona);
        return new ResponseEntity<>(nuevaPersona, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PersonaJpa>> obtenerTodasLasPersonas() {
        List<PersonaJpa> personas = personaService.obtenerTodasLasPersonas();
        return ResponseEntity.ok(personas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonaJpa> obtenerPersonaPorId(@PathVariable Long id) {
        return personaService.obtenerPersonaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonaJpa> actualizarPersona(
            @PathVariable Long id,
            @RequestBody PersonaJpa persona) {
        return personaService.actualizarPersona(id, persona)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPersona(@PathVariable Long id) {
        if (personaService.eliminarPersona(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Busca personas por nombre de forma case-insensitive.
     * Endpoint: GET /api/personas/buscar?nombre=fragmento
     * @param nombreFragmento Fragmento de nombre a buscar.
     * @return Lista de personas que coinciden.
     */
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
