package net.ooder.skill.scene.dto.llm;

import jakarta.validation.constraints.NotBlank;

public class TranslateRequestDTO {
    
    @NotBlank(message = "鏂囨湰涓嶈兘涓虹┖")
    private String text;
    
    private String sourceLang;
    
    @NotBlank(message = "鐩爣璇█涓嶈兘涓虹┖")
    private String targetLang;
    
    private String model;

    public TranslateRequestDTO() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceLang() {
        return sourceLang;
    }

    public void setSourceLang(String sourceLang) {
        this.sourceLang = sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public void setTargetLang(String targetLang) {
        this.targetLang = targetLang;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
