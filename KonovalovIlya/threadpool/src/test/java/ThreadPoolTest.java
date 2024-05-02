import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.turbogoose.FixedThreadPool;
import ru.turbogoose.Task;
import ru.turbogoose.TaskScheduler;
import ru.turbogoose.thread.BalancingStrategy;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ThreadPoolTest {
    @ParameterizedTest
    @EnumSource(BalancingStrategy.class) // test runs for every enum value (it passes as method parameter)
    @DisplayName("Check number of created threads")
    public void shouldCreatePassedNumberOfThreads(BalancingStrategy balancingStrategy) {
        int threadCountWithoutPool = Thread.activeCount();
        int poolThreadCount = 10;
        FixedThreadPool threadPool = new FixedThreadPool(poolThreadCount, balancingStrategy);
        assertEquals(threadCountWithoutPool + poolThreadCount, Thread.activeCount());
        threadPool.close();
        trySleep(1000);
        assertEquals(threadCountWithoutPool, Thread.activeCount());
    }

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check result() is blocking and return right answer")
    public void shouldBlockAndReturnResultWhenResultCalled(BalancingStrategy balancingStrategy) {
        try (FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy)) {
            TaskScheduler scheduler = new TaskScheduler(threadPool);
            Task<String> task = scheduler.schedule(() -> {
                trySleep(1000);
                return "bebra";
            });
            assertEquals("bebra", task.result());
        }
    }

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check isCompleted() not blocking and return status according to execution progress")
    public void shouldNotBlockAndReturnStatusWhenIsCompletedCalled(BalancingStrategy balancingStrategy) {
        try (FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy)) {
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

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check case when #_tasks > #_threads")
    public void shouldProcessWhenThereAreMoreTasksThanThreads(BalancingStrategy balancingStrategy) {
        int taskCount = 15;
        List<Task<?>> tasks = new ArrayList<>(taskCount);
        try (FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy)) {
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

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check tasks process sequentially using continueWith()")
    public void shouldProcessContinuedTasksSequentially(BalancingStrategy balancingStrategy) {
        try (FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy)) {
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

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check thread pool does not accept tasks after closing and finish existent tasks")
    public void shouldNotAcceptNewTasksAndFinishExistentWhenClosing(BalancingStrategy balancingStrategy) {
        FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy);
        TaskScheduler scheduler = new TaskScheduler(threadPool);

        Task<String> task = scheduler.schedule(() -> {
            trySleep(1000);
            return "bebra";
        });

        threadPool.close();
        assertEquals("bebra", task.result());
        assertThrows(IllegalStateException.class, () -> scheduler.schedule(() -> "Blin, ne uspel :("));
    }

    @ParameterizedTest
    @EnumSource(BalancingStrategy.class)
    @DisplayName("Check exception thrown in task is wrapped and rethrown on result() call ")
    public void shouldRethrowExceptionWhenItIsThrownFromTask(BalancingStrategy balancingStrategy) {
        try (FixedThreadPool threadPool = new FixedThreadPool(3, balancingStrategy)) {
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
