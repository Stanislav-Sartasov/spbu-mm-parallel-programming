package ru.turbogoose.task;

import java.util.function.Supplier;

public class TaskImpl<T> implements Task<T> {
    private final Supplier<T> task;
    private volatile T result = null;

    public TaskImpl(Supplier<T> task) {
        this.task = task;
    }

    @Override
    public void run() {
        result = task.get();
    }

    @Override
    public boolean isCompleted() {
        return result != null;
    }

    @Override
    public T result() {
        while (result == null) {
            Thread.yield();
        }
        return result;
    }
}
