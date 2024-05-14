package ru.turbogoose.deanery.core;

import java.util.Objects;

public class Exam {
    private final long studentId;
    private final long courseId;

    public Exam(long studentId, long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }

    public long getStudentId() {
        return studentId;
    }

    public long getCourseId() {
        return courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exam exam = (Exam) o;
        return studentId == exam.studentId && courseId == exam.courseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseId);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "studentId=" + studentId +
                ", courseId=" + courseId +
                '}';
    }
}
