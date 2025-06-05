package com.universityweb.statistics.service;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.service.user.UserService;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.entity.Course;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.course.service.CourseService;
import com.universityweb.statistics.CourseStatisticsRepos;
import com.universityweb.statistics.request.CourseFilterReq;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseStatisticsServiceImpl implements CourseStatisticsService {
    private final CourseStatisticsRepos courseStatisticsRepos;
    private final MediaService mediaService;
    private final CourseService courseService;
    private final UserService userService;

    @Override
    public List<Map<String, Object>> getRevenueByYear(int year) {
        return courseStatisticsRepos.findRevenueByYear(year);
    }

    @Override
    public Page<CourseResponse> getTopCoursesByRevenue(CourseFilterReq req) {
        String ownerUsername = req.getOwnerUsername();
        Integer month = req.getMonth();
        Integer year = req.getYear();
        Integer page = req.getPage();
        Integer size = req.getSize();
        PageRequest pageRequest = PageRequest.of(page, size);

        // Fetch the data from the repository
        Page<Object[]> results = courseStatisticsRepos.findTopCoursesByRevenue(ownerUsername, month, year, pageRequest);

        // Transform the result into the required format
        List<CourseResponse> courseResponses = new ArrayList<>();
        for (Object[] result : results) {
            Long id = (Long) result[0];
            BigDecimal revenue = (BigDecimal) result[3];

            Course course = courseService.getEntityById(id);
            CourseResponse courseResponse = courseService.mapCourseToResponse(course);
            courseResponse.setTotalRevenue(revenue);
            courseResponses.add(courseResponse);
        }

        return new PageImpl<>(courseResponses, PageRequest.of(page, size), results.getTotalElements());
    }

    @Override
    public List<Map<String, Object>> getCoursesForSuggestions() {
        List<CourseResponse> courses = courseService.getAll();

        List<String> difficulties = Arrays.asList("Beginner", "Elementary", "Intermediate", "Upper-Intermediate", "Advanced");

        Random random = new Random();

        List<Map<String, Object>> courseList = new ArrayList<>();
        for (CourseResponse c : courses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("course_id", c.getId());
            courseData.put("course_title", c.getTitle());

            courseData.put("category", c.getCategories());

            String difficulty = difficulties.get(random.nextInt(difficulties.size()));
            courseData.put("difficulty", difficulty);

            int duration = 5 + random.nextInt(96); // 5 to 100 hours
            courseData.put("duration_hours", duration);

            double rating = Math.round((1.0 + random.nextDouble() * 4.0) * 10.0) / 10.0; // 1.0 to 5.0, rounded to 1 decimal
            courseData.put("rating", rating);

            int numLessons = 5 + random.nextInt(26); // 5 to 30 lessons
            courseData.put("num_lessons", numLessons);

            int level =  random.nextInt(5); // 0 to 4
            courseData.put("prerequisite_level", level);

            courseData.put("topic", c.getTopic().getName());
            courseData.put("level", c.getLevel().getName());

            courseList.add(courseData);
        }

        return courseList;
    }

    @Override
    public List<Map<String, Object>> getUsersForSuggestions() {
        List<UserDTO> users = userService.getAll();

        List<String> goals = Arrays.asList("Business", "Academic", "Travel", "General", "Test_Prep");
        List<String> skills = Arrays.asList("Grammar", "Vocabulary", "Speaking", "Listening", "Reading", "Writing", "TOEIC", "IELTS");
        List<String> countries = Arrays.asList("Vietnam", "USA", "UK", "India", "Australia", "Canada");

        Random random = new Random();

        List<Map<String, Object>> courseList = new ArrayList<>();
        for (UserDTO user : users) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("username", user.getUsername());

            int age = 16 + random.nextInt(30); // Random age from 16 to 45
            courseData.put("age", age);

            int level = random.nextInt(5); // 0 to 4
            courseData.put("current_level", level);

            String goal = goals.get(random.nextInt(goals.size()));
            courseData.put("learning_goal", goal);

            int studyTime = 1 + random.nextInt(15); // 1 to 15 hours per week
            courseData.put("study_time_per_week", studyTime);

            String skill = skills.get(random.nextInt(skills.size()));
            courseData.put("preferred_skill", skill);

            String country = countries.get(random.nextInt(countries.size()));
            courseData.put("country", country);

            courseList.add(courseData);
        }

        return courseList;
    }

    @Override
    public List<Map<String, Object>> getInteractionsForSuggestions() {
        List<UserDTO> users = userService.getAll();
        Random random = new Random();

        List<Map<String, Object>> interactionList = new ArrayList<>();
        for (UserDTO user : users) {
            Map<String, Object> interactionData = new HashMap<>();
            interactionData.put("username", user.getUsername());

            // Generate a fake course_id (e.g., course_1 to course_10)
            String courseId = "course_" + (1 + random.nextInt(10));
            interactionData.put("course_id", courseId);

            int testScore = random.nextInt(101); // 0 to 100
            interactionData.put("test_score", testScore);

            double completionRate = Math.round(random.nextDouble() * 100.0) / 100.0; // Rounded to 2 decimal places
            interactionData.put("completion_rate", completionRate);

            int satisfaction = 1 + random.nextInt(5); // 1 to 5
            interactionData.put("satisfaction_rating", satisfaction);

            int timeSpent = 1 + random.nextInt(50); // 1 to 50 hours
            interactionData.put("time_spent_hours", timeSpent);

            int attempts = 1 + random.nextInt(10); // 1 to 10 attempts
            interactionData.put("attempts", attempts);

            int passed = testScore >= 50 ? 1 : 0;
            interactionData.put("passed", passed);

            interactionList.add(interactionData);
        }

        return interactionList;
    }

    @Override
    public Page<Map<String, Object>> getTopCoursesByRevenue(String ownerUsername, int month, int year, int page, int size) {
        // Fetch the data from the repository
        Page<Object[]> results = courseStatisticsRepos.findTopCoursesByRevenue(ownerUsername, month, year, PageRequest.of(page, size));

        // Transform the result into the required format
        List<Map<String, Object>> courseList = new ArrayList<>();
        for (Object[] result : results) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", result[0]);
            courseData.put("imagePreview", mediaService.constructFileUrl((String) result[1]));
            courseData.put("title", result[2]);
            courseData.put("totalRevenue", result[3]);
            courseData.put("ownerUsername", result[4]);
            courseList.add(courseData);
        }

        return new PageImpl<>(courseList, PageRequest.of(page, size), results.getTotalElements());
    }

}
