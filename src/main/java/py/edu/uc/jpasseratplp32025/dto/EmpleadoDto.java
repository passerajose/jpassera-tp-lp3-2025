package py.edu.uc.jpasseratplp32025.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) para la entidad Empleado.
 * Utiliza Lombok para generar automáticamente getters, setters, constructores y toString.
 */
@Data // Incluye @Getter, @Setter, @ToString, @EqualsAndHashCode
@AllArgsConstructor // Genera un constructor con todos los campos
@NoArgsConstructor // Opcional, genera un constructor sin argumentos (útil para frameworks como Spring/Jackson)
public class EmpleadoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String numeroDeCedula;
    private String tipoEmpleado; // Para identificar la subclase (TiempoCompleto, Contratista, etc.)
    private String informacionCompleta; // Resultado del método polimórfico
}