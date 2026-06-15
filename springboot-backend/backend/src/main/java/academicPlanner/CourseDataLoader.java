package academicPlanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CourseDataLoader implements CommandLineRunner {

    private final CourseRepository courseRepository;

    public CourseDataLoader(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public void run(String... args) {
        if (courseRepository.count() == 0) {
            courseRepository.save(new Course("CS101", "Programming Fundamentals", 4, "CSE", "Beginner",
                    "Introduction to programming logic, variables, loops, and functions."));
            courseRepository.save(new Course("CS201", "Data Structures", 4, "CSE", "Intermediate",
                    "Covers arrays, stacks, queues, linked lists, trees, and graphs."));
            courseRepository.save(new Course("CS301", "Database Systems", 4, "CSE", "Intermediate",
                    "Relational database concepts, SQL, normalization, and transactions."));
            courseRepository.save(new Course("CS401", "Artificial Intelligence", 3, "CSE", "Advanced",
                    "Search, reasoning, intelligent agents, and basic machine learning concepts."));
            courseRepository.save(new Course("MA201", "Mathematics for Data Science", 3, "MATH", "Intermediate",
                    "Matrices, probability, statistics, and linear algebra for data science."));
        }
    }
}