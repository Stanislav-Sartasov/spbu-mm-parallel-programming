#include "ThreadPool.h"

void ThreadPool::worker_thread() {
  while (!stop_flag_.test(std::memory_order_relaxed)) {
    std::function<void()> task;

    {
      std::unique_lock<std::mutex> lock(queue_mutex_);
      condition_.wait(lock, [this] {
        return stop_flag_.test(std::memory_order_relaxed) || !tasks_.empty();
      });
      if (stop_flag_.test(std::memory_order_relaxed) && tasks_.empty())
        return;

      task = tasks_.dequeue();
    }

    if (task) {
      task();
    }
  }
}

ThreadPool::ThreadPool(size_t threads) {
  for (size_t i = 0; i < threads; ++i) {
    workers_.emplace_back([this] { worker_thread(); });
  }
}

ThreadPool::~ThreadPool() {
  stop_flag_.test_and_set(std::memory_order_relaxed);
  condition_.notify_all();
  for (std::thread &worker : workers_) {
    if (worker.joinable())
      worker.join();
  }
}

