package ru.turbogoose.deanery.web;

import org.springframework.web.bind.annotation.*;
import ru.turbogoose.deanery.core.ExamSystem;

@RestController
@RequestMapping("/exams")
public class ExamController {
    private final ExamSystem examSystem;

    public ExamController(ExamSystem examSystem) {
        this.examSystem = examSystem;
    }

    @PostMapping
    public void addExam(@RequestParam long studentId, @RequestParam long courseId) {
        examSystem.add(studentId, courseId);
    }

    @DeleteMapping
    public void deleteExam(@RequestParam long studentId, @RequestParam long courseId) {
        examSystem.remove(studentId, courseId);
    }

    @GetMapping
    public boolean examExists(@RequestParam long studentId, @RequestParam long courseId) {
        return examSystem.contains(studentId, courseId);
    }

    @GetMapping("/count")
    public int getExamCount() {
        return examSystem.count();
    }
}
