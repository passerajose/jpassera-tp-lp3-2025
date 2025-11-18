package py.edu.uc.jpasseratplp32025.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmpleadoExterno {
    private Long employeeId;
    private String firstName;
    private String lastName;
    private String identificationNumber;
    private String employeeType;
    private LocalDate birthDate;
    private String department;
}