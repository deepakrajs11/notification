package com.deepakraj.notification.config.datasource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.deepakraj.notification.contact.repository",
        entityManagerFactoryRef = "contactEntityManagerFactory",
        transactionManagerRef = "contactTransactionManager")
public class ContactDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "notification.datasource.contact")
    public JdbcConnectionProperties contactDataSourceProperties() {
        return new JdbcConnectionProperties();
    }

    @Bean
    public DataSource contactDataSource(JdbcConnectionProperties contactDataSourceProperties) {
        return DataSourceBuilder.create()
                .driverClassName(contactDataSourceProperties.getDriverClassName())
                .url(contactDataSourceProperties.getUrl())
                .username(contactDataSourceProperties.getUsername())
                .password(contactDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean contactEntityManagerFactory(@Qualifier("contactDataSource") DataSource contactDataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        Map<String, Object> jpaProperties = Map.of(
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.show_sql", "false"
        );
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(vendorAdapter, ds -> jpaProperties, null);
        return builder
                .dataSource(contactDataSource)
                .packages("com.deepakraj.notification.contact.entity")
                .persistenceUnit("contact")
                .build();
    }

    @Bean
    public PlatformTransactionManager contactTransactionManager(
            @Qualifier("contactEntityManagerFactory") LocalContainerEntityManagerFactoryBean contactEntityManagerFactory) {
        return new JpaTransactionManager(contactEntityManagerFactory.getObject());
    }
}
