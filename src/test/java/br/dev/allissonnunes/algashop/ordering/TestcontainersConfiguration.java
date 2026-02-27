package br.dev.allissonnunes.algashop.ordering;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse("postgres:18-alpine");

    private static final PostgreSQLContainer POSTGRESQL_CONTAINER
            = new PostgreSQLContainer(POSTGRES_IMAGE).withDatabaseName("ordering_test");

    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return POSTGRESQL_CONTAINER;
    }

}
