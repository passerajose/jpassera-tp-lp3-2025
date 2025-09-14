package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;

@Repository
public interface PersonaRepository extends JpaRepository<PersonaJpa, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    
    // Custom query methods can be added here
    PersonaJpa findByNumeroDeCedula(String numeroDeCedula);
    boolean existsByNumeroDeCedula(String numeroDeCedula);
}
