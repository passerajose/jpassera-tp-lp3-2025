package py.edu.uc.jpasseratplp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoPorHora;

@Component
public class EmpleadoPorHoraMapper extends BaseMapper<EmpleadoPorHora, EmpleadoDto> {

    @Override
    public EmpleadoDto toDto(EmpleadoPorHora entity) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setNumeroDeCedula(entity.getNumeroDeCedula());
        dto.setTipoEmpleado("EMPLEADO_POR_HORA");
        dto.setInformacionCompleta(entity.obtenerInformacionCompleta());
        // mapCommonFields(entity, dto);
        return dto;
    }

    @Override
    public EmpleadoPorHora toEntity(EmpleadoDto dto) {
        EmpleadoPorHora empleado = new EmpleadoPorHora();
        empleado.setId(dto.getId());
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setNumeroDeCedula(dto.getNumeroDeCedula());
        return empleado;
    }
}