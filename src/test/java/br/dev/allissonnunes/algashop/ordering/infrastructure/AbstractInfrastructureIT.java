package br.dev.allissonnunes.algashop.ordering.infrastructure;

import br.dev.allissonnunes.algashop.ordering.DataJpaCleanUpExtension;
import br.dev.allissonnunes.algashop.ordering.DataSourceProxyQueryCountConfiguration;
import br.dev.allissonnunes.algashop.ordering.TestcontainersConfiguration;
import br.dev.allissonnunes.algashop.ordering.infrastructure.persistence.SpringDataJpaConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@Import({ TestcontainersConfiguration.class, SpringDataJpaConfiguration.class, DataSourceProxyQueryCountConfiguration.class })
@DataJpaTest(
        showSql = false,
        useDefaultFilters = false,
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*(Persistence)?(Provider|EntityAssembler|EntityDisassembler)"),
        }
)
@ExtendWith(DataJpaCleanUpExtension.class)
public abstract class AbstractInfrastructureIT {

}
