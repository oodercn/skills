package net.ooder.skill.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder.skill.calendar")
public class CalendarSkillApplication {
    public static void main(String[] args) {
        SpringApplication.run(CalendarSkillApplication.class, args);
    }
}
