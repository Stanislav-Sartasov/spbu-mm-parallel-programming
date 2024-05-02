package ru.turbogoose.deque;

public interface Deque<T> {
    void pushTail(T element);
    T popHead();
    T popTail();
    boolean isEmpty();
}
