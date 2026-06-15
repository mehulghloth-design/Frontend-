package academicPlanner;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RecommendationLogRepository extends MongoRepository<RecommendationLog, String> {
    List<RecommendationLog> findByUserId(String userId);
}
