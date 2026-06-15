package academicPlanner;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * MongoDB document for audit logs.
 */
@Document(collection = "auditLog")
public class AuditLog {

    @Id
    private String id;
    private String userId;
    private String action;
    private String entity;
    private String entityId;
    private String details;
    private Instant timestamp;

    public AuditLog() {}

    public AuditLog(String userId, String action, String entity, String entityId, String details, Instant timestamp) {
        this.userId = userId;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
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
    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
