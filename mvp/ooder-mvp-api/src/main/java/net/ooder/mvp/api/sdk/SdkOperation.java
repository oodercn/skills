package net.ooder.mvp.api.sdk;

@FunctionalInterface
public interface SdkOperation<T> {
    T execute() throws Exception;
}
