package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserActivityLogService {

    @Autowired
    private UserActivityLogRepository repository;

    public UserActivityLog save(String userId, String action, String details) {
        UserActivityLog entry = new UserActivityLog(userId, action, details, Instant.now());
        return repository.save(entry);
    }

    public List<UserActivityLog> findByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<UserActivityLog> findAll() {
        return repository.findAll();
    }
}
