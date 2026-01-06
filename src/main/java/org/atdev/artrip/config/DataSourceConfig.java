package org.atdev.artrip.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {

    @Bean
    @Profile("local")
    public DataSource localDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:33069/artrip?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("artrip1!");
        return dataSource;
    }

    @Bean
    @Profile("dev")
    public DataSource devDataSource(
            @Value("${DB_HOST}") String dbHost,
            @Value("${DB_PORT}") int dbPort,
            @Value("${DB_NAME}") String dbName,
            @Value("${DB_USERNAME}") String username,
            @Value("${DB_PASSWORD}") String password
    ) {

        HikariDataSource dataSource = new HikariDataSource();
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true",
                dbHost, dbPort, dbName);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;

    }

}
