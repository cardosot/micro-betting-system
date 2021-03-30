package com.tiagocardoso.mbs.datasources.utils;

import org.elasticsearch.action.ActionListener;

import java.util.concurrent.CompletableFuture;

/**
 * {@link ActionListener} implementation that delegates success/failure handling to a bound {@link CompletableFuture}
 */
public final class ActionListenerAdapter<T> implements ActionListener<T> {

    private final CompletableFuture<T> future;

    private ActionListenerAdapter(CompletableFuture<T> future) {
        this.future = future;
    }

    public static <T> ActionListener<T> bindTo(CompletableFuture<T> future) {
        return new ActionListenerAdapter<>(future);
    }

    @Override
    public void onResponse(T response) {
        future.complete(response);
    }

    @Override
    public void onFailure(Exception e) {
        future.completeExceptionally(e);
    }

}