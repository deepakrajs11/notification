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
        basePackages = "com.deepakraj.notification.device.repository",
        entityManagerFactoryRef = "deviceEntityManagerFactory",
        transactionManagerRef = "deviceTransactionManager")
public class DeviceDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "notification.datasource.device")
    public JdbcConnectionProperties deviceDataSourceProperties() {
        return new JdbcConnectionProperties();
    }

    @Bean
    public DataSource deviceDataSource(JdbcConnectionProperties deviceDataSourceProperties) {
        return DataSourceBuilder.create()
                .driverClassName(deviceDataSourceProperties.getDriverClassName())
                .url(deviceDataSourceProperties.getUrl())
                .username(deviceDataSourceProperties.getUsername())
                .password(deviceDataSourceProperties.getPassword())
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean deviceEntityManagerFactory(@Qualifier("deviceDataSource") DataSource deviceDataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        Map<String, Object> jpaProperties = Map.of(
                "hibernate.hbm2ddl.auto", "update",
                "hibernate.show_sql", "false"
        );
        EntityManagerFactoryBuilder builder = new EntityManagerFactoryBuilder(vendorAdapter, ds -> jpaProperties, null);
        return builder
                .dataSource(deviceDataSource)
                .packages("com.deepakraj.notification.device.entity")
                .persistenceUnit("device")
                .build();
    }

    @Bean
    public PlatformTransactionManager deviceTransactionManager(
            @Qualifier("deviceEntityManagerFactory") LocalContainerEntityManagerFactoryBean deviceEntityManagerFactory) {
        return new JpaTransactionManager(deviceEntityManagerFactory.getObject());
    }
}
