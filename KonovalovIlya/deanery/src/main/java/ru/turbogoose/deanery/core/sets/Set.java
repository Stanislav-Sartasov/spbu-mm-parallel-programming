package ru.turbogoose.deanery.core.sets;

public interface Set<T> {
    boolean add(T elem);
    boolean remove(T elem);
    boolean contains(T elem);
    int size();
}
