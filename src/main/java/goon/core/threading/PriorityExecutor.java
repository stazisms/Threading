package goon.core.threading;

import goon.core.Utils.NamedThreadFactory;

import java.util.concurrent.*;

// a thread pool that executes tasks based on their priority
public class PriorityExecutor extends ThreadPoolExecutor {

    public PriorityExecutor(int corePoolSize, String name) {
        super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<>(), new NamedThreadFactory(name));
    }

    // submits a task with a given priority
    public Future<?> submit(Runnable runnable, TaskPriority priority) {
        return super.submit(new PriorityTask(runnable, priority));
    }

    // a wrapper that gives a runnable a priority
    private static final class PriorityTask implements Runnable, Comparable<PriorityTask> {
        private final Runnable runnable;
        private final TaskPriority priority;

        public PriorityTask(Runnable runnable, TaskPriority priority) {
            this.runnable = runnable;
            this.priority = priority;
        }

        @Override
        public void run() {
            runnable.run();
        }

        @Override
        public int compareTo(PriorityTask other) {
            // higher priority enum values have lower ordinal values, so we reverse the comparison
            return other.priority.ordinal() - this.priority.ordinal();
        }
    }
}
