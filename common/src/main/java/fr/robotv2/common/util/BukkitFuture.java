package fr.robotv2.common.util;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BukkitFuture<T> extends CompletableFuture<T> {
    @Override
    public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> fn) {
        return super.thenApply(fn);
    }
}
