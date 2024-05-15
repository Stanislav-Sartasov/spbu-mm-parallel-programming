package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ExamSystemTest {
    private ExamSystem optimisticExamSystem;
    private ExamSystem lazyExamSystem;

    @Before
    public void setUp() {
        optimisticExamSystem = new OptimisticExamSystem();
        lazyExamSystem = new LazyExamSystem();
    }

    @Test
    public void testAddAndContainsOptimistic() {
        optimisticExamSystem.add(1L, 1L);
        assertTrue(optimisticExamSystem.contains(1L, 1L));
    }

    @Test
    public void testAddAndContainsLazy() {
        lazyExamSystem.add(1L, 1L);
        assertTrue(lazyExamSystem.contains(1L, 1L));
    }

    @Test
    public void testRemoveOptimistic() {
        optimisticExamSystem.add(1L, 1L);
        optimisticExamSystem.remove(1L, 1L);
        assertFalse(optimisticExamSystem.contains(1L, 1L));
    }

    @Test
    public void testRemoveLazy() {
        lazyExamSystem.add(1L, 1L);
        lazyExamSystem.remove(1L, 1L);
        assertFalse(lazyExamSystem.contains(1L, 1L));
    }

    @Test
    public void testCountOptimistic() {
        optimisticExamSystem.add(1L, 1L);
        optimisticExamSystem.add(2L, 2L);
        assertEquals(2, optimisticExamSystem.count());
    }

    @Test
    public void testCountLazy() {
        lazyExamSystem.add(1L, 1L);
        lazyExamSystem.add(2L, 2L);
        assertEquals(2, lazyExamSystem.count());
    }
}
