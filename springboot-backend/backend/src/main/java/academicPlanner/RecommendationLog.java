package academicPlanner;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

/**
 * MongoDB document for recommendation logs.
 */
@Document(collection = "recommendationLog")
public class RecommendationLog {

    @Id
    private String id;
    private String userId;
    private String recommendedFor; // e.g., "courses" or other
    private String payload; // JSON or text representation of recommended items
    private Instant timestamp;

    public RecommendationLog() {}

    public RecommendationLog(String userId, String recommendedFor, String payload, Instant timestamp) {
        this.userId = userId;
        this.recommendedFor = recommendedFor;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRecommendedFor() { return recommendedFor; }
    public void setRecommendedFor(String recommendedFor) { this.recommendedFor = recommendedFor; }
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
