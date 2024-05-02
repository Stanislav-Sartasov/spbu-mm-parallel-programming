package ru.turbogoose.thread;

import ru.turbogoose.deque.Deque;
import ru.turbogoose.task.Task;

import java.util.Map;
import java.util.Random;

public class WorkStealingThread extends Thread {
    private final Map<Long, Deque<Task<?>>> context;
    private final Random random;

    public WorkStealingThread(Map<Long, Deque<Task<?>>> context) {
        this.context = context;
        this.random = new Random();
    }

    @Override
    public void run() {
        Thread me = Thread.currentThread();
        Deque<Task<?>> myDeque = context.get(me.getId());
        Task<?> task = myDeque.popTail();
        while (!me.isInterrupted()) {
            while (task != null) {
                task.run();
                task = myDeque.popTail();
            }
            while (task == null && !me.isInterrupted()) {
                Thread.yield();
                long victimId = context.keySet().stream()
                        .skip(random.nextInt(context.size()))
                        .findFirst().orElseThrow();
                Deque<Task<?>> victimQueue = context.get(victimId);
                if (!victimQueue.isEmpty()) {
                    task = victimQueue.popHead();
                }
            }
        }
    }
}
