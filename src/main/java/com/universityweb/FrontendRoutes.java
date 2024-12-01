package com.universityweb;

public class FrontendRoutes {
    public static String getOrderDetailRoute(String orderIdStr) {
        return "/order-detail/" + orderIdStr;
    }
}
