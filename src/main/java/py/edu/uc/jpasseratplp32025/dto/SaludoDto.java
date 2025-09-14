package py.edu.uc.jpasseratplp32025.dto;

public class SaludoDto extends BaseDto {
    private String mensaje;
    private String timestamp;

    public SaludoDto() {
        super();
    }

    public SaludoDto(String mensaje) {
        super();
        this.mensaje = mensaje;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}