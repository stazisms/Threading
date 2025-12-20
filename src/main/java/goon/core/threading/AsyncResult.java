package goon.core.threading;

import goon.core.Utils.MainThreadExecutor;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;

// handles a background task and then consumes its result on the main thread
public class AsyncResult<T> {

    private final Future<T> future;

    // starts the background task immediately
    public AsyncResult(Callable<T> backgroundTask) {
        this.future = CThread.submit(backgroundTask);
    }

    // defines what to do with the result on the main thread
    // this method does not block
    public void then(Consumer<T> mainThreadConsumer) {
        CThread.submit(() -> {
            try {
                T result = future.get(); // this blocks the background thread, not the main thread
                MainThreadExecutor.run(() -> mainThreadConsumer.accept(result));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
