package ru.sbrf.pg;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author Onishenko Artem
 */
@Slf4j
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() throws IOException {
        int port = 15000;
        DataSource ds = EmbeddedPostgres.builder()
                                        .setPort(port)
                                        .start()
                                        .getPostgresDatabase();
        log.warn("PG embedded start on {} port", port);

        return ds;
    }
}


