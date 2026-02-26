package br.dev.allissonnunes.algashop.ordering;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

public class DataJpaCleanUpExtension implements BeforeEachCallback {

    private static final Logger log = LoggerFactory.getLogger(DataJpaCleanUpExtension.class);

    @Override
    public void beforeEach(final @NonNull ExtensionContext context) throws Exception {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        final EntityManagerFactory entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        final TransactionTemplate transactionTemplate = applicationContext.getBean(TransactionTemplate.class);
        final SessionFactoryImplementor sessionFactoryImplementor = entityManagerFactory.unwrap(SessionFactoryImplementor.class);

        log.info("Truncating mapped objects...");
        transactionTemplate.executeWithoutResult(status -> {
            sessionFactoryImplementor.getSchemaManager().truncateMappedObjects();
        });
        log.info("Truncation finished.");
    }

}
