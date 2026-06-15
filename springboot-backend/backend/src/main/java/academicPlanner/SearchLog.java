package academicPlanner;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * MongoDB document representing a search log entry.
 * Stored in the 'searchLog' collection in the MongoDB database configured by spring.data.mongodb.uri
 */
@Document(collection = "searchLog")
public class SearchLog {

    @Id
    private String id;

    private String userId; // optional user id (if logged in)
    private String query;
    private String ipAddress;
    private Instant timestamp;

    public SearchLog() {}

    public SearchLog(String userId, String query, String ipAddress, Instant timestamp) {
        this.userId = userId;
        this.query = query;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
