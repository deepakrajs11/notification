package com.deepakraj.notification.config.datasource;

import lombok.Data;

@Data
public class JdbcConnectionProperties {

    private String url;
    private String driverClassName = "org.h2.Driver";
    private String username = "sa";
    private String password = "";
}
