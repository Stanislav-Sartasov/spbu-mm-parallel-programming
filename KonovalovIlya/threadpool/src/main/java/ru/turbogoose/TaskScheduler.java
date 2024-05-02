package ru.turbogoose;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class TaskScheduler {
    private final FixedThreadPool threadPool;

    public TaskScheduler(FixedThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public <T> Task<T> schedule(Callable<T> action) {
        Task<T> task = new Task<>(action);
        threadPool.enqueue(task);
        return task;
    }

    public <T, C> Task<C> continueWith(Task<T> task, Function<T, C> continuation) {
        return schedule(() -> continuation.apply(task.result()));
    }
}
