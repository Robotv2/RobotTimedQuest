package fr.robotv2.bukkit.util;

import fr.robotv2.bukkit.RTQBukkitPlugin;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class BukkitFuture<T> {

    private final RTQBukkitPlugin plugin;
    private final CompletableFuture<T> future;

    public BukkitFuture(RTQBukkitPlugin plugin, CompletableFuture<T> future) {
        Objects.requireNonNull(plugin, "Plugin cannot be null");
        Objects.requireNonNull(future, "Future cannot be null");
        this.plugin = plugin;
        this.future = future;
    }

    public static <T> BukkitFuture<T> from(CompletableFuture<T> future, RTQBukkitPlugin plugin) {
        return new BukkitFuture<>(plugin, future);
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public BukkitFuture<Void> thenAcceptBukkit(Consumer<T> consumer) {
        return thenApplyBukkit((t) -> {
            consumer.accept(t);
            return null;
        });
    }

    public <U> BukkitFuture<U> thenApplyBukkit(Function<? super T,? extends U> fn) {
        CompletableFuture<U> future = this.future.thenApplyAsync((t) -> {
            try {
                return Bukkit.getScheduler().callSyncMethod(plugin, () -> BukkitFuture.this.future.thenApply(fn).join()).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("An error occurred while executing future operation", e);
            }
        });

        return new BukkitFuture<>(plugin, future);
    }

    public BukkitFuture<T> whenCompleteBukkit(BiConsumer<? super T, ? super Throwable> action) {
        Objects.requireNonNull(action);
        CompletableFuture<T> exceptionalFuture = future.whenComplete(action);
        return new BukkitFuture<>(plugin, exceptionalFuture);
    }
}
