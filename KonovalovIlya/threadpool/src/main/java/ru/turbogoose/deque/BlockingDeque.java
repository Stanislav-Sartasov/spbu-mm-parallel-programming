package ru.turbogoose.deque;

import java.util.ArrayDeque;

/**
 * Simple synchronized wrapper on java.util.Deque
 */
public class BlockingDeque<T> implements Deque<T> {
    private final java.util.Deque<T> deque = new ArrayDeque<>();

    @Override
    public synchronized T popTail() {
        return deque.pollLast();
    }

    @Override
    public synchronized void pushHead(T element) {
        deque.addFirst(element);
    }

    @Override
    public synchronized T popHead() {
        return deque.pollFirst();
    }

    @Override
    public synchronized int size() {
        return deque.size();
    }

    @Override
    public synchronized boolean isEmpty() {
        return deque.isEmpty();
    }
}
