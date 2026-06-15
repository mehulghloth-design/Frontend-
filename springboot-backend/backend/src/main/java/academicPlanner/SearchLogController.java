package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller exposing endpoints to write and query search logs in MongoDB.
 * These endpoints are additive and do not interfere with existing PostgreSQL entities.
 */
@RestController
@RequestMapping("/api/logs/search")
public class SearchLogController {

    @Autowired
    private SearchLogService searchLogService;

    @PostMapping
    public ResponseEntity<?> createSearchLog(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String query = body.get("query");
        String ip = body.getOrDefault("ipAddress", "");

        if (query == null || query.trim().isEmpty()) {
            Map<String, Object> resp = new HashMap<>();
            resp.put("status", "error");
            resp.put("message", "query is required");
            return ResponseEntity.badRequest().body(resp);
        }

        SearchLog saved = searchLogService.saveSearchLog(userId, query.trim(), ip);

        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("data", saved);
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getLogsForUser(@PathVariable String userId) {
        List<SearchLog> logs = searchLogService.getLogsForUser(userId);
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("count", logs.size());
        resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllLogs() {
        List<SearchLog> logs = searchLogService.getAllLogs();
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "success");
        resp.put("count", logs.size());
        resp.put("data", logs);
        return ResponseEntity.ok(resp);
    }
}
