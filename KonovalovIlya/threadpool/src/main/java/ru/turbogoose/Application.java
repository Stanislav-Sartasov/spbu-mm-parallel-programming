package ru.turbogoose;

import ru.turbogoose.thread.BalancingStrategy;
import ru.turbogoose.pool.FixedThreadPool;
import ru.turbogoose.pool.ThreadPool;
import ru.turbogoose.task.Task;
import ru.turbogoose.task.TaskScheduler;

import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        try (ThreadPool pool = new FixedThreadPool(10, BalancingStrategy.WORK_STEALING)) {
            TaskScheduler scheduler = new TaskScheduler(pool);
            final int taskCount = 100;
            List<Task<String>> tasks = new ArrayList<>(taskCount);

            for (int i = 0; i < taskCount; i++) {
                int num = i;
                tasks.add(scheduler.schedule(() -> action(num + "", num + "", 2000)));
            }

            tasks.forEach(t -> System.out.println(t.result()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String action(String prev, String name, int sleepDuration) {
        System.out.printf("Task #%s started%n", name);
        sleep(sleepDuration);
        System.out.printf("Task #%s finished%n", name);
        return prev + " bruh";
    }


    private static void sleep(long ms) {
        try {
          Thread.sleep(ms);
        } catch (InterruptedException ignore) {}
    }
}
