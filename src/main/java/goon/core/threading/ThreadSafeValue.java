package goon.core.threading;

// a simple, thread-safe wrapper for a single value
// useful for sharing simple data like booleans or counters between threads
public class ThreadSafeValue<T> {

    private T value;
    private final Object lock = new Object();

    public ThreadSafeValue(T initialValue) {
        this.value = initialValue;
    }

    // gets the current value in a thread-safe way
    public T get() {
        synchronized (lock) {
            return value;
        }
    }

    // sets a new value in a thread-safe way
    public void set(T newValue) {
        synchronized (lock) {
            this.value = newValue;
        }
    }
}
