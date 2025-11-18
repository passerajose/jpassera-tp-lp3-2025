package py.edu.uc.jpasseratplp32025.mapper;

import org.springframework.stereotype.Component;
import py.edu.uc.jpasseratplp32025.dto.EmpleadoDto;
import py.edu.uc.jpasseratplp32025.entity.EmpleadoTiempoCompleto;

@Component
public class EmpleadoTiempoCompletoMapper extends BaseMapper<EmpleadoTiempoCompleto, EmpleadoDto> {

    @Override
    public EmpleadoDto toDto(EmpleadoTiempoCompleto entity) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setNumeroDeCedula(entity.getNumeroDeCedula());
        dto.setTipoEmpleado("EMPLEADO_TIEMPO_COMPLETO");
        dto.setInformacionCompleta(entity.obtenerInformacionCompleta());
        // mapCommonFields(entity, dto);
        return dto;
    }

    @Override
    public EmpleadoTiempoCompleto toEntity(EmpleadoDto dto) {
        EmpleadoTiempoCompleto empleado = new EmpleadoTiempoCompleto();
        empleado.setId(dto.getId());
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setNumeroDeCedula(dto.getNumeroDeCedula());
        return empleado;
    }
}