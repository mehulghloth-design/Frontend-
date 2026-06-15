package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository repository;

    public AuditLog save(String userId, String action, String entity, String entityId, String details) {
        AuditLog entry = new AuditLog(userId, action, entity, entityId, details, Instant.now());
        return repository.save(entry);
    }

    public List<AuditLog> findByUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<AuditLog> findAll() {
        return repository.findAll();
    }
}
