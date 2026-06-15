package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
public class SemesterPlanController {

    @Autowired
    private SemesterPlanService semesterPlanService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPlans() {
        List<SemesterPlan> plans = semesterPlanService.getAllPlans();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Semester plans fetched successfully");
        response.put("count", plans.size());
        response.put("data", plans);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getPlansByUser(@PathVariable Long userId) {
        List<SemesterPlan> plans = semesterPlanService.getPlansByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Plans filtered by user");
        response.put("userId", userId);
        response.put("count", plans.size());
        response.put("data", plans);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPlanById(@PathVariable Long id) {
        return semesterPlanService.getPlanById(id)
                .map(plan -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Semester plan fetched successfully");
                    response.put("data", plan);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Semester plan not found");
                    response.put("planId", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createPlan(@RequestBody SemesterPlanRequest request) {
        if (request.getUserId() == null) {
            return badRequest("User is required");
        }
        if (request.getSemesterName() == null || request.getSemesterName().trim().isEmpty()) {
            return badRequest("Semester name is required");
        }
        if (request.getSemesterOrder() == null || request.getSemesterOrder() <= 0) {
            return badRequest("Semester order must be greater than 0");
        }
        if (request.getAcademicYear() == null || request.getAcademicYear() <= 0) {
            return badRequest("Academic year must be greater than 0");
        }

        try {
            SemesterPlan savedPlan = semesterPlanService.createPlan(
                    request.getUserId(),
                    request.getSemesterName().trim(),
                    request.getSemesterOrder(),
                    request.getAcademicYear()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Semester plan created successfully");
            response.put("data", savedPlan);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException ex) {
            return badRequest(ex.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePlan(
            @PathVariable Long id,
            @RequestBody SemesterPlanRequest request
    ) {
        try {
            SemesterPlan updatedPlan = semesterPlanService.updatePlan(
                    id,
                    request.getUserId(),
                    request.getSemesterName(),
                    request.getSemesterOrder(),
                    request.getAcademicYear()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Semester plan updated successfully");
            response.put("data", updatedPlan);

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            if ("Semester plan not found".equals(ex.getMessage())) {
                return notFound("Semester plan not found", id);
            }
            return badRequest(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePlan(@PathVariable Long id) {
        return semesterPlanService.getPlanById(id)
                .map(plan -> {
                    semesterPlanService.deletePlan(id);

                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Semester plan deleted successfully");
                    response.put("deletedPlanId", id);

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> notFound("Semester plan not found", id));
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> notFound(String message, Long id) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("planId", id);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public static class SemesterPlanRequest {
        private Long userId;
        private String semesterName;
        private Integer semesterOrder;
        private Integer academicYear;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getSemesterName() {
            return semesterName;
        }

        public void setSemesterName(String semesterName) {
            this.semesterName = semesterName;
        }

        public Integer getSemesterOrder() {
            return semesterOrder;
        }

        public void setSemesterOrder(Integer semesterOrder) {
            this.semesterOrder = semesterOrder;
        }

        public Integer getAcademicYear() {
            return academicYear;
        }

        public void setAcademicYear(Integer academicYear) {
            this.academicYear = academicYear;
        }
    }
}