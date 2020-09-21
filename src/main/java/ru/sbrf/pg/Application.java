package ru.sbrf.pg;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

/**
 * @author Onishenko Artem
 */
@Slf4j
@SpringBootApplication
public class Application {

    private static final List<String> DEFAULT_ADDITIONAL_INIT_DB_PARAMS = Arrays.asList("--nosync", "--locale=en_US.UTF-8");

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @DependsOn("postgresProcess")
    public DataSource dataSource(PostgresConfig config) {

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(String.format("jdbc:postgresql://%s:%s/%s", config.net().host(), config.net().port(), config.storage().dbName()));
        ds.setUsername(config.credentials().username());
        ds.setPassword(config.credentials().password());

        return ds;
    }

    /**
     * @param config the PostgresConfig configuration to use to start Postgres db process
     * @return PostgresProcess , the started db process
     */
    @Bean(destroyMethod = "stop")
    public PostgresProcess postgresProcess(PostgresConfig config) throws IOException {
        log.error("user.home = {}", System.getProperty("user.home"));
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
        PostgresExecutable exec = runtime.prepare(config);
        return exec.start();
    }

    /**
     * @return PostgresConfig that contains embedded db configuration like user name , password
     */
    @Bean
    public PostgresConfig embeddedPostgresConfig() throws IOException {
        // make it readable from configuration source file or system , it is hard coded here for explanation purpose only
        final PostgresConfig postgresConfig = new PostgresConfig(
            Version.V10_6,
            new AbstractPostgresConfig.Net("localhost", 15000),
            new AbstractPostgresConfig.Storage("test"),
            new AbstractPostgresConfig.Timeout(),
            new AbstractPostgresConfig.Credentials("user", "pass")
        );
        postgresConfig.getAdditionalInitDbParams().addAll(DEFAULT_ADDITIONAL_INIT_DB_PARAMS);
        return postgresConfig;
    }
}


