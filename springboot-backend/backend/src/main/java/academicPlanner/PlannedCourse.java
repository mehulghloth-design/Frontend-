package academicPlanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "planned_courses")
public class PlannedCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plannedCourseId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private SemesterPlan semesterPlan;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public PlannedCourse() {
    }

    public PlannedCourse(SemesterPlan semesterPlan, Course course) {
        this.semesterPlan = semesterPlan;
        this.course = course;
    }

    public Long getPlannedCourseId() {
        return plannedCourseId;
    }

    public void setPlannedCourseId(Long plannedCourseId) {
        this.plannedCourseId = plannedCourseId;
    }

    public SemesterPlan getSemesterPlan() {
        return semesterPlan;
    }

    public void setSemesterPlan(SemesterPlan semesterPlan) {
        this.semesterPlan = semesterPlan;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}