package ru.turbogoose.thread;

import ru.turbogoose.deque.Deque;
import ru.turbogoose.task.Task;

import java.util.Map;

public class WorkStealingThread extends Thread {
    private final Map<Long, Deque<Task<?>>> context;

    public WorkStealingThread(Map<Long, Deque<Task<?>>> context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
    }
}
