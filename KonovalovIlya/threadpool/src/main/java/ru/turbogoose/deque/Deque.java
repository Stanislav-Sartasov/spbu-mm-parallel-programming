package ru.turbogoose.deque;

public interface Deque<T> {
    void pushTail(T element);
    T popTail();
    void pushHead(T element);
    T popHead();
    int size();
    boolean isEmpty();
}
