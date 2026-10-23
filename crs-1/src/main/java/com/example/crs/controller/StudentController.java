package com.example.crs.controller;

import com.example.crs.model.Student;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:5173") // Stay consistent with CourseController for Vue
@RestController
@RequestMapping("/api")
public class StudentController {

    // Task 1: Simulate "in-memory" Student "Database"
    private static final Map<String, Student> studentDatabase = new ConcurrentHashMap<>();

    // generate a test account
    static {
        Student defaultStudent = new Student();
        defaultStudent.setStudentId("1001");
        defaultStudent.setPassword("123456");
        defaultStudent.setName("Test Student");
        defaultStudent.setEmail("test@school.edu");
        studentDatabase.put("1001", defaultStudent);
    }

    // ===== Task 2: REGISTER =====
    @PostMapping("/register")
    public ResponseEntity<?> registerStudent(@RequestBody Student newStudent) {

        // 0. ID or password empty -> 400
        if (newStudent.getStudentId() == null || newStudent.getStudentId().trim().isEmpty()
                || newStudent.getPassword() == null || newStudent.getPassword().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student ID and Password cannot be empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // 1. ID already exists -> 409
        if (studentDatabase.containsKey(newStudent.getStudentId())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: Student ID already exists.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // 2. New ID -> save into the map
        studentDatabase.put(newStudent.getStudentId(), newStudent);

        // 3. Success -> 201
        Map<String, String> ok = new HashMap<>();
        ok.put("message", "Registration successful!");
        return ResponseEntity.status(HttpStatus.CREATED).body(ok);
    }

    // ===== Task 3: LOGIN (checks the map) =====
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String studentId = loginRequest.get("studentId");
        String password = loginRequest.get("password");

        // empty -> 400
        if (studentId == null || studentId.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Student ID and Password cannot be empty.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        // look up the student in the map
        Student student = studentDatabase.get(studentId);

        // exists AND password matches -> 200
        if (student != null && student.getPassword().equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", student.getStudentId());
            response.put("name", student.getName());
            return ResponseEntity.ok(response);
        } else {
            // wrong -> 401
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid Student ID or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
}