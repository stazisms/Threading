package goon.core.threading;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

// a static utility for working with standard Future objects
public final class FutureUtils {

    private FutureUtils() {}

    // converts a list of futures into a single future that completes with a list of their results
    // this is useful for waiting on a batch of tasks submitted to a standard executor
    public static <T> Future<List<T>> collect(List<Future<T>> futures) {
        // submit a new task that will wait for all the other futures
        Callable<List<T>> collectorTask = () -> futures.stream()
                .map(future -> {
                    try {
                        // .get() blocks until the future is complete
                        return future.get();
                    } catch (Exception e) {
                        // rethrow as a runtime exception to be handled by the final future
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return CThread.submit(collectorTask);
    }
}
