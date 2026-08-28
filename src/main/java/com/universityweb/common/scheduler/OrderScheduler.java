package com.universityweb.common.scheduler;

import com.universityweb.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderService orderService;

    /**
     * Checks for pending orders that have exceeded the 5-minute timeout window
     * and marks them as EXPIRED.
     * The fixedRate is configured in application.yml.
     */
    @Scheduled(fixedRateString = "${app.scheduler.order-expiration-rate:3600000}")
    public void runExpirationCheckJob() {
        orderService.updateExpiredOrders();
    }
}
