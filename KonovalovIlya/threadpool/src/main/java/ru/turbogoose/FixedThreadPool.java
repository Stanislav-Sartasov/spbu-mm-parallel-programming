package ru.turbogoose;

import ru.turbogoose.thread.BalancingStrategy;
import ru.turbogoose.thread.ThreadFactory;

import java.util.*;

public class FixedThreadPool implements AutoCloseable {
    private final Random random = new Random();
    private final int threadCount;
    private final List<Thread> threads;
    private final Map<Long, BlockingDeque<Task<?>>> context; // thread id -> thread's task queue
    private boolean closed;

    public FixedThreadPool(int threadCount, BalancingStrategy balancingStrategy) {
        if (threadCount < 1) {
            throw new IllegalArgumentException("Thread count must be positive");
        }
        this.threadCount = threadCount;
        this.closed = false;

        context = new HashMap<>(threadCount);
        threads = new ArrayList<>(threadCount);
        for (int i = 0; i < this.threadCount; i++) {
            Thread newThread = ThreadFactory.newThread(balancingStrategy, context);
            context.putIfAbsent(newThread.getId(), new BlockingDeque<>());
            threads.add(newThread);
        }
        threads.forEach(Thread::start);
        System.out.println("Thread pool initialized and running");
    }

    public void enqueue(Task<?> task) {
        if (closed) {
            throw new IllegalStateException("Thread pool is closed");
        }
        int threadNum = random.nextInt(threadCount);
        long threadId = threads.get(threadNum).getId();
        context.get(threadId).pushHead(task);
    }

    @Override
    public void close() {
        closed = true;
        threads.forEach(Thread::interrupt);
        System.out.println("Thread pool closed");
    }
}
