package py.edu.uc.jpasseratplp32025.dto;

import java.math.BigDecimal;

public class EmpleadoTiempoCompletoImpuestoDto {

    private Long empleadoId;
    private BigDecimal montoImpuesto;
    private boolean datosValidos;
    private String informacionCompleta; // <-- nuevo campo

    public EmpleadoTiempoCompletoImpuestoDto() {
    }

    public EmpleadoTiempoCompletoImpuestoDto(Long empleadoId, BigDecimal montoImpuesto, boolean datosValidos, String informacionCompleta) {
        this.empleadoId = empleadoId;
        this.montoImpuesto = montoImpuesto;
        this.datosValidos = datosValidos;
        this.informacionCompleta = informacionCompleta;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public BigDecimal getMontoImpuesto() {
        return montoImpuesto;
    }

    public void setMontoImpuesto(BigDecimal montoImpuesto) {
        this.montoImpuesto = montoImpuesto;
    }

    public boolean isDatosValidos() {
        return datosValidos;
    }

    public void setDatosValidos(boolean datosValidos) {
        this.datosValidos = datosValidos;
    }

    public String getInformacionCompleta() {
        return informacionCompleta;
    }

    public void setInformacionCompleta(String informacionCompleta) {
        this.informacionCompleta = informacionCompleta;
    }
}