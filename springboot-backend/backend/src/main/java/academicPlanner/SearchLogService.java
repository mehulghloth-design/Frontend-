package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service to handle search log operations. Uses MongoDB as a secondary datastore.
 */
@Service
public class SearchLogService {

    @Autowired
    private SearchLogRepository searchLogRepository;

    /**
     * Save a new search log entry.
     */
    public SearchLog saveSearchLog(String userId, String query, String ipAddress) {
        SearchLog entry = new SearchLog(userId, query, ipAddress, Instant.now());
        return searchLogRepository.save(entry);
    }

    /**
     * Retrieve logs for a specific user.
     */
    public List<SearchLog> getLogsForUser(String userId) {
        return searchLogRepository.findByUserId(userId);
    }

    /**
     * Retrieve all logs (use with caution in production).
     */
    public List<SearchLog> getAllLogs() {
        return searchLogRepository.findAll();
    }
}
