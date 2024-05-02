package ru.turbogoose.task;

public interface Task<T> {
    void run();
    boolean isCompleted();
    T result();
}
