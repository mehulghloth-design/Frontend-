package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs/activity")
public class UserActivityLogController {

    @Autowired
    private UserActivityLogService service;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String action = body.get("action");
        String details = body.getOrDefault("details", "");

        if (action == null || action.trim().isEmpty()) {
            Map<String, Object> r = new HashMap<>(); r.put("status", "error"); r.put("message", "action is required");
            return ResponseEntity.badRequest().body(r);
        }

        UserActivityLog saved = service.save(userId, action, details);
        Map<String, Object> resp = new HashMap<>(); resp.put("status", "success"); resp.put("data", saved);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable String userId) {
        List<UserActivityLog> logs = service.findByUser(userId);
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        List<UserActivityLog> logs = service.findAll();
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }
}
