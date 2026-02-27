package br.dev.allissonnunes.algashop.ordering.domain.model;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@ExtendWith(DataJpaCleanUpExtension.class)
@Import(TestcontainersConfiguration.class)
@SpringBootTest
public class AbstractDomainIT {

}
