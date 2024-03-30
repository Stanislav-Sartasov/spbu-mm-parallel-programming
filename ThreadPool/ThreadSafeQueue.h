#pragma once

#include <condition_variable>
#include <mutex>
#include <queue>
#include <functional>

template<typename T>
class ThreadSafeQueue {
private:
    std::mutex mtx;
    std::queue<T> queue;
    std::condition_variable cond;

public:
    void enqueue(T&& value) {
        std::lock_guard<std::mutex> lock(mtx);
        queue.emplace(std::forward<T>(value));
        cond.notify_one();
    }

    T dequeue() {
        std::unique_lock<std::mutex> lock(mtx);
        cond.wait(lock, [this] { return !queue.empty(); });
        T value = std::move(queue.front());
        queue.pop();
        return value;
    }

    bool empty() {
        std::lock_guard<std::mutex> lock(mtx);
        return queue.empty();
    }
};

