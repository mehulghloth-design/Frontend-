package academicPlanner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SemesterPlanRepository extends JpaRepository<SemesterPlan, Long> {

    List<SemesterPlan> findByUserOrderBySemesterOrderAsc(AppUser user);
}