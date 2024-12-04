package com.universityweb;

public class FrontendRoutes {
    public static String getOrderDetailRoute(String orderIdStr) {
        return "/order-detail/" + orderIdStr;
    }

    public static String getCourseDetailRoute(String courseId) {
        return "/course-detail/" + courseId;
    }
}
