import java.util.ArrayList;
import java.util.List;

public class Factory {
    private static final int PRODUCER_COUNT = 2;
    private static final int CONSUMER_COUNT = 2;

    public static List<Thread> createConsumersAndProducers(Queue queue) {
        List<Thread> result = createConsumers(queue);
        result.addAll(createProducers(queue));
        return result;
    }

    public static List<Thread> createConsumers(Queue queue) {
        List<Thread> consumers = new ArrayList<>(CONSUMER_COUNT);
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            consumers.add(new Consumer("C" + i, queue));
        }
        return consumers;
    }

    public static List<Thread> createProducers(Queue queue) {
        List<Thread> producers = new ArrayList<>(PRODUCER_COUNT);
        for (int i = 0; i < PRODUCER_COUNT; i++) {
            producers.add(new Producer("P" + i, queue));
        }
        return producers;
    }
}
