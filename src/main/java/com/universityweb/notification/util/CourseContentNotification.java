package com.universityweb.notification.util;

public class CourseContentNotification {
    public static String courseRated(String teacherName, String courseName, int rating) {
        return String.format("Hello %s! Your course '%s' has received a new rating of %d stars.",
                teacherName, courseName, rating);
    }

    public static String coursePurchased(String teacherName, String courseName) {
        return String.format("Hello %s! A student has purchased your course '%s'.",
                teacherName, courseName);
    }

    public static String courseApproved(String teacherName, String courseName) {
        return String.format("Congratulations %s! Your course '%s' has been approved and is now published.",
                teacherName, courseName);
    }

    public static String courseRejected(String teacherName, String courseName, String reason) {
        return String.format("Dear %s, your course '%s' has been rejected. Reason: %s. Please review and resubmit if necessary.",
                teacherName, courseName, reason);
    }

    public static String newCoursePendingApproval(String adminName, String courseName, String submittedBy) {
        return String.format("Hello %s! A new course '%s' submitted by '%s' is waiting for your approval.",
                adminName, courseName, submittedBy);
    }
}
