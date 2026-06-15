package academicPlanner;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for SearchLog documents stored in MongoDB.
 */
@Repository
public interface SearchLogRepository extends MongoRepository<SearchLog, String> {
    List<SearchLog> findByUserId(String userId);
}
