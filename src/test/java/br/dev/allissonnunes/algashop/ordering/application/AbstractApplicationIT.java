package br.dev.allissonnunes.algashop.ordering.application;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@ExtendWith(DataJpaCleanUpExtension.class)
@Import(MapStructTestConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
public abstract class AbstractApplicationIT {

    @Container
    @ServiceConnection
    protected static PostgreSQLContainer postgreSQLContainer
            = new PostgreSQLContainer("postgres:18-alpine")
            .withDatabaseName("ordering_test");

}
