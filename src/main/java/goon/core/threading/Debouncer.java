package goon.core.threading;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// used to run a task only after a period of inactivity
// good for handling spammy events like mouse movement or player input
public class Debouncer {
    private final long delay;
    private final TimeUnit unit;
    private ScheduledFuture<?> future;

    public Debouncer(long delay, TimeUnit unit) {
        this.delay = delay;
        this.unit = unit;
    }

    // call this every time the event happens
    // it will reset the timer and schedule the task to run again
    public void call(Runnable task) {
        // cancel the previously scheduled task
        if (future != null && !future.isDone()) {
            future.cancel(false);
        }
        // schedule the new task
        future = CThread.runLater(task, delay, unit);
    }
}
