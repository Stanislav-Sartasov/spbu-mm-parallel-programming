package ru.turbogoose.task;


import java.util.concurrent.Callable;

public class TaskImpl<T> implements Task<T> {
    private final Callable<T> task;
    private volatile T result = null;
    private volatile Exception suppressedException = null;

    public TaskImpl(Callable<T> task) {
        this.task = task;
    }

    @Override
    public void run() {
        try {
            result = task.call();
        } catch (Exception exc) {
            suppressedException = exc;
        }
    }

    @Override
    public boolean isCompleted() {
        return result != null;
    }

    @Override
    public T result() {
        while (result == null && suppressedException == null) {
            Thread.yield();
        }
        if (suppressedException != null) {
            throw new RuntimeException(suppressedException);
        }
        return result;
    }
}
