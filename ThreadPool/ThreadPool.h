#pragma once

#include <atomic>
#include <functional>
#include <future>
#include <iostream>
#include <random>
#include <thread>
#include <vector>

#include "ThreadSafeQueue.h"

enum class Strategy { WorkSharing, WorkStealing };

template <Strategy strategy> class ThreadPool {
private:
    std::vector<std::thread> workers_;
    std::atomic<int> workers_size_{0};
    std::vector<ThreadSafeQueue<std::function<void()>>> tasks_;
    std::atomic_flag stop_flag_ = ATOMIC_FLAG_INIT;
    std::condition_variable condition_;
    std::mutex queue_mutex_;
    std::mt19937 rng{ std::random_device{}()}; // Only used for WorkStealing strategy
    std::mutex rng_mutex; // Mutex to protect rng access

    std::condition_variable work_stealing_cond_;
    std::mutex work_stealing_mutex_;

    void worker_thread(int index);

    int get_index()
    {
        if constexpr (strategy == Strategy::WorkSharing)
            return 0;

        std::lock_guard<std::mutex> lock(rng_mutex);
        int max = workers_size_.load(std::memory_order_relaxed) - 1;

        return std::uniform_int_distribution<int>{0, max}(rng);
    }

public:
    explicit ThreadPool(size_t threads = std::thread::hardware_concurrency());
    ~ThreadPool();

    template <typename F> auto enqueue(F &&f)
    {
        if (stop_flag_.test(std::memory_order_relaxed))
            throw std::runtime_error("enqueue on stopped ThreadPool");

        using return_type = typename std::result_of<F()>::type;
        auto task_ptr = std::make_shared<std::packaged_task<return_type()>>(std::forward<F>(f));
        std::future<return_type> res = task_ptr->get_future();
        {
            std::unique_lock<std::mutex> lock(queue_mutex_);
            tasks_[get_index()].enqueue([task_ptr] { (*task_ptr)(); });
        }

        if constexpr (strategy == Strategy::WorkSharing)
            condition_.notify_one();

        return res;
    }
};
