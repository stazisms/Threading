package goon.core.threading;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Function;

// wraps an object to provide thread-safe read/write access using a ReadWriteLock
// ideal for objects that are read often but written to less frequently
public class GuardedObject<T> {

    private final T object;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public GuardedObject(T object) {
        this.object = object;
    }

    // performs a read operation on the object
    // multiple threads can read at the same time
    public <R> R read(Function<T, R> action) {
        lock.readLock().lock();
        try {
            return action.apply(object);
        } finally {
            lock.readLock().unlock();
        }
    }

    // performs a write operation on the object
    // blocks all other reads and writes until the action is complete
    public void write(Consumer<T> action) {
        lock.writeLock().lock();
        try {
            action.accept(object);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
