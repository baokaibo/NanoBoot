package org.nanoboot.data.mybatis;

import org.nanoboot.annotation.Annotation.Component;
import org.nanoboot.data.datasource.MySQLDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.io.PrintWriter;

/**
 * MyBatis配置和会话工厂管理
 */
@Component
public class MyBatisConfiguration {

    private SqlSessionFactory sqlSessionFactory;

    public void configure(MySQLDataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();

        Environment environment = new Environment("development", transactionFactory, new DataSourceAdapter(dataSource));

        org.apache.ibatis.session.Configuration configuration =
            new org.apache.ibatis.session.Configuration(environment);

        // 可以在这里添加Mapper扫描等配置
        configuration.setLazyLoadingEnabled(true);
        configuration.setAggressiveLazyLoading(false);

        this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * 适配器类，将MySQLDataSource包装为javax.sql.DataSource
     */
    private static class DataSourceAdapter implements DataSource {
        private final MySQLDataSource mysqlDataSource;

        public DataSourceAdapter(MySQLDataSource mysqlDataSource) {
            this.mysqlDataSource = mysqlDataSource;
        }

        @Override
        public java.sql.Connection getConnection() throws SQLException {
            return mysqlDataSource.getConnection();
        }

        @Override
        public java.sql.Connection getConnection(String username, String password) throws SQLException {
            // 使用配置的用户名密码，忽略传入参数
            return mysqlDataSource.getConnection();
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            if (iface.isAssignableFrom(getClass())) {
                return iface.cast(this);
            }
            throw new SQLException("Cannot unwrap to " + iface.getName());
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return iface.isAssignableFrom(getClass());
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter out) throws SQLException {
            // 不支持
        }

        @Override
        public void setLoginTimeout(int seconds) throws SQLException {
            // 不支持
        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public java.util.logging.Logger getParentLogger() throws java.sql.SQLFeatureNotSupportedException {
            throw new java.sql.SQLFeatureNotSupportedException();
        }
    }
}