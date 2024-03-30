#pragma once

#include <atomic>
#include <functional>
#include <future>
#include <thread>
#include <vector>
#include "ThreadSafeQueue.h"

class ThreadPool {
private:
    std::vector<std::thread> workers_;
    ThreadSafeQueue<std::function<void()>> tasks_;
    std::atomic_flag stop_flag_ = ATOMIC_FLAG_INIT;
    std::condition_variable condition_;
    std::mutex queue_mutex_;
    std::atomic_int active_tasks_{0};

    void worker_thread();

public:
    explicit ThreadPool(size_t threads = std::thread::hardware_concurrency());
    ~ThreadPool();

    template <typename F>
    auto enqueue(F&& f) -> std::future<decltype(f())> {
        using return_type = decltype(f());

        auto task_ptr = std::make_shared<std::packaged_task<return_type()>>(std::forward<F>(f));
        std::future<return_type> res = task_ptr->get_future();
        {
            std::unique_lock<std::mutex> lock(queue_mutex_);

            if (stop_flag_.test(std::memory_order_relaxed))
                throw std::runtime_error("enqueue on stopped ThreadPool");

            tasks_.enqueue([task_ptr]() { (*task_ptr)(); });
        }

        condition_.notify_one();
        return res;
    }
};

