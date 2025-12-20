package goon.core.threading;

// an abstract thread that runs a loop until its told to stop
// perfect for background modules that need to run continuously
public abstract class StoppableThread extends Thread {

    private volatile boolean stopped = false;

    public StoppableThread(String name) {
        super(name);
    }

    // the main logic loop, implement this
    protected abstract void onUpdate();

    @Override
    public final void run() {
        while (!stopped) {
            onUpdate();
        }
    }

    // checks if the thread is currently running
    public boolean isRunning() {
        return !stopped && isAlive();
    }

    // stops the thread's loop
    public void stopThread() {
        this.stopped = true;
    }
}
