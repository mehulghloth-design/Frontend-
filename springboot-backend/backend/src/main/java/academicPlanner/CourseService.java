package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByDepartment(String department) {
        return courseRepository.findByDepartmentIgnoreCase(department);
    }

    public List<Course> getCoursesByLevel(String level) {
        return courseRepository.findByLevelIgnoreCase(level);
    }

    public List<Course> searchCourses(String query) {
        return courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                query, query, query
        );
    }
}