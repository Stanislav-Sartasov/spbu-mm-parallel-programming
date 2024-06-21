package Tasks.Task2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SemaphoreBuffer {
    private final List<Integer> buffer = new LinkedList<>();
    private final Semaphore producerSemaphore;
    private final Semaphore consumerSemaphore;

    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 2;
    private static final int OPERATIONS_BEFORE_PAUSE = 2;

    private int producedCount = 0;
    private int consumedCount = 0;

    SemaphoreBuffer() {
        producerSemaphore = new Semaphore(1);
        consumerSemaphore = new Semaphore(0);
    }

    class Producer extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    producerSemaphore.acquire();
                    int item = genItem();
                    buffer.add(item);
                    System.out.println("Producer id:" + Thread.currentThread().getName() +  ", generated: " + item);
                    producedCount++;
                    if (producedCount % OPERATIONS_BEFORE_PAUSE == 0) {
                        Thread.sleep(2000);
                    }
                    consumerSemaphore.release();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }

        private int genItem() {
            return (int) (Math.random() * 100);
        }
    }

    class Consumer extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    consumerSemaphore.acquire();
                    int item = buffer.removeFirst();
                    System.out.println("Consumer id: " + Thread.currentThread().getName() + ", consumed: " + item);
                    consumedCount++;
                    if (consumedCount % OPERATIONS_BEFORE_PAUSE == 0) {
                        Thread.sleep(2000);
                    }
                    producerSemaphore.release();
                } catch (InterruptedException e) {
                    interrupt();
                }
            }
        }
    }

    public void start() {
        List<Thread> producerThreads = new ArrayList<>();
        List<Thread> consumerThreads = new ArrayList<>();

        for (int i = 0; i < NUM_PRODUCERS; i++) {
            Thread producerThread = new Producer();
            producerThreads.add(producerThread);
            producerThread.start();
        }

        for (int i = 0; i < NUM_CONSUMERS; i++) {
            Thread consumerThread = new Consumer();
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }

        System.out.println("numProducers: " + NUM_PRODUCERS + " numConsumers: " + NUM_CONSUMERS);
        System.out.println("Press any key to stop");

        try {
            System.in.read();
            System.out.println("Stopping...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Thread producerThread : producerThreads) {
            producerThread.interrupt();
        }

        for (Thread consumerThread : consumerThreads) {
            consumerThread.interrupt();
        }
    }

    public static void main(String[] args) {
        new SemaphoreBuffer().start();
    }
}
