package ru.turbogoose;

import ru.turbogoose.pool.BalancingStrategy;
import ru.turbogoose.pool.FixedThreadPool;
import ru.turbogoose.pool.ThreadPool;
import ru.turbogoose.task.Task;
import ru.turbogoose.task.TaskScheduler;

public class Application {
    public static void main(String[] args) {
        try (ThreadPool pool = new FixedThreadPool(10, BalancingStrategy.WORK_STEALING)) {
            TaskScheduler scheduler = new TaskScheduler(pool);

            Task<String> task1 = scheduler.schedule(() -> {
                System.out.println("Task1 started");
                sleep(3000);
                return "Hello";
            });

            Task<Integer> task2 = scheduler.continueWith(task1, s -> {
                System.out.println("Task2 started");
                sleep(2000);
                return s.length();
            });

            sleep(1000);
            System.out.println("Task1 completed: " + task1.isCompleted());
            System.out.println("Result: " + task1.result());

            sleep(1000);
            System.out.println("Task2 completed: " + task2.isCompleted());
            System.out.println("Result: " + task2.result());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private static void sleep(long ms) {
        try {
          Thread.sleep(ms);
        } catch (InterruptedException ignore) {}
    }
}
