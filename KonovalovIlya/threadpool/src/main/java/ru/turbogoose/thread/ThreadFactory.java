package ru.turbogoose.thread;

import ru.turbogoose.deque.Deque;
import ru.turbogoose.task.Task;

import java.util.Map;

public class ThreadFactory {
    public static Thread newThread(BalancingStrategy balancingStrategy, Map<Long, Deque<Task<?>>> context) {
        return switch (balancingStrategy) {
            case WORK_SHARING -> new WorkSharingThread(context);
            case WORK_STEALING -> new WorkStealingThread(context);
        };
    }
}
