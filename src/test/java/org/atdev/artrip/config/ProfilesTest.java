package org.atdev.artrip.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles({"common", "local"})
public class ProfilesTest {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("프로파일 설정값 확인")
    void localConfigurationLoadTest() {
        //given

        //when
        String[] activeProfiles = env.getActiveProfiles();

        //then
        assertThat(activeProfiles).contains("common", "local");
    }

    @Test
    @DisplayName("데이터 베이스 연결 확인")
    void databaseConnectionTest() {
        //given

        //when
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isClosed()).isFalse();
            System.out.println("연결 성공: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            fail("실패 : " + e.getMessage());
        }
    }

}
