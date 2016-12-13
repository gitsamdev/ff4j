package org.ff4j.utils;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface KeyValueMapHelper<T> extends Function<String, T>, Serializable {
    
    default String key() {
        return functionalMethod().getParameters()[0].getName();
    }

    default T value() {
        return apply(key());
    }

    default Method functionalMethod() {
        final SerializedLambda serialzedLabmda = serializedLambda();
        final Class<?> implementationClass = implementationClass(serialzedLabmda);
        return Stream.of(implementationClass.getDeclaredMethods())
                .filter(m -> Objects.equals(m.getName(), serialzedLabmda.getImplMethodName())).findFirst()
                .orElseThrow(RuntimeException::new);
    }

    default Class<?> implementationClass(SerializedLambda serializedLambda) {
        try {
            final String className = serializedLambda.getImplClass().replaceAll("/", ".");
            return Class.forName(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    default SerializedLambda serializedLambda() {
        try {
            final Method replaceMethod = getClass().getDeclaredMethod("writeReplace");
            replaceMethod.setAccessible(true);
            return (SerializedLambda) replaceMethod.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SafeVarargs
    static <V> Map<String, V> mapOf(KeyValueMapHelper<V>... mappings) {
        return Stream.of(mappings).collect(Collectors.toMap(KeyValueMapHelper::key, KeyValueMapHelper::value));
    }
}