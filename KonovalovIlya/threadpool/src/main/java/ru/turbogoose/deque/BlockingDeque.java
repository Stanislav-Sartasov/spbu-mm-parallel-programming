package ru.turbogoose.deque;

import java.util.ArrayDeque;

/**
 * Simple synchronized wrapper on java.util.Deque
 */
public class BlockingDeque<T> implements Deque<T> {
    private final java.util.Deque<T> deque = new ArrayDeque<>();

    @Override
    public synchronized void pushTail(T element) {
        deque.addLast(element);
    }

    @Override
    public synchronized T popTail() {
        return deque.removeLast();
    }

    @Override
    public synchronized void pushHead(T element) {
        deque.addFirst(element);
    }

    @Override
    public synchronized T popHead() {
        return deque.removeFirst();
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
