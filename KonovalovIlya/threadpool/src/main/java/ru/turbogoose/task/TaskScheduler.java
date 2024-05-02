package ru.turbogoose.task;

import ru.turbogoose.pool.ThreadPool;

import java.util.concurrent.Callable;
import java.util.function.Function;

public class TaskScheduler {
    private final ThreadPool threadPool;

    public TaskScheduler(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }

    public <T> Task<T> schedule(Callable<T> action) {
        TaskImpl<T> task = new TaskImpl<>(action);
        threadPool.enqueue(task);
        return task;
    }

    public <T, C> Task<C> continueWith(Task<T> task, Function<T, C> continuation) {
        return schedule(() -> continuation.apply(task.result()));
    }
}
