package com.universityweb.common;

import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.course.model.Course;
import com.universityweb.course.model.Lesson;
import com.universityweb.course.model.Section;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.repository.LessonRepository;
import com.universityweb.course.repository.SectionRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@Transactional
public class InitData {
    private final Logger log = LogManager.getLogger(this.getClass().getName());

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Bean
    public void init() {
        List<User> users = initUsers();
        //initCourses();
    }

    private List<User> initUsers() {
        String commonPass = passwordEncoder.encode("P@123456");

        User student1 = User.builder()
                .username("vanan")
                .password(commonPass)
                .fullName("Van An")
                .email("vanan@gmail.com")
                .phoneNumber("+840971640799")
                .bio("Studying at HCMUTE")
                .gender(User.EGender.MALE)
                .dob(LocalDate.of(2003, 12, 3))
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .status(User.EStatus.ACTIVE)
                .build();

        User student2 = User.builder()
                .username("hungsam")
                .password(commonPass)
                .fullName("Hung")
                .email("hung@gmail.com")
                .phoneNumber("+840971640788")
                .bio("Studying at HCMUTE")
                .gender(User.EGender.MALE)
                .dob(LocalDate.of(2003, 3, 3))
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .status(User.EStatus.ACTIVE)
                .build();

        User teacher = User.builder()
                .username("long")
                .password(commonPass)
                .fullName("Hoang Long")
                .email("long@gmail.com")
                .phoneNumber("+840971640710")
                .bio("Teaching at HCMUTE")
                .gender(User.EGender.MALE)
                .dob(LocalDate.of(2003, 10, 3))
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .status(User.EStatus.ACTIVE)
                .build();

        User admin = User.builder()
                .username("admin")
                .password(commonPass)
                .fullName("admin")
                .email("")
                .phoneNumber("")
                .bio("Admin")
                .gender(User.EGender.MALE)
                .dob(LocalDate.of(2024, 7, 3))
                .role(User.ERole.STUDENT)
                .createdAt(LocalDateTime.now())
                .status(User.EStatus.ACTIVE)
                .build();

        List<User> users = List.of(student1, student2, teacher, admin);
        try {
            return userService.saveAll(users);
        } catch (HibernateException e) {
            log.error("Error occurred while saving users", e);
            return users;
        }
    }

//    private void initCourses() {
//        String commonUrlImg = "https://img-c.udemycdn.com/course/240x135/2776760_f176_10.jpg";
//
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        Course course1 = Course.builder()
//                .title("Java Basics")
//                .category("Programming")
//                .level("Beginner")
//                .imageUrl(commonUrlImg)
//                .duration(30)
//                .price(200)
//                .description("Introduction to Java programming.")
//                .isPublish(true)
//                .createdBy("admin")
//                .createdAt(now.format(formatter))
//                .build();
//
//        Course course2 = Course.builder()
//                .title("Advanced Python")
//                .category("Programming")
//                .level("Advanced")
//                .imageUrl(commonUrlImg)
//                .duration(40)
//                .price(300)
//                .description("Deep dive into advanced Python topics.")
//                .isPublish(true)
//                .createdBy("admin")
//                .createdAt(now.format(formatter))
//                .build();
//
//        Section section1 = Section.builder()
//                .title("Introduction to Java")
//                .createdBy("admin")
//                .createdAt(now.format(formatter))
//                .course(course1)
//                .build();
//
//        Section section2 = Section.builder()
//                .title("Advanced Concepts in Java")
//                .createdBy("admin")
//                .createdAt(now.format(formatter))
//                .course(course1)
//                .build();
//
//        Lesson lesson1 = Lesson.builder()
//                .title("Getting Started with Java")
//                .type("Video")
//                .content("Basic introduction to Java programming.")
//                .contentUrl("http://example.com/java-intro.mp4")
//                .description("First lesson of the Java Basics course.")
//                .duration(60)
//                .isPreview(true)
//                .startDate(LocalDateTime.now())
//                .createdBy("admin")
//                .createdAt(LocalDateTime.now())
//                .section(section1)
//                .build();
//
//        Lesson lesson2 = Lesson.builder()
//                .title("Java OOP Concepts")
//                .type("Video")
//                .content("Understanding Object-Oriented Programming in Java.")
//                .contentUrl("http://example.com/java-oop.mp4")
//                .description("Lesson on OOP concepts in Java.")
//                .duration(90)
//                .isPreview(false)
//                .startDate(LocalDateTime.now())
//                .createdBy("admin")
//                .createdAt(LocalDateTime.now())
//                .section(section2)
//                .build();
//
//        try {
//            courseRepository.saveAll(Arrays.asList(course1, course2));
//            sectionRepository.saveAll(Arrays.asList(section1, section2));
//            lessonRepository.saveAll(Arrays.asList(lesson1, lesson2));
//        } catch (HibernateException e) {
//            log.error("Error occurred while saving courses, sections, and lessons", e);
//        }
//    }
}
