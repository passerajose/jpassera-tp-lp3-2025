package py.edu.uc.jpasseratplp32025.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import py.edu.uc.jpasseratplp32025.entity.Contratista;
import py.edu.uc.jpasseratplp32025.service.ContratistaService;

import java.util.List;

@RestController
@RequestMapping("/api/contratistas")
public class ContratistaController {

    private final ContratistaService service;

    @Autowired
    public ContratistaController(ContratistaService service) {
        this.service = service;
    }

    // POST /api/contratistas
    @PostMapping
    public ResponseEntity<Contratista> create(@RequestBody Contratista contratista) {
        try {
            Contratista savedContratista = service.save(contratista);
            return new ResponseEntity<>(savedContratista, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // GET /api/contratistas
    @GetMapping
    public ResponseEntity<List<Contratista>> findAll() {
        List<Contratista> contratistas = service.findAll();
        if (contratistas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(contratistas, HttpStatus.OK);
    }

    // GET /api/contratistas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Contratista> findById(@PathVariable Long id) {
        return service.findById(id)
                .map(contratista -> new ResponseEntity<>(contratista, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE /api/contratistas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // PUT /api/contratistas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Contratista contratistaDetails) {
        // NOTA: El tipo de retorno ahora es ResponseEntity<?>
        return service.findById(id)
                .map(existingContratista -> {
                    contratistaDetails.setId(id); // Asegura que el ID sea correcto para la actualización
                    try {
                        Contratista updatedContratista = service.save(contratistaDetails);
                        // Retorna 200 OK con el objeto Contratista
                        return new ResponseEntity<>(updatedContratista, HttpStatus.OK);
                    } catch (IllegalArgumentException e) {
                        // Retorna 400 Bad Request con un mensaje de error (String)
                        return ResponseEntity.badRequest().body("Error de validación: " + e.getMessage());
                    }
                })
                // Retorna 404 Not Found (asegurando que el tipo de retorno sea compatible)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND)); 
    }

    // CONSULTA ESPECÍFICA: GET /api/contratistas/vigentes
    @GetMapping("/vigentes")
    public ResponseEntity<List<Contratista>> getContratosVigentes() {
        List<Contratista> contratistas = service.buscarContratosVigentes();
        if (contratistas.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(contratistas, HttpStatus.OK);
    }
}