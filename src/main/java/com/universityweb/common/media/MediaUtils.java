package com.universityweb.common.media;

import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.order.dto.OrderDTO;
import com.universityweb.order.dto.OrderItemDTO;
import com.universityweb.test.dto.TestDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class MediaUtils {
    public static OrderDTO addOrderMediaUrls(MediaService mediaService, OrderDTO order) {
        if (order == null) return null;
        List<OrderItemDTO> items = order.getItems();

        if (items != null) {
            items.forEach(item -> {
                CourseResponse course = item.getCourse();
                if (course != null) {
                    item.setCourse(attachCourseMediaUrls(mediaService, course));
                }
            });
        }
        return order;
    }

    public static Page<OrderDTO> addMediaUrlsToOrders(MediaService mediaService, Page<OrderDTO> orders) {
        orders.forEach(order -> addMediaUrlsToOrderItems(mediaService, order.getItems()));
        return orders;
    }

    public static List<OrderItemDTO> addMediaUrlsToOrderItems(MediaService mediaService, List<OrderItemDTO> items) {
        items.forEach(item -> item.setCourse(attachCourseMediaUrls(mediaService, item.getCourse())));
        return items;
    }

    public static Page<OrderItemDTO> addMediaUrlsToOrderItems(MediaService mediaService, Page<OrderItemDTO> items) {
        items.forEach(item -> item.setCourse(attachCourseMediaUrls(mediaService, item.getCourse())));
        return items;
    }

    public static Page<CourseResponse> addCourseMediaUrls(MediaService mediaService, Page<CourseResponse> courseResponses) {
        return courseResponses.map(course -> attachCourseMediaUrls(mediaService, course));
    }

    public static List<CourseResponse> addCourseMediaUrls(MediaService mediaService, List<CourseResponse> courseResponses) {
        return courseResponses.stream()
                .map(course -> attachCourseMediaUrls(mediaService, course))
                .collect(Collectors.toList());
    }

    public static CourseResponse addCourseMediaUrls(MediaService mediaService, CourseResponse courseResponse) {
        return attachCourseMediaUrls(mediaService, courseResponse);
    }

    public static CourseResponse attachCourseMediaUrls(MediaService mediaService, CourseResponse response) {
        if (response == null) return null;
        response.setVideoPreview(mediaService.constructFileUrl(response.getVideoPreview()));
        response.setImagePreview(mediaService.constructFileUrl(response.getImagePreview()));
        return response;
    }

    public static TestDTO attachTestMediaUrls(MediaService mediaService, TestDTO dto) {
        dto.setAudioPath(mediaService.constructFileUrl(dto.getAudioPath()));
        return dto;
    }
}
