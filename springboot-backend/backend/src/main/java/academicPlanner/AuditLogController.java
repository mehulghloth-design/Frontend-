package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs/audit")
public class AuditLogController {

    @Autowired
    private AuditLogService service;

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String action = body.get("action");
        String entity = body.get("entity");
        String entityId = body.getOrDefault("entityId", "");
        String details = body.getOrDefault("details", "");

        if (action == null || action.trim().isEmpty() || entity == null || entity.trim().isEmpty()) {
            Map<String, Object> r = new HashMap<>(); r.put("status", "error"); r.put("message", "action and entity are required");
            return ResponseEntity.badRequest().body(r);
        }

        AuditLog saved = service.save(userId, action, entity, entityId, details);
        Map<String, Object> resp = new HashMap<>(); resp.put("status", "success"); resp.put("data", saved);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> byUser(@PathVariable String userId) {
        List<AuditLog> logs = service.findByUser(userId);
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> all() {
        List<AuditLog> logs = service.findAll();
        Map<String, Object> resp = new HashMap<>(); resp.put("status","success"); resp.put("count", logs.size()); resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }
}
