package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;

import java.util.List;

@Repository
public interface EmpleadoPorHorasRepository extends JpaRepository<EmpleadoPorHora, Long> {

    /**
     * Busca todos los empleados por hora que tienen un número de horas trabajadas
     * estrictamente mayor que el valor dado.
     * Implementa la convención de nombres de Query Methods de Spring Data JPA: findBy<Propiedad>GreaterThan.
     *
     * @param horas El número mínimo de horas trabajadas (exclusivo).
     * @return Una lista de EmpleadoPorHora.
     */
    List<EmpleadoPorHora> findByHorasTrabajadasGreaterThan(Integer horas);
}