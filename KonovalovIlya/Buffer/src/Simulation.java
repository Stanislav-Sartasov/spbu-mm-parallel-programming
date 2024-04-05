import java.util.List;
import java.util.Scanner;

public class Simulation {
    public static void main(String[] args) {
        Queue queue = new Queue();
        List<Thread> consumersAndProducers = Factory.createConsumersAndProducers(queue);

        consumersAndProducers.forEach(Thread::start);

        try (Scanner sc = new Scanner(System.in)) {
            sc.nextLine();
            consumersAndProducers.forEach(Thread::interrupt);
        }
    }
}
