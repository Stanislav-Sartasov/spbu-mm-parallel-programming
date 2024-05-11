package ru.turbogoose.deanery.core.sets;

public class LazySet<T> implements Set<T> {
    private final Node<T> head;
    private final Node<T> tail;

    public LazySet() {
        this.head = new Node<>(Integer.MIN_VALUE);
        this.tail = new Node<>(Integer.MAX_VALUE);
        this.head.next = this.tail;
    }

    @Override
    public boolean add(T elem) {
        int key = elem.hashCode();
        while (true) {
            Node<T> prev = head;
            Node<T> cur = head.next;
            while (cur.key < key) {
                prev = cur;
                cur = cur.next;
            }
            synchronized (prev) {
                synchronized (cur) {
                    if (validate(prev, cur)) {
                        if (cur.key == key) {
                            return false;
                        } else {
                            Node<T> newNode = new Node<>(elem);
                            newNode.next = cur;
                            prev.next = newNode;
                            return true;
                        }
                    }
                }
            }
        }
    }

    private boolean validate(Node<T> prev, Node<T> cur) {
        return !prev.marked && !cur.marked && prev.next == cur;
    }

    @Override
    public boolean remove(T elem) {
        int key = elem.hashCode();
        while (true) {
            Node<T> prev = head;
            Node<T> cur = head.next;
            while (cur.key < key) {
                prev = cur;
                cur = cur.next;
            }
            synchronized (prev) {
                synchronized (cur) {
                    if (validate(prev, cur)) {
                        if (cur.key != key) {
                            return false;
                        } else {
                            cur.marked = true;
                            prev.next = cur.next;
                            return true;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(T elem) {
        int key = elem.hashCode();
        Node<T> cur = head;
        while (cur.key < key) {
            cur = cur.next;
        }
        return cur.key == key && !cur.marked;
    }

    @Override
    synchronized public int size() {
        int size = 0;
        Node<T> cur = head;
        while (cur.next != null) {
            size++;
            cur = head.next;
        }
        return size - 2;
    }

    private static class Node<T> {
        T value;
        int key;
        Node<T> next;
        boolean marked = false;

        public Node(int key) {
            this.key = key;
        }

        public Node(T value) {
            this.value = value;
            this.key = value.hashCode();
        }
    }
}
