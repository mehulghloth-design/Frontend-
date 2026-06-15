package academicPlanner;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * MongoDB document for user activity logs.
 */
@Document(collection = "userActivityLog")
public class UserActivityLog {

    @Id
    private String id;
    private String userId;
    private String action;
    private String details;
    private Instant timestamp;

    public UserActivityLog() {}

    public UserActivityLog(String userId, String action, String details, Instant timestamp) {
        this.userId = userId;
        this.action = action;
        this.details = details;
        this.timestamp = timestamp;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
