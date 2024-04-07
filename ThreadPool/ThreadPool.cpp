#include "ThreadPool.h"

template <Strategy strategy> void ThreadPool<strategy>::worker_thread(int index)
{
    while (!stop_flag_.test(std::memory_order_relaxed)) {
        if constexpr (strategy == Strategy::WorkSharing) {
            std::unique_lock<std::mutex> lock(queue_mutex_);

            condition_.wait(lock, [this] {
                return stop_flag_.test(std::memory_order_relaxed) ||
                       !tasks_[0].empty();
            });

            if (stop_flag_.test(std::memory_order_relaxed) && tasks_[0].empty())
                return;

            // task_[0] is not empty now, so we can safely call task()
            tasks_[0].dequeue().value()();
        } else {
            std::function<void()> task;
            bool found = false;

            // Try own queue first
            auto opt_task = tasks_[index].dequeue();
            if (opt_task.has_value()) {
              task = *opt_task;
              found = true;
            } else {
              size_t workers_size = workers_size_.load(std::memory_order_relaxed);
              for (size_t i = 0; i < workers_size; ++i) {
                auto opt_task_steal = tasks_[get_index()].dequeue();
                if (opt_task_steal.has_value()) {
                  task = *opt_task_steal;
                  found = true;
                  break;
                }
              }
            }

            if (found) {
                task();
            } else {
                // No task was found, and this thread will sleep for a short
                // duration to reduce busy waiting
                std::this_thread::sleep_for(std::chrono::milliseconds(1));
            }
        }
    }
}

template <Strategy strategy>
ThreadPool<strategy>::ThreadPool(size_t threads)
    : tasks_(strategy == Strategy::WorkSharing ? 1 : threads)
{
    for (size_t i = 0; i < threads; ++i)
        workers_.emplace_back([this, i] { worker_thread(i); });

    workers_size_.store(threads, std::memory_order_relaxed);
}

template <Strategy strategy> ThreadPool<strategy>::~ThreadPool()
{
    stop_flag_.test_and_set(std::memory_order_relaxed);

    if constexpr (strategy == Strategy::WorkSharing)
        condition_.notify_all();

    for (std::thread &worker : workers_) {
        if (worker.joinable())
            worker.join();
    }
}

template class ThreadPool<Strategy::WorkSharing>;
template class ThreadPool<Strategy::WorkStealing>;
