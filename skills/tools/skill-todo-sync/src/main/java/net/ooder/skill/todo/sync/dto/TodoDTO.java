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
public class TodoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String todoId;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String dueDate;
    private String dueTime;
    private String reminderTime;
    private String assignee;
    private String creator;
    private List<String> tags;
    private String platform;
    private String platformTodoId;
    private String createTime;
    private String updateTime;
    private String completeTime;
    private Integer progress;
    private String parentId;
    private List<String> subTaskIds;
}
