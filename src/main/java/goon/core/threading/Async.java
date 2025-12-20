package goon.core.threading;

import goon.core.Utils.MainThreadExecutor;
import goon.core.Utils.Pair;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

// a static utility class for working with CompletableFuture
// makes it easier to create and manage async task pipelines
public final class Async {

    // our shared pool for running async tasks
    private static final Executor executor = CThread.builder().name("Async-Pool").build();

    private Async() {}

    // runs a task asynchronously and returns a future that completes when its done
    public static CompletableFuture<Void> run(Runnable task) {
        return CompletableFuture.runAsync(task, executor);
    }

    // runs a task that supplies a value asynchronously
    public static <T> CompletableFuture<T> supply(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    // adds a timeout to any completable future (Java 8 compatible)
    // if the timeout is reached, the future will complete exceptionally
    public static <T> CompletableFuture<T> withTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        CompletableFuture<T> result = new CompletableFuture<>();

        // schedule a task to fail the future if it doesn't complete in time
        ScheduledFuture<?> timeoutTask = CThread.runLater(() ->
                result.completeExceptionally(new TimeoutException("Future timed out after " + timeout + " " + unit)),
                timeout, unit);

        // when the original future completes, pass its result to our new future
        future.whenComplete((res, ex) -> {
            timeoutTask.cancel(false); // cancel the timeout
            if (ex != null) {
                result.completeExceptionally(ex);
            } else {
                result.complete(res);
            }
        });

        return result;
    }

    // combines the results of two futures into a single future holding a Pair
    public static <A, B> CompletableFuture<Pair<A, B>> combine(CompletableFuture<A> futureA, CompletableFuture<B> futureB) {
        return futureA.thenCombine(futureB, Pair::new);
    }

    // chains a future to consume its result on the main game thread
    public static <T> void onMainThread(CompletableFuture<T> future, Consumer<T> consumer) {
        future.thenAccept(result -> MainThreadExecutor.run(() -> consumer.accept(result)));
    }
}
