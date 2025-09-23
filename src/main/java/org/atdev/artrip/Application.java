package org.atdev.artrip;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJpaAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    @Bean
    public CommandLineRunner testDb(DataSource dataSource) {
        return args -> {
            System.out.println("DB 연결 체크 시작");
            try (var conn = dataSource.getConnection()) {
                System.out.println("DB 연결 성공!");
                System.out.println("JDBC URL: " + conn.getMetaData().getURL());
                System.out.println("DB 제품: " + conn.getMetaData().getDatabaseProductName());
                System.out.println("DB 버전: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("DB 사용자: " + conn.getMetaData().getUserName());
            } catch (Exception e) {
                System.err.println("DB 연결 실패!");
                e.printStackTrace();
            }
        };
    }
}

