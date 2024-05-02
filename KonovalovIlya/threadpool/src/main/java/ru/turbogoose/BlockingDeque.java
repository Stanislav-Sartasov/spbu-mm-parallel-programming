package ru.turbogoose;

import java.util.ArrayDeque;

/**
 * Simple synchronized wrapper on java.util.Deque
 */
public class BlockingDeque<T> {
    private final java.util.Deque<T> deque = new ArrayDeque<>();

    public synchronized void pushTail(T element) {
        deque.addLast(element);
    }

    public synchronized T popTail() {
        return deque.pollLast();
    }

    public synchronized void pushHead(T element) {
        deque.addFirst(element);
    }

    public synchronized T popHead() {
        return deque.pollFirst();
    }

    public synchronized int size() {
        return deque.size();
    }

    public synchronized boolean isEmpty() {
        return deque.isEmpty();
    }
}
