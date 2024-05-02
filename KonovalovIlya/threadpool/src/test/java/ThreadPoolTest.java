import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.turbogoose.pool.FixedThreadPool;
import ru.turbogoose.pool.ThreadPool;
import ru.turbogoose.task.Task;
import ru.turbogoose.task.TaskScheduler;
import ru.turbogoose.thread.BalancingStrategy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {
    @Test
    @DisplayName("Check number of created threads")
    public void shouldCreatePassedNumberOfThreads() throws Exception {
        int threadCountWithoutPool = Thread.activeCount();
        int poolThreadCount = 10;
        ThreadPool threadPool = new FixedThreadPool(poolThreadCount, BalancingStrategy.WORK_SHARING);
        assertEquals(threadCountWithoutPool + poolThreadCount, Thread.activeCount());
        threadPool.close();
        trySleep(1000);
        assertEquals(threadCountWithoutPool, Thread.activeCount());
    }

    @Test
    @DisplayName("Check result() is blocking and return right answer")
    public void shouldBlockAndReturnResultWhenResultCalled() throws Exception {
        try (ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);
            Task<String> task = scheduler.schedule(() -> {
                trySleep(1000);
                return "bebra";
            });
            assertEquals("bebra", task.result());
        }
    }

    @Test
    @DisplayName("Check isCompleted() not blocking and return status according to execution progress")
    public void shouldNotBlockAndReturnStatusWhenIsCompletedCalled() throws Exception {
        try (ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);
            Task<String> task = scheduler.schedule(() -> {
                trySleep(1000);
                return "bebra";
            });
            assertFalse(task.isCompleted());
            trySleep(2000);
            assertTrue(task.isCompleted());
        }
    }

    @Test
    @DisplayName("Check case when #_tasks > #_threads")
    public void shouldProcessWhenThereAreMoreTasksThanThreads() throws Exception {
        int taskCount = 15;
        List<Task<?>> tasks = new ArrayList<>(taskCount);
        try (ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);

            for (int i = 0; i < taskCount; i++) {
                tasks.add(scheduler.schedule(() -> {
                    trySleep(500);
                    return "bebra";
                }));
            }

            for (Task<?> task : tasks) {
                assertEquals("bebra", task.result());
            }
        }
    }

    @Test
    @DisplayName("Check tasks process sequentially using continueWith()")
    public void shouldProcessContinuedTasksSequentially() throws Exception {
        try (ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);

            Task<String> task1 = scheduler.schedule(() -> {
                trySleep(1000);
                return "1";
            });
            Task<String> task2 = scheduler.continueWith(task1, s -> {
                trySleep(1000);
                return s + " 2";
            });
            Task<String> task3 = scheduler.continueWith(task2, s -> {
                trySleep(1000);
                return s + " 3";
            });

            assertEquals("1 2 3", task3.result());
        }
    }

    @Test
    @DisplayName("Check thread pool does not accept tasks after closing and finish existent tasks")
    public void shouldNotAcceptNewTasksAndFinishExistentWhenClosing() throws Exception {
        ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING);
        TaskScheduler scheduler = new TaskScheduler(threadPool);

        Task<String> task = scheduler.schedule(() -> {
            trySleep(1000);
            return "bebra";
        });

        threadPool.close();
        assertEquals("bebra", task.result());
        assertThrows(IllegalStateException.class, () -> scheduler.schedule(() -> "Blin, ne uspel :("));
    }

    @Test
    @DisplayName("Check exception thrown in task is wrapped and rethrown on result() call ")
    public void shouldRethrowExceptionWhenItIsThrownFromTask() throws Exception {
        try (ThreadPool threadPool = new FixedThreadPool(3, BalancingStrategy.WORK_SHARING)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);

            Task<String> task = scheduler.schedule(() -> {
                throw new IllegalArgumentException();
            });

            RuntimeException exception = assertThrows(RuntimeException.class, task::result);
            assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
        }
    }

    private void trySleep(long ms) {
        try {
            Thread.sleep(ms); // will be interrupted on threadPool.close()
        } catch (InterruptedException exc) {
            Thread.currentThread().interrupt(); // needed to preserve interrupted=true thread status
        }
    }
}
