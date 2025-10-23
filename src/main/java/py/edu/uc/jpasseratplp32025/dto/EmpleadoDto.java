package py.edu.uc.jpasseratplp32025.dto;

import java.time.LocalDate;

public class EmpleadoDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String numeroDeCedula;
    private String tipoEmpleado; // Para identificar la subclase (TiempoCompleto, Contratista, etc.)
    private String informacionCompleta; // Resultado del método polimórfico

    // Constructor
    public EmpleadoDto(Long id, String nombre, String apellido, String numeroDeCedula, String tipoEmpleado, String informacionCompleta) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.numeroDeCedula = numeroDeCedula;
        this.tipoEmpleado = tipoEmpleado;
        this.informacionCompleta = informacionCompleta;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNumeroDeCedula() {
        return numeroDeCedula;
    }

    public void setNumeroDeCedula(String numeroDeCedula) {
        this.numeroDeCedula = numeroDeCedula;
    }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public String getInformacionCompleta() {
        return informacionCompleta;
    }

    public void setInformacionCompleta(String informacionCompleta) {
        this.informacionCompleta = informacionCompleta;
    }
}