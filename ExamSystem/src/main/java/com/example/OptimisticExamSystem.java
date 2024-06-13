package com.example;

import java.util.concurrent.atomic.AtomicInteger;

public class OptimisticExamSystem implements ExamSystem {
    private final Node head;
    private final Node tail;
    private final AtomicInteger size;

    public OptimisticExamSystem() {
        this.head = new Node(Integer.MIN_VALUE);
        this.tail = new Node(Integer.MAX_VALUE);
        this.head.next = this.tail;
        this.size = new AtomicInteger(0);
    }

    @Override
    public void add(long studentId, long courseId) {
        int key = computeKey(studentId, courseId);
        while (true) {
            Node pred = head;
            Node curr = head.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            synchronized (pred) {
                synchronized (curr) {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return;
                        } else {
                            Node newNode = new Node(key);
                            newNode.next = curr;
                            pred.next = newNode;
                            size.incrementAndGet();
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void remove(long studentId, long courseId) {
        int key = computeKey(studentId, courseId);
        while (true) {
            Node pred = head;
            Node curr = head.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            synchronized (pred) {
                synchronized (curr) {
                    if (validate(pred, curr)) {
                        if (curr.key != key) {
                            return;
                        } else {
                            pred.next = curr.next;
                            size.decrementAndGet();
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(long studentId, long courseId) {
        int key = computeKey(studentId, courseId);
        Node curr = head;
        while (curr.key < key) {
            curr = curr.next;
        }
        return curr.key == key;
    }

    @Override
    public int count() {
        int currentCount = size.get();
        return currentCount;
    }

    private int computeKey(long studentId, long courseId) {
        return (int) (studentId * 31 + courseId);
    }

    private boolean validate(Node pred, Node curr) {
        return pred.next == curr;
    }

    private static class Node {
        final int key;
        Node next;

        Node(int key) {
            this.key = key;
        }
    }
}

