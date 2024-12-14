package org.orecruncher.dsurround.lib.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Container {
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Supplier<?>> factories = new HashMap<>();

    public <T> Container registerSingleton(Class<T> type, T instance) {
        instances.put(type, instance);
        return this;
    }

    public <T> Container registerSingleton(T instance) {
        return registerSingleton((Class<T>) instance.getClass(), instance);
    }

    public <T> Container registerSingleton(Class<T> type, Class<? extends T> implementation) {
        try {
            var instance = implementation.getDeclaredConstructor().newInstance();
            return registerSingleton(type, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + implementation.getName(), e);
        }
    }

    public <T> Container registerFactory(Class<T> type, Supplier<T> factory) {
        factories.put(type, factory);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        var instance = instances.get(type);
        if (instance != null) {
            return (T) instance;
        }

        var factory = factories.get(type);
        if (factory != null) {
            return (T) factory.get();
        }

        throw new RuntimeException("No registration found for type " + type.getName());
    }
} 