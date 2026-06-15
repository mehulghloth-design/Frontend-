package academicPlanner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;

    @Column(name = "course_code")
    private String code;

    @Column(name = "course_name")
    private String name;

    @Column(name = "credits")
    private int credits;

    @Column(name = "department")
    private String department;

    // Optional columns - will be created if not present (hibernate.ddl-auto=update)
    @Column(name = "level")
    private String level;

    @Column(name = "description")
    private String description;

    public Course() {
    }

    public Course(String code, String name, int credits, String department, String level, String description) {
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.department = department;
        this.level = level;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}