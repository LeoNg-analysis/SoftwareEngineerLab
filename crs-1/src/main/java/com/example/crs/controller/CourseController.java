package com.example.crs.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Dependency Injection
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.beans.factory.annotation.Autowired;  

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.crs.model.Course;
import com.example.crs.model.CourseRepository;

@CrossOrigin(origins = "http://localhost:5173") // used for vue
@RestController
@RequestMapping("/api")
public class CourseController {

	@Autowired
	CourseRepository courseRepository;

	////http://localhost:8080/api/
	@GetMapping("/")
public String home()
{
	return "API is working";
}

	//http://localhost:8080/api/courses
//api/courses?title=software/
	@GetMapping("/courses")
	public ResponseEntity<List<Course>> getAllCourses(@RequestParam(required = false) String title) {
		try {
			List<Course> courses = new ArrayList<Course>();

			if (title == null)
				courseRepository.findAll().forEach(courses::add);
				
			else
				courseRepository.findByTitleContaining(title).forEach(courses::add);

			if (courses.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}

			return new ResponseEntity<>(courses, HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

//			return new ResponseEntity<>(null,     
//                                HttpStatus.INTERNAL_SERVER_ERROR);


//			return new ResponseEntity
//                               .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                               .build();
		}
	}

	@GetMapping("/courses/{id}")
	public ResponseEntity<Course> getCourseById(@PathVariable("id") long id) {
		Optional<Course> courseData = courseRepository.findById(id);

		if (courseData.isPresent()) {
			return new ResponseEntity<>(courseData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping("/courses")
	public ResponseEntity<Course> createTutorial(@RequestBody Course course) {
		try {
			Course _course = courseRepository.save(new Course(course.getCode(), course.getTitle()));
			return new ResponseEntity<>(_course, HttpStatus.CREATED);//201
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/courses/{id}")
	public ResponseEntity<Course> updateCourse(@PathVariable("id") long id, @RequestBody Course course) {
		Optional<Course> courseData = courseRepository.findById(id);

		if (courseData.isPresent()) {
			Course _course = courseData.get();
			_course.setCode(course.getCode());
			_course.setTitle(course.getTitle());
			return new ResponseEntity<>(courseRepository.save(_course), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/courses/{id}")
	public ResponseEntity<HttpStatus> deleteCourse(@PathVariable("id") long id) {
		try {
			courseRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/courses")
	public ResponseEntity<HttpStatus> deleteAllTutorials() {
		try {
			courseRepository.deleteAll();
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
/*	@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String studentId = loginRequest.get("studentId");
        String password = loginRequest.get("password");

        // for testing only
        if ("1001".equals(studentId) && "123456".equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", studentId);
            response.put("name", "Test Student");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid Student ID or Password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

*/

}
