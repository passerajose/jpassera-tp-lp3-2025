package py.edu.uc.jpasseratplp32025.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import py.edu.uc.jpasseratplp32025.entity.Contratista;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContratistaRepository extends JpaRepository<Contratista, Long> {
    @Query(value = "SELECT * FROM personas WHERE tipo_persona = 'CONTRATISTA' AND fecha_fin_contrato > :fecha", 
       nativeQuery = true)
    List<Contratista> findByFechaFinContratoAfter(@Param("fecha") LocalDate fecha);
}