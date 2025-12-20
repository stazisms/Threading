package goon.core.threading;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

// a static utility for running multiple tasks in parallel
public final class Parallel {

    private Parallel() {}

    // runs all given tasks in parallel
    // returns a single future that completes when all tasks are finished
    public static CompletableFuture<Void> run(Runnable... tasks) {
        CompletableFuture<?>[] futures = Arrays.stream(tasks)
                .map(Async::run)
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures);
    }

    // runs all given tasks in parallel and collects their results
    // returns a future that will contain a list of all the results when complete
    public static <T> CompletableFuture<List<T>> run(Callable<T>... tasks) {
        List<CompletableFuture<T>> futures = Arrays.stream(tasks)
                .map(callable -> Async.supply(() -> {
                    try {
                        return callable.call();
                    } catch (Exception e) {
                        // wrap checked exceptions so the future can handle them
                        throw new CompletionException(e);
                    }
                }))
                .collect(Collectors.toList());

        CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allDoneFuture.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join) // .join() is safe here because all futures are already complete
                        .collect(Collectors.toList())
        );
    }
}
