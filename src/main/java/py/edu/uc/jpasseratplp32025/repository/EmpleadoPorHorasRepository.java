package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;

import java.time.LocalDate;
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

    @Query("SELECT e FROM EmpleadoPorHora e WHERE e.horasTrabajadas > 0")
    List<EmpleadoPorHora> findVigentes();
    
    @Query(value = "SELECT * FROM personas WHERE tipo_persona = 'EMPLEADO_POR_HORA' AND fecha_fin_contrato > :fecha", 
           nativeQuery = true)
    List<EmpleadoPorHora> findByFechaFinContratoAfter(@Param("fecha") LocalDate fecha);
}