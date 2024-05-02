package ru.turbogoose.thread;

import ru.turbogoose.deque.Deque;
import ru.turbogoose.task.Task;

import java.util.Map;

public class WorkSharingThread extends Thread {
    private final Map<Long, Deque<Task<?>>> context;

    public WorkSharingThread(Map<Long, Deque<Task<?>>> context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();
    }
}
