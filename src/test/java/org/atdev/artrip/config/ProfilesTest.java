package org.atdev.artrip.config;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"common", "local"})
public class ProfilesTest {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("프로파일 설정값 확인")
    void localConfigurationLoadTest() {
        //given

        //when
        String[] activeProfiles = env.getActiveProfiles();

        //then
        assertThat(activeProfiles).contains("common", "local");
    }

}
