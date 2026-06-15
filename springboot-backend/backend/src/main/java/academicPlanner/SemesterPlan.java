package academicPlanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class SemesterPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String semesterName;

    @Column(nullable = false)
    private Integer semesterOrder;

    @Column(nullable = false)
    private Integer academicYear;

    @OneToMany(
            mappedBy = "semesterPlan",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<PlannedCourse> plannedCourses = new ArrayList<>();

    public SemesterPlan() {
    }

    public SemesterPlan(AppUser user, String semesterName, Integer semesterOrder, Integer academicYear) {
        this.user = user;
        this.semesterName = semesterName;
        this.semesterOrder = semesterOrder;
        this.academicYear = academicYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

    public Integer getSemesterOrder() {
        return semesterOrder;
    }

    public void setSemesterOrder(Integer semesterOrder) {
        this.semesterOrder = semesterOrder;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public List<PlannedCourse> getPlannedCourses() {
        return plannedCourses;
    }

    public void setPlannedCourses(List<PlannedCourse> plannedCourses) {
        this.plannedCourses = plannedCourses;
    }

    public void addPlannedCourse(PlannedCourse plannedCourse) {
        plannedCourses.add(plannedCourse);
        plannedCourse.setSemesterPlan(this);
    }

    public void removePlannedCourse(PlannedCourse plannedCourse) {
        plannedCourses.remove(plannedCourse);
        plannedCourse.setSemesterPlan(null);
    }
}