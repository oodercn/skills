package net.ooder.skill.org.ldap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "net.ooder")
public class LdapOrgApplication {

    public static void main(String[] args) {
        SpringApplication.run(LdapOrgApplication.class, args);
    }
}
