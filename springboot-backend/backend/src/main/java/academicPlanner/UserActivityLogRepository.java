package academicPlanner;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
    List<UserActivityLog> findByUserId(String userId);
}
