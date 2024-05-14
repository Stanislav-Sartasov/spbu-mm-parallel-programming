package ru.turbogoose.deanery.core;

import org.springframework.stereotype.Component;
import ru.turbogoose.deanery.core.sets.Set;


@Component
public class ExamSystem {
    private final Set<Exam> exams;

    public ExamSystem(Set<Exam> exams) {
        this.exams = exams;
    }

    public void add(long studentId, long courseId) {
        exams.add(new Exam(studentId, courseId));
    }

    public void remove(long studentId, long courseId) {
        exams.remove(new Exam(studentId, courseId));
    }

    public boolean contains(long studentId, long courseId) {
        return exams.contains(new Exam(studentId, courseId));
    }

    public int count() {
        return exams.size();
    }
}