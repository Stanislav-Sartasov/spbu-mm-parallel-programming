import java.util.Random;

public class Consumer extends Thread {
    private static final int MIN_SLEEP_DURATION = 4000;
    private static final int MAX_SLEEP_DURATION = 6000;
    private final Random random = new Random();
    private final Queue queue;
    private final String name;

    public Consumer(String name, Queue queue) {
        this.queue = queue;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " started");
        while (!Thread.currentThread().isInterrupted()) {
            if (!queue.isEmpty()) {
                String task = queue.dequeue();
                System.out.printf("%s dequeued %s%n%n", name, task);
            }
            try {
                Thread.sleep(random.nextInt(MIN_SLEEP_DURATION, MAX_SLEEP_DURATION));
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(name + " finished");
    }
}
