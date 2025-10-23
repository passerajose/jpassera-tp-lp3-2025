package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.Contratista;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContratistaRepository extends JpaRepository<Contratista, Long> {

    /**
     * Busca todos los contratistas cuyo contrato está vigente.
     * Esto se determina si la fechaFinContrato es posterior (After) a la fecha de hoy.
     * Implementa la convención de nombres de Query Methods de Spring Data JPA: findBy<Propiedad>After.
     *
     * @param fecha La fecha actual (LocalDate.now()) que se usa como límite.
     * @return Una lista de Contratista con contratos vigentes.
     */
    List<Contratista> findByFechaFinContratoAfter(LocalDate fecha);
}