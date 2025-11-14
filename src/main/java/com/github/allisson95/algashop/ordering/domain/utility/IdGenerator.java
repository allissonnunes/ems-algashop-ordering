package com.github.allisson95.algashop.ordering.domain.utility;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;

import java.util.UUID;

public final class IdGenerator {

    private static final TimeBasedEpochGenerator TIME_BASED_UUID_GENERATOR = Generators.timeBasedEpochGenerator();

    private IdGenerator() {
        throw new IllegalStateException("Utility class");
    }

    public static UUID generate() {
        return TIME_BASED_UUID_GENERATOR.generate();
    }

}
