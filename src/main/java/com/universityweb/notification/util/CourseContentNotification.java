package com.universityweb.notification.util;

public class CourseContentNotification {
    public static String courseRated(String teacherName, String courseTitle, int rating) {
        return String.format("Hello %s! Your course '%s' has received a new rating of %d stars.",
                teacherName, courseTitle, rating);
    }

    public static String reviewResponded(String studentName, String teacherName, String courseTitle, String response) {
        return String.format("Hello %s! Your review on the course '%s' has received a response from %s: \"%s\".",
                studentName, courseTitle, teacherName, response);
    }

    public static String coursePurchased(String teacherName, String courseTitle) {
        return String.format("Hello %s! A student has purchased your course '%s'.",
                teacherName, courseTitle);
    }

    public static String courseApproved(String teacherName, String courseTitle) {
        return String.format("Congratulations %s! Your course '%s' has been approved and is now published.",
                teacherName, courseTitle);
    }

    public static String courseRejected(String teacherName, String courseTitle, String reason) {
        return String.format("Dear %s, your course '%s' has been rejected. Reason: %s. Please review and resubmit if necessary.",
                teacherName, courseTitle, reason);
    }

    public static String newCoursePendingApproval(String adminName, String courseTitle, String submittedBy) {
        return String.format("Hello %s! A new course '%s' submitted by '%s' is waiting for your approval.",
                adminName, courseTitle, submittedBy);
    }
}
