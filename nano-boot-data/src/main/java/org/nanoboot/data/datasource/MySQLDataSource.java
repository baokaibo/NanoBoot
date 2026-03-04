package org.nanoboot.data.datasource;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.annotation.Annotation.Value;
import org.nanoboot.core.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * MySQL数据源配置和连接管理
 */
@Component
public class MySQLDataSource {

    @Value("${mysql.url:jdbc:mysql://localhost:3306/test}")
    private String url;

    @Value("${mysql.username:root}")
    private String username;

    @Value("${mysql.password:password}")
    private String password;

    @Value("${mysql.driver:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    public Connection getConnection() throws SQLException {
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }

        return DriverManager.getConnection(url, username, password);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}