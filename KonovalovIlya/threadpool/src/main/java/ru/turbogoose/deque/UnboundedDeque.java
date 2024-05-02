package ru.turbogoose.deque;

public class UnboundedDeque<T> implements Deque<T> {
    @Override
    public void pushTail(T element) {

    }

    @Override
    public T popHead() {
        return null;
    }

    @Override
    public T popTail() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
