package br.dev.allissonnunes.algashop.ordering.domain.model;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import io.hypersistence.tsid.TSID;

import java.util.UUID;

public final class IdGenerator {

    private static final TimeBasedEpochGenerator TIME_BASED_UUID_GENERATOR = Generators.timeBasedEpochGenerator();

    private static final TSID.Factory TSID_FACTORY = TSID.Factory.INSTANCE;

    private IdGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID generateTimeBasedUUID() {
        return TIME_BASED_UUID_GENERATOR.generate();
    }

    /*
     * TSID_NODE
     * TSID_NODE_COUNT
     */
    public static TSID gererateTSID() {
        return TSID_FACTORY.generate();
    }

}
