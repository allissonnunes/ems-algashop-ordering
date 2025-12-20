package com.github.allisson95.algashop.ordering.infrastructure.persistence.commons;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public final class DomainVersionHandler {

    private static final Map<Class<?>, DomainVersionMethod> CACHE = new ConcurrentHashMap<>();

    private DomainVersionHandler() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T getVersion(final Object target) {
        final DomainVersionMethod domainVersionMethod = getDomainVersionMethod(target.getClass());
        return domainVersionMethod.getVersion(target);
    }

    public static void setVersion(final Object target, final Object version) {
        final DomainVersionMethod domainVersionMethod = getDomainVersionMethod(target.getClass());
        domainVersionMethod.setVersion(target, version);
    }

    private static DomainVersionMethod getDomainVersionMethod(final Class<?> clazz) {
        return CACHE.computeIfAbsent(clazz, DomainVersionMethod::of);
    }

    record DomainVersionMethod(Method getVersionMethod, Method setVersionMethod) {

        DomainVersionMethod {
            requireNonNull(getVersionMethod);
            requireNonNull(setVersionMethod);
        }

        static DomainVersionMethod of(final Class<?> clazz) {
            final Method getVersionMethod = ReflectionUtils.findMethod(clazz, "getVersion");
            final Method setVersionMethod = ReflectionUtils.findMethod(clazz, "setVersion", Long.class);
            return new DomainVersionMethod(getVersionMethod, setVersionMethod);
        }

        @SuppressWarnings("unchecked")
        <T> T getVersion(final Object target) {
            ReflectionUtils.makeAccessible(getVersionMethod);
            final Object versionValue = ReflectionUtils.invokeMethod(getVersionMethod, target);
            getVersionMethod.setAccessible(false);
            return (T) versionValue;
        }

        void setVersion(final Object target, final Object version) {
            ReflectionUtils.makeAccessible(setVersionMethod);
            ReflectionUtils.invokeMethod(setVersionMethod, target, version);
            setVersionMethod.setAccessible(false);
        }

    }

}
