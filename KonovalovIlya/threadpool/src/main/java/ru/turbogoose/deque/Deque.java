package ru.turbogoose.deque;

public interface Deque<T> {
    T popTail();

    void pushHead(T element);

    T popHead();

    int size();

    boolean isEmpty();
}
