package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlannedCourseService {

    @Autowired
    private PlannedCourseRepository plannedCourseRepository;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private CourseRepository courseRepository;

    public List<PlannedCourse> getCoursesByPlanId(Long planId) {
        return plannedCourseRepository.findBySemesterPlan_IdOrderByPlannedCourseIdAsc(planId);
    }

    public PlannedCourse savePlannedCourse(Long planId, Long courseId) {
        SemesterPlan semesterPlan = semesterPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Semester plan not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        PlannedCourse plannedCourse = new PlannedCourse(semesterPlan, course);
        return plannedCourseRepository.save(plannedCourse);
    }

    public void deletePlannedCourse(Long id) {
        plannedCourseRepository.deleteById(id);
    }
}