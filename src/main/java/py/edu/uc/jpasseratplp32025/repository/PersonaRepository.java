package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;

import java.util.List;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaJpa, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository

    PersonaJpa findByNumeroDeCedula(String numeroDeCedula);
    boolean existsByNumeroDeCedula(String numeroDeCedula);

    /**
     * Busca personas cuyo nombre contenga la cadena proporcionada, ignorando mayúsculas/minúsculas.
     * Ejemplo: buscarPorNombreContainingIgnoreCase("mar") encontrará "Marcos", "maria", "MARIO".
     *
     * @param nombre La cadena a buscar dentro del campo nombre.
     * @return Una lista de PersonaJpa que cumplen con el criterio.
     */
    List<PersonaJpa> findByNombreContainingIgnoreCase(String nombre);
}