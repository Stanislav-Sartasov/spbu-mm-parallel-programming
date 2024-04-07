import java.util.Random;

public class Producer extends Thread {
    private static final int MIN_SLEEP_DURATION = 2000;
    private static final int MAX_SLEEP_DURATION = 5000;
    private final Random random = new Random();
    private final Queue queue;
    private final String name;

    public Producer(String name, Queue queue) {
        this.queue = queue;
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + " started");
        int counter = 0;
        while (!Thread.currentThread().isInterrupted()) {
            String taskName = name + "-" + counter;
            queue.enqueue(taskName);
            System.out.printf("%s enqueued %s%n%n", name, taskName);
            counter++;
            try {
                Thread.sleep(random.nextInt(MIN_SLEEP_DURATION, MAX_SLEEP_DURATION));
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println(name + " finished");

    }
}
