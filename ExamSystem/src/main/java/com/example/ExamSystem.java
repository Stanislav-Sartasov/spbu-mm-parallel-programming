package com.example;

public interface ExamSystem {
    void add(long studentId, long courseId);
    void remove(long studentId, long courseId);
    boolean contains(long studentId, long courseId);
    int count();
}
