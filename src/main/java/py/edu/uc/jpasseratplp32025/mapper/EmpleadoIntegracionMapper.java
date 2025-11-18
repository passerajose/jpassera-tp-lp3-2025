package py.edu.uc.jpasseratplp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoExterno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class EmpleadoIntegracionMapper implements IntegracionMapper<EmpleadoExterno, EmpleadoDto> {
    private static final Logger log = LoggerFactory.getLogger(EmpleadoIntegracionMapper.class);

    @Override
    public EmpleadoDto toDto(EmpleadoExterno externo) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(externo.getEmployeeId());
        dto.setNombre(externo.getFirstName() + " " + externo.getLastName());
        dto.setNumeroDeCedula(externo.getIdentificationNumber());
        dto.setTipoEmpleado(externo.getEmployeeType());
        dto.setInformacionCompleta(buildFullInfo(externo));
        return dto;
    }

    @Override
    public EmpleadoExterno toEntity(EmpleadoDto dto) {
        // Implementación para convertir DTO a modelo externo
        EmpleadoExterno externo = new EmpleadoExterno();
        // ... mapeo de campos
        return externo;
    }

    private String buildFullInfo(EmpleadoExterno externo) {
        // Lógica para construir información completa desde sistema externo
        return String.format("%s %s (%s)", 
            externo.getFirstName(),
            externo.getLastName(),
            externo.getEmployeeType());
    }
}