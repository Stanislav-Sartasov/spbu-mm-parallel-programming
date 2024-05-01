package ru.turbogoose.pool;

import ru.turbogoose.task.Task;

public interface ThreadPool extends AutoCloseable {
    <T> void enqueue(Task<T> task);
}
