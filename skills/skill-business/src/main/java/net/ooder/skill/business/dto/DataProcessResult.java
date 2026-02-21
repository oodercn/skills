package net.ooder.skill.business.dto;

import lombok.Data;
import java.util.Map;

@Data
public class DataProcessResult {
    private Boolean success;
    private String message;
    private Map<String, Object> data;
    private Long processTime;
    
    public static DataProcessResult success(Map<String, Object> data) {
        DataProcessResult result = new DataProcessResult();
        result.setSuccess(true);
        result.setMessage("Data processed successfully");
        result.setData(data);
        result.setProcessTime(System.currentTimeMillis());
        return result;
    }
    
    public static DataProcessResult fail(String message) {
        DataProcessResult result = new DataProcessResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setProcessTime(System.currentTimeMillis());
        return result;
    }
}
