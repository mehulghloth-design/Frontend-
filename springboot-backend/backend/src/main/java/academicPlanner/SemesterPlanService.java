package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class SemesterPlanService {

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public List<SemesterPlan> getAllPlans() {
        return semesterPlanRepository.findAll();
    }

    public Optional<SemesterPlan> getPlanById(Long id) {
        return semesterPlanRepository.findById(id);
    }

    public List<SemesterPlan> getPlansByUserId(Long userId) {
        return appUserRepository.findById(userId)
                .map(semesterPlanRepository::findByUserOrderBySemesterOrderAsc)
                .orElse(Collections.emptyList());
    }

    public SemesterPlan createPlan(Long userId, String semesterName, Integer semesterOrder, Integer academicYear) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        SemesterPlan semesterPlan = new SemesterPlan(user, semesterName, semesterOrder, academicYear);
        return semesterPlanRepository.save(semesterPlan);
    }

    public SemesterPlan updatePlan(Long id, Long userId, String semesterName, Integer semesterOrder, Integer academicYear) {
        SemesterPlan existingPlan = semesterPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Semester plan not found"));

        if (userId != null) {
            AppUser user = appUserRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            existingPlan.setUser(user);
        }

        if (semesterName != null && !semesterName.trim().isEmpty()) {
            existingPlan.setSemesterName(semesterName.trim());
        }

        if (semesterOrder != null) {
            existingPlan.setSemesterOrder(semesterOrder);
        }

        if (academicYear != null) {
            existingPlan.setAcademicYear(academicYear);
        }

        return semesterPlanRepository.save(existingPlan);
    }

    public SemesterPlan savePlan(SemesterPlan semesterPlan) {
        return semesterPlanRepository.save(semesterPlan);
    }

    public void deletePlan(Long id) {
        semesterPlanRepository.deleteById(id);
    }
}