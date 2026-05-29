package com.example.math_race.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DatabaseConfig {

    private final Environment env;

    @Autowired
    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() throws Exception {
        String dbUser,dbSchema, dbPass,host;
        Integer port;

        dbUser = env.getProperty("DB_ROOT");
        dbSchema = env.getProperty("DB_SCHEMA");
        dbPass = env.getProperty("DB_PASSWORD");
        host = env.getProperty("DB_HOST");
        port = env.getProperty("DB_PORT", Integer.class);

        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setDriverClass("com.mysql.cj.jdbc.Driver");

        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, dbSchema);
        dataSource.setJdbcUrl(jdbcUrl);

        dataSource.setUser(dbUser);
        dataSource.setPassword(dbPass);

        dataSource.setMaxPoolSize(20);
        dataSource.setMinPoolSize(5);
        dataSource.setInitialPoolSize(5);
        dataSource.setIdleConnectionTestPeriod(3600);
        dataSource.setTestConnectionOnCheckin(true);

        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() throws Exception {
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());

        Properties hibernateProperties = new Properties();
        hibernateProperties.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.put("hibernate.jdbc.batch_size", 50);
        hibernateProperties.put("hibernate.connection.characterEncoding", "utf8");
        hibernateProperties.put("hibernate.enable_lazy_load_no_trans", "true");

        sessionFactoryBean.setHibernateProperties(hibernateProperties);
        sessionFactoryBean.setMappingResources("objects.hbm.xml");

        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager() throws Exception {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }
}
