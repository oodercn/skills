package net.ooder.skill.todo;

import net.ooder.skill.todo.service.TodoService;
import net.ooder.skill.todo.service.impl.TodoServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "skill.todo.enabled", havingValue = "true", matchIfMissing = true)
public class TodoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(TodoService.class)
    public TodoService todoService() {
        return new TodoServiceImpl();
    }
}