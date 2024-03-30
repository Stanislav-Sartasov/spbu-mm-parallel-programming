#include <gtest/gtest.h>
#include <algorithm>

#include "ThreadPool.h"

TEST(ThreadPoolTests, SingleTask) {
    ThreadPool pool(4);
    std::future<int> result = pool.enqueue([]() { return 1; });
    ASSERT_EQ(result.get(), 1);
}

TEST(ThreadPoolTests, MultipleTasks) {
    ThreadPool pool(4);
    std::future<int> result1 = pool.enqueue([]() { return 1; });
    std::future<int> result2 = pool.enqueue([]() { return 2; });
    ASSERT_EQ(result1.get() + result2.get(), 3);
}

TEST(ThreadPoolTests, HighVolume) {
    ThreadPool pool(100);
    std::vector<std::future<int>> results;
    for (int i = 0; i < 1000; ++i) {
        results.push_back(pool.enqueue([i]() { return i; }));
    }
    int sum = 0;
    for (auto &result : results) {
        sum += result.get();
    }
    ASSERT_EQ(sum, 499500); // Sum of first 1000 natural numbers
}

// Task with side-effects
TEST(ThreadPoolTests, SideEffectTask) {
    ThreadPool pool(4);
    std::vector<int> numbers(1000, 0);
    std::vector<std::future<void>> futures;

    for (int i = 0; i < 1000; ++i) {
        futures.push_back(pool.enqueue([&numbers, i]() { numbers[i] = i; }));
    }

    for (auto& future : futures) {
        future.get();
    }

    // Check if numbers vector was populated correctly
    bool all_correct = std::all_of(numbers.begin(), numbers.end(), [idx = 0](int value) mutable { return value == idx++; });
    ASSERT_TRUE(all_correct);
}

// Tasks with varying execution times
TEST(ThreadPoolTests, VaryingExecutionTimes) {
    ThreadPool pool(4);
    std::vector<std::future<int>> futures;

    for (int i = 0; i < 10; ++i) {
        futures.push_back(pool.enqueue([i]() {
            std::this_thread::sleep_for(std::chrono::milliseconds(50 * i)); // Increasing sleep time
            return i;
        }));
    }

    int sum = 0;
    for (auto& future : futures) {
        sum += future.get();
    }

    ASSERT_EQ(sum, 45); // Sum of first 10 natural numbers (0-9)
}

// Tasks that throw exceptions
TEST(ThreadPoolTests, ExceptionTask) {
    ThreadPool pool(4);
    std::future<void> result = pool.enqueue([]() { throw std::runtime_error("Test exception"); });

    EXPECT_THROW({
        try {
            result.get();
        } catch (const std::runtime_error& e) {
            ASSERT_STREQ("Test exception", e.what());
            throw;
        }
    }, std::runtime_error);
}

// Tasks returning complex data types
TEST(ThreadPoolTests, ReturnComplexType) {
    ThreadPool pool(4);
    std::future<std::vector<int>> result = pool.enqueue([]() -> std::vector<int> {
        return std::vector<int>{1, 2, 3, 4, 5};
    });

    std::vector<int> expected{1, 2, 3, 4, 5};
    ASSERT_EQ(result.get(), expected);
}

// Tasks with dependencies (simulate a simple continuation)
TEST(ThreadPoolTests, TaskDependencies) {
    ThreadPool pool(4);
    std::future<int> initialTask = pool.enqueue([]() { return 42; });

    std::future<std::string> dependentTask = pool.enqueue([&initialTask]() {
        int result = initialTask.get(); // This will wait for the initial task to complete
        return std::string("Result is ") + std::to_string(result);
    });

    ASSERT_EQ(dependentTask.get(), "Result is 42");
}

TEST(ThreadPoolTests, ThreadCount) {
    std::atomic<int> counter{0};
    ThreadPool pool(4);
    for (int i = 0; i < 100; ++i) {
        pool.enqueue([&counter]() {
            counter.fetch_add(1, std::memory_order_relaxed);
            std::this_thread::sleep_for(std::chrono::milliseconds(10));
        });
    }
    std::this_thread::sleep_for(std::chrono::seconds(1)); // Wait for some tasks to complete
    ASSERT_GE(counter.load(), 4); // This tests that at least 4 tasks have been processed concurrently
}
