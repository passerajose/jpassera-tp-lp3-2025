package py.edu.uc.jpasseratplp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.Gerente;

@Component
public class GerenteMapper extends BaseMapper<Gerente, EmpleadoDto> {
    
    @Override
    public EmpleadoDto toDto(Gerente gerente) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(gerente.getId());
        dto.setNombre(gerente.getNombre());
        dto.setApellido(gerente.getApellido());
        dto.setNumeroDeCedula(gerente.getNumeroDeCedula());
        dto.setTipoEmpleado("GERENTE");
        dto.setInformacionCompleta(gerente.obtenerInformacionCompleta());
        return dto;
    }

    @Override
    public Gerente toEntity(EmpleadoDto dto) {
        Gerente gerente = new Gerente();
        gerente.setId(dto.getId());
        gerente.setNombre(dto.getNombre());
        gerente.setApellido(dto.getApellido());
        gerente.setNumeroDeCedula(dto.getNumeroDeCedula());
        return gerente;
    }
}