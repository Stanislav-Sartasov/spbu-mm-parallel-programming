package ru.turbogoose.thread;

import ru.turbogoose.BlockingDeque;
import ru.turbogoose.Task;

import java.util.Map;
import java.util.Random;

public class WorkSharingThread extends Thread {
    private static final int DIFF_THRESHOLD = 10;
    private final Map<Long, BlockingDeque<Task<?>>> context;
    private final Random random;

    public WorkSharingThread(Map<Long, BlockingDeque<Task<?>>> context) {
        this.context = context;
        this.random = new Random();
    }

    @Override
    public void run() {
        Thread me = Thread.currentThread();
        BlockingDeque<Task<?>> myQueue = context.get(me.getId());
        while (!me.isInterrupted() || !myQueue.isEmpty()) {
            Task<?> task = myQueue.popTail();
            if (task != null) {
                task.run();
            }
            if (procBalance(myQueue.size())) {
                long victimId = context.keySet().stream()
                        .skip(random.nextInt(context.size()))
                        .findFirst().orElseThrow();
                BlockingDeque<Task<?>> victimQueue = context.get(victimId);
                balance(myQueue, victimQueue);
            }
        }
    }

    private boolean procBalance(int num) {
        return random.nextInt(num + 1) == num;
    }

    private void balance(BlockingDeque<Task<?>> queue1, BlockingDeque<Task<?>> queue2) {
        int diff = queue1.size() - queue2.size();
        if (Math.abs(diff) < DIFF_THRESHOLD) {
            return;
        }
        BlockingDeque<Task<?>> bigQueue = diff < 0 ? queue2 : queue1;
        BlockingDeque<Task<?>> smallQueue = diff < 0 ? queue1 : queue2;
        while (bigQueue.size() > smallQueue.size()) {
            smallQueue.pushHead(bigQueue.popTail());
        }
    }
}
