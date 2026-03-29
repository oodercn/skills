package net.ooder.skill.todo.sync.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoSyncResultDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String platform;
    private Boolean success;
    private Integer totalTodos;
    private Integer createdTodos;
    private Integer updatedTodos;
    private Integer completedTodos;
    private Integer failedTodos;
    private List<String> errors;
    private String syncTime;
}
