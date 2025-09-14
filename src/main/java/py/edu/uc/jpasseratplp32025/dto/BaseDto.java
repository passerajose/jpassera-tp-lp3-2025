package py.edu.uc.jpasseratplp32025.dto;

public abstract class BaseDto {
    private int statusCode;
    private String technicalMessage;
    private String userMessage;

    public BaseDto() {
        this.statusCode = 200; // Default success status
        this.technicalMessage = "";
        this.userMessage = "";
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTechnicalMessage() {
        return technicalMessage;
    }

    public void setTechnicalMessage(String technicalMessage) {
        this.technicalMessage = technicalMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}