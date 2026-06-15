package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs/recommendation")
public class RecommendationLogController {

    @Autowired
    private RecommendationLogService service;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String recommendedFor = body.getOrDefault("recommendedFor", "");
        String payload = body.getOrDefault("payload", "");

        RecommendationLog saved = service.save(userId, recommendedFor, payload);
        Map<String, Object> resp = new HashMap<>(); resp.put("status", "success"); resp.put("data", saved);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable String userId) {
        List<RecommendationLog> logs = service.findByUser(userId);
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        List<RecommendationLog> logs = service.findAll();
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }
}
