package ru.turbogoose.thread;

import ru.turbogoose.BlockingDeque;
import ru.turbogoose.Task;

import java.util.Map;

public class ThreadFactory {
    public static Thread newThread(BalancingStrategy balancingStrategy, Map<Long, BlockingDeque<Task<?>>> context) {
        return switch (balancingStrategy) {
            case WORK_SHARING -> new WorkSharingThread(context);
            case WORK_STEALING -> new WorkStealingThread(context);
        };
    }
}
