package br.dev.allissonnunes.algashop.ordering.application;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.MapStructTestConfiguration;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@ExtendWith(DataJpaCleanUpExtension.class)
@Import({ MapStructTestConfiguration.class, TestcontainersConfiguration.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public abstract class AbstractApplicationIT {

}
