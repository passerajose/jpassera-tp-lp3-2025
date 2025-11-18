package py.edu.uc.jpasseratplp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.jpasseratplp32025.dto.BaseDto;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.PersonaJpa;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Mapper base para convertir entre entidades y DTOs.
 * Proporciona funcionalidad común de mapeo y métodos de utilidad.
 *
 * @param <E> Tipo de entidad que extiende PersonaJpa
 * @param <D> Tipo de DTO que extiende BaseDto
 */
@Component
public abstract class BaseMapper<E extends PersonaJpa, D extends EmpleadoDto> {

    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Convierte una entidad a DTO
     */
    public abstract D toDto(E entity);

    /**
     * Convierte un DTO a entidad
     */
    public abstract E toEntity(D dto);

    /**
     * Mapea campos comunes de PersonaJpa a cualquier DTO
     */
    protected void mapCommonFields(PersonaJpa entity, BaseDto dto) {
        dto.setStatusCode(200);
        if (entity.validarDatosEspecificos()) {
            dto.setTechnicalMessage("Datos válidos");
            dto.setUserMessage("Datos correctos");
        } else {
            dto.setStatusCode(400);
            dto.setTechnicalMessage("Validación fallida");
            dto.setUserMessage("Los datos no cumplen con las reglas de negocio");
        }
    }

    /**
     * Método utilitario para parsear fechas de manera segura
     */
    protected LocalDate parseDate(String date) {
        try {
            return date != null ? LocalDate.parse(date, DATE_FORMATTER) : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Método utilitario para formatear fechas de manera segura
     */
    protected String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    /**
     * Valida si una entidad cumple con las reglas de negocio
     */
    protected boolean validateEntity(E entity) {
        return entity != null && entity.validarDatosEspecificos();
    }
}