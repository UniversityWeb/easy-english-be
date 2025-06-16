package com.universityweb.common.util;

public class FrontendRoutes {
    public static String getOrderDetailRoute(String orderIdStr) {
        return "/order-detail/" + orderIdStr;
    }

    public static String getCourseViewDetailWithReviewTabRoute(String courseId) {
        return String.format("/course-view-detail/%s?tab=reviews", courseId);
    }

    public static String getCourseDetailRoute(String courseId) {
        return String.format("/course-detail/%s", courseId);
    }
}
