import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicLock {
    private final AtomicBoolean isLocked = new AtomicBoolean(false);

    public void lock() {
        while (!isLocked.compareAndSet(false, true)) {}
    }

    public void unlock() {
        isLocked.set(false);
    }
}
