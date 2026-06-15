package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class RecommendationLogService {

    @Autowired
    private RecommendationLogRepository repository;

    public RecommendationLog save(String userId, String recommendedFor, String payload) {
        RecommendationLog entry = new RecommendationLog(userId, recommendedFor, payload, Instant.now());
        return repository.save(entry);
    }

    public List<RecommendationLog> findByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<RecommendationLog> findAll() {
        return repository.findAll();
    }
}
