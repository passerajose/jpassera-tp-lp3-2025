package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import py.edu.uc.jpasseratplp32025.entity.Contratista;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmpleadoTiempoCompletoRepository extends JpaRepository<EmpleadoTiempoCompleto, Long> {

    /**
     * Busca todos los empleados de tiempo completo que pertenecen a un departamento específico.
     * Implementa la convención de nombres de Query Methods de Spring Data JPA: findBy<Propiedad>.
     *
     * @param departamento El nombre del departamento.
     * @return Una lista de EmpleadoTiempoCompleto.
     */
    List<EmpleadoTiempoCompleto> findByDepartamento(String departamento);

    @Query(value = "SELECT * FROM personas WHERE tipo_persona = 'EMPLEADO_TIEMPO_COMPLETO' AND fecha_fin_contrato > :fecha", 
           nativeQuery = true)
    List<EmpleadoTiempoCompleto> findByFechaFinContratoAfter(@Param("fecha") LocalDate fecha);
}