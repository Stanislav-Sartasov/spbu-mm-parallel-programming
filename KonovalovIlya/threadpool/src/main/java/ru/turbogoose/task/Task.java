package ru.turbogoose.task;

import java.util.function.Function;

public interface Task<T> {
    void run();
    boolean isCompleted();
    T result();
    <C> Task<C> continueWith(Function<T, C> continuation);
}
