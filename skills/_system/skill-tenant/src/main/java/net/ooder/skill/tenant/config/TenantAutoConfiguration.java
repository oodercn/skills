package net.ooder.skill.tenant.config;

import net.ooder.skill.tenant.repository.TenantMemberRepository;
import net.ooder.skill.tenant.repository.TenantRepository;
import net.ooder.skill.tenant.service.TenantService;
import net.ooder.skill.tenant.service.impl.TenantServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages = "net.ooder.skill.tenant")
@EnableJpaRepositories(basePackages = "net.ooder.skill.tenant.repository")
@ConditionalOnProperty(name = "skill.tenant.enabled", havingValue = "true", matchIfMissing = true)
public class TenantAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(TenantAutoConfiguration.class);

    public TenantAutoConfiguration() {
        log.info("[TenantAutoConfiguration] Initializing multi-tenant skill module with JPA persistence");
    }

    @Bean
    public TenantService tenantService(TenantRepository tenantRepository, TenantMemberRepository memberRepository) {
        log.info("[TenantAutoConfiguration] Creating TenantService bean");
        return new TenantServiceImpl(tenantRepository, memberRepository);
    }

    @Bean
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource tenantDataSource() {
        String dbType = System.getProperty("skill.tenant.db.type", "sqlite");

        if ("mysql".equalsIgnoreCase(dbType)) {
            log.info("[TenantAutoConfiguration] Using MySQL database");
            return DataSourceBuilder.create()
                    .url(System.getProperty("skill.tenant.db.url",
                            "jdbc:mysql://localhost:3306/ooder_tenant?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true"))
                    .username(System.getProperty("skill.tenant.db.username", "root"))
                    .password(System.getProperty("skill.tenant.db.password", ""))
                    .driverClassName("com.mysql.cj.jdbc.Driver")
                    .build();
        } else {
            log.info("[TenantAutoConfiguration] Using SQLite database (default)");
            String dbPath = System.getProperty("skill.tenant.db.path",
                    System.getProperty("user.home") + "/.ooder/data/tenant.db");
            return DataSourceBuilder.create()
                    .url("jdbc:sqlite:" + dbPath)
                    .driverClassName("org.sqlite.JDBC")
                    .build();
        }
    }

    @Bean
    @ConditionalOnMissingBean(LocalContainerEntityManagerFactoryBean.class)
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("net.ooder.skill.tenant.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(false);
        vendorAdapter.setGenerateDdl(true);
        emf.setJpaVendorAdapter(vendorAdapter);

        Properties jpaProperties = new Properties();

        String dbType = System.getProperty("skill.tenant.db.type", "sqlite");
        if ("mysql".equalsIgnoreCase(dbType)) {
            jpaProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        } else {
            jpaProperties.setProperty("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
            jpaProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        }

        jpaProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        jpaProperties.setProperty("hibernate.format_sql", "false");
        emf.setJpaProperties(jpaProperties);

        return emf;
    }

    @Bean
    @ConditionalOnMissingBean(PlatformTransactionManager.class)
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }
}
