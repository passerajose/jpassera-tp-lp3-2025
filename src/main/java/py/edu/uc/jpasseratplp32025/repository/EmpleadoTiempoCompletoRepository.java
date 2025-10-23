package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;

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
}