package br.dev.allissonnunes.algashop.ordering;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class DataJpaCleanUpExtension implements BeforeEachCallback {

    @Override
    public void beforeEach(final @NonNull ExtensionContext context) throws Exception {
        final ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
        final EntityManagerFactory entityManagerFactory = applicationContext.getBean(EntityManagerFactory.class);
        final SessionFactoryImplementor sessionFactoryImplementor = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        sessionFactoryImplementor.getSchemaManager().truncateMappedObjects();
    }

}
