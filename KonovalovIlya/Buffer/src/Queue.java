import java.util.Deque;
import java.util.LinkedList;

public class Queue {
    private volatile Deque<String> buffer = new LinkedList<>();
    private final AtomicLock lock = new AtomicLock();

    public void enqueue(String val) {
        try {
            lock.lock();
            buffer.addFirst(val);
            System.out.println(this);
        } finally {
            lock.unlock();
        }
    }

    public String dequeue() {
        try {
            lock.lock();
            String element = buffer.removeLast();
            System.out.println(this);
            return element;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    @Override
    public String toString() {
        return "Queue " + buffer;
    }
}
