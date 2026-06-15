package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Courses fetched successfully");
        response.put("count", courses.size());
        response.put("data", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(course -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Course fetched successfully");
                    response.put("data", course);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Course not found");
                    response.put("courseId", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    // SEARCH ENDPOINT REMOVED

    @GetMapping("/department/{department}")
    public ResponseEntity<Map<String, Object>> getCoursesByDepartment(@PathVariable String department) {
        List<Course> courses = courseService.getCoursesByDepartment(department);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Courses filtered by department");
        response.put("department", department);
        response.put("count", courses.size());
        response.put("data", courses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/level/{level}")
    public ResponseEntity<Map<String, Object>> getCoursesByLevel(@PathVariable String level) {
        List<Course> courses = courseService.getCoursesByLevel(level);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Courses filtered by level");
        response.put("level", level);
        response.put("count", courses.size());
        response.put("data", courses);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody Course course) {
        Course savedCourse = courseService.saveCourse(course);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Course created successfully");
        response.put("data", savedCourse);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody Course updatedCourse) {
        return courseService.getCourseById(id)
                .map(existingCourse -> {
                    existingCourse.setCode(updatedCourse.getCode());
                    existingCourse.setName(updatedCourse.getName());
                    existingCourse.setCredits(updatedCourse.getCredits());
                    existingCourse.setDepartment(updatedCourse.getDepartment());
                    existingCourse.setLevel(updatedCourse.getLevel());
                    existingCourse.setDescription(updatedCourse.getDescription());

                    Course savedCourse = courseService.saveCourse(existingCourse);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Course updated successfully");
                    response.put("data", savedCourse);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Course not found");
                    response.put("courseId", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        return courseService.getCourseById(id)
                .map(course -> {
                    courseService.deleteCourse(id);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Course deleted successfully");
                    response.put("deletedCourseId", id);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Course not found");
                    response.put("courseId", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }
}