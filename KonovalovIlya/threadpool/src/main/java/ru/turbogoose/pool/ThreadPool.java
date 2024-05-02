package ru.turbogoose.pool;

import ru.turbogoose.task.Task;

public interface ThreadPool extends AutoCloseable {
    void enqueue(Task<?> task);
}
