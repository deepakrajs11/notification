package com.deepakraj.notification.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = {
                "com.deepakraj.notification.template.repository",
                "com.deepakraj.notification.queue.repository",
                "com.deepakraj.notification.log.repository"
        },
        entityManagerFactoryRef = "engineEntityManagerFactory",
        transactionManagerRef = "engineTransactionManager")
public class EngineDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "notification.datasource.engine")
    public JdbcConnectionProperties engineDataSourceProperties() {
        return new JdbcConnectionProperties();
    }

    @Bean
    @Primary
    public DataSource engineDataSource(JdbcConnectionProperties engineDataSourceProperties) {
        return DataSourceBuilder.create()
                .driverClassName(engineDataSourceProperties.getDriverClassName())
                .url(engineDataSourceProperties.getUrl())
                .username(engineDataSourceProperties.getUsername())
                .password(engineDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean engineEntityManagerFactory(@Qualifier("engineDataSource") DataSource engineDataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        Map<String, Object> jpaProperties = Map.of(
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.show_sql", "false"
        );
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(vendorAdapter, ds -> jpaProperties, null);
        return builder
                .dataSource(engineDataSource)
                .packages(
                        "com.deepakraj.notification.template.entity",
                        "com.deepakraj.notification.queue.entity",
                        "com.deepakraj.notification.log.entity")
                .persistenceUnit("engine")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager engineTransactionManager(
            @Qualifier("engineEntityManagerFactory") LocalContainerEntityManagerFactoryBean engineEntityManagerFactory) {
        return new JpaTransactionManager(engineEntityManagerFactory.getObject());
    }
}
