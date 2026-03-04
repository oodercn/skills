package net.ooder.skill.scene.dto.llm;

public class ChatResponseDTO {
    
    private String response;
    private String model;
    private String provider;
    private boolean error;
    private String errorMessage;
    
    public ChatResponseDTO() {}
    
    public ChatResponseDTO(String response, String model, String provider) {
        this.response = response;
        this.model = model;
        this.provider = provider;
        this.error = false;
    }
    
    public static ChatResponseDTO success(String response, String model, String provider) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setResponse(response);
        dto.setModel(model);
        dto.setProvider(provider);
        dto.setError(false);
        return dto;
    }
    
    public static ChatResponseDTO error(String errorMessage) {
        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setError(true);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
