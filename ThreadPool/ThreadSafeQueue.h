#pragma once

#include <condition_variable>
#include <functional>
#include <mutex>
#include <queue>
#include <optional>

template <typename T> class ThreadSafeQueue {
private:
    std::mutex mtx;
    std::queue<T> queue;

public:
    void enqueue(T &&value)
    {
        std::lock_guard<std::mutex> lock(mtx);
        queue.emplace(std::forward<T>(value));
    }

    bool empty()
    {
        std::lock_guard<std::mutex> lock(mtx);
        return queue.empty();
    }

    std::optional<T> dequeue()
    {
        std::lock_guard<std::mutex> lock(mtx);
        if (!queue.empty()) {
            T value = std::move(queue.front());
            queue.pop();
            return std::optional<T>(std::move(value));
        }
        return {};
    }
};
