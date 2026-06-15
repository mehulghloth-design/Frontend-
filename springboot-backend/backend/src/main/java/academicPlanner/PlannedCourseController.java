package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/planned-courses")
@CrossOrigin(origins = "*")
public class PlannedCourseController {

    @Autowired
    private PlannedCourseService plannedCourseService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> savePlannedCourse(@RequestBody PlannedCourseRequest request) {
        if (request.getPlanId() == null || request.getCourseId() == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "planId and courseId are required");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            PlannedCourse saved = plannedCourseService.savePlannedCourse(request.getPlanId(), request.getCourseId());

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Planned course saved successfully");
            response.put("data", saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{planId}")
    public ResponseEntity<Map<String, Object>> getCoursesByPlan(@PathVariable Long planId) {
        List<PlannedCourse> plannedCourses = plannedCourseService.getCoursesByPlanId(planId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Planned courses fetched successfully");
        response.put("count", plannedCourses.size());
        response.put("data", plannedCourses);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String test() {
        return "PlannedCourseController is working";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlannedCourse(@PathVariable Long id) {
        plannedCourseService.deletePlannedCourse(id);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Planned course deleted successfully");
        response.put("deletedPlannedCourseId", id);

        return ResponseEntity.ok(response);
    }

    public static class PlannedCourseRequest {
        private Long planId;
        private Long courseId;

        public Long getPlanId() {
            return planId;
        }

        public void setPlanId(Long planId) {
            this.planId = planId;
        }

        public Long getCourseId() {
            return courseId;
        }

        public void setCourseId(Long courseId) {
            this.courseId = courseId;
        }
    }
}