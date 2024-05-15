package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExamSystemController {

    private final ExamSystem examSystem;

    @Autowired
    public ExamSystemController(ExamSystem examSystem) {
        this.examSystem = examSystem;
    }

    @PostMapping("/add")
    public void add(@RequestParam long studentId, @RequestParam long courseId) {
        examSystem.add(studentId, courseId);
    }

    @DeleteMapping("/remove")
    public void remove(@RequestParam long studentId, @RequestParam long courseId) {
        examSystem.remove(studentId, courseId);
    }

    @GetMapping("/contains")
    public boolean contains(@RequestParam long studentId, @RequestParam long courseId) {
        return examSystem.contains(studentId, courseId);
    }

    @GetMapping("/count")
    public int count() {
        return examSystem.count();
    }
}
