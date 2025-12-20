package goon.core.threading;

// a stoppable thread that runs at a fixed "ticks per second" (tps)
// automatically handles sleep timing to maintain the desired rate
public abstract class GameLoopThread extends StoppableThread {

    private final int tps;
    private final long tickIntervalNanos;

    public GameLoopThread(String name, int tps) {
        super(name);
        this.tps = tps;
        this.tickIntervalNanos = 1_000_000_000 / tps;
    }

    // the main logic loop, implement this
    // this will be called at the specified tps
    protected abstract void onTick();

    @Override
    protected final void onUpdate() {
        long startTime = System.nanoTime();

        onTick();

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        long sleepTime = tickIntervalNanos - executionTime;

        if (sleepTime > 0) {
            try {
                // convert nanos to millis and nanos for Thread.sleep
                long sleepMillis = sleepTime / 1_000_000;
                int sleepNanos = (int) (sleepTime % 1_000_000);
                Thread.sleep(sleepMillis, sleepNanos);
            } catch (InterruptedException e) {
                stopThread();
            }
        }
    }
}
