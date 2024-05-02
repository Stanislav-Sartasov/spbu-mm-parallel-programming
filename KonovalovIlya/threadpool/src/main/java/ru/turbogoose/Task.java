package ru.turbogoose;


import java.util.concurrent.Callable;

public class Task<T> {
    private final Callable<T> task;
    private volatile T result = null;
    private volatile Exception suppressedException = null;

    public Task(Callable<T> task) {
        this.task = task;
    }

    public void run() {
        try {
            result = task.call();
        } catch (Exception exc) {
            suppressedException = exc;
        }
    }

    public boolean isCompleted() {
        return result != null;
    }

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
