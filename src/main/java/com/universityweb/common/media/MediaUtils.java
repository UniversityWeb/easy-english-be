package com.universityweb.common.media;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.media.service.MediaService;
import com.universityweb.course.response.CourseResponse;
import com.universityweb.lesson.entity.Lesson;
import com.universityweb.lesson.response.LessonResponse;
import com.universityweb.message.Message;
import com.universityweb.message.MessageDTO;
import com.universityweb.notification.response.NotificationResponse;
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

    public static Page<MessageDTO> addMessageMediaUrls(MediaService mediaService, Page<MessageDTO> messageDTOs) {
        return messageDTOs.map(message -> attachMessageMediaUrls(mediaService, message));
    }

    public static List<MessageDTO> addMessageMediaUrls(MediaService mediaService, List<MessageDTO> messageDTOs) {
        return messageDTOs.stream()
                .map(message -> attachMessageMediaUrls(mediaService, message))
                .collect(Collectors.toList());
    }

    public static MessageDTO attachMessageMediaUrls(MediaService mediaService, MessageDTO messageDTO) {
        if (messageDTO == null) return null;
        if (messageDTO.getType() != Message.EType.IMAGE) return messageDTO;
        messageDTO.setContent(mediaService.constructFileUrl(messageDTO.getContent()));
        return messageDTO;
    }

    public static Page<UserDTO> addUserMediaUrlsForPage(MediaService mediaService, Page<UserDTO> userDTOs) {
        return userDTOs.map(user -> attachUserMediaUrls(mediaService, user));
    }

    public static Page<UserForAdminDTO> addUserAdminMediaUrlsForPage(MediaService mediaService, Page<UserForAdminDTO> userDTOs) {
        return userDTOs.map(user -> attachUserAdminMediaUrls(mediaService, user));
    }

    public static List<UserDTO> addUserMediaUrlsForList(MediaService mediaService, List<UserDTO> userDTOs) {
        return userDTOs.stream()
                .map(user -> attachUserMediaUrls(mediaService, user))
                .collect(Collectors.toList());
    }

    public static UserDTO attachUserMediaUrls(MediaService mediaService, UserDTO userDTO) {
        if (userDTO == null) return null;
        userDTO.setAvatarPath(mediaService.constructFileUrl(userDTO.getAvatarPath()));
        return userDTO;
    }

    public static UserForAdminDTO attachUserAdminMediaUrls(MediaService mediaService, UserForAdminDTO userDTO) {
        if (userDTO == null) return null;
        userDTO.setAvatarPath(mediaService.constructFileUrl(userDTO.getAvatarPath()));
        return userDTO;
    }

    private static Page<LessonResponse> constructMediaUrl(MediaService mediaService, Page<LessonResponse> lessonResponses) {
        return lessonResponses.map(l -> attachLessonMediaUrls(mediaService, l));
    }

    private static List<LessonResponse> constructMediaUrl(MediaService mediaService, List<LessonResponse> lessonResponses) {
        return lessonResponses.stream()
                .map(l -> attachLessonMediaUrls(mediaService, l))
                .toList();
    }

    public static LessonResponse attachLessonMediaUrls(MediaService mediaService, LessonResponse lessonResponse) {
        if (lessonResponse == null) return null;
        lessonResponse.setContentUrl(mediaService.constructFileUrl(lessonResponse.getContentUrl()));
        return lessonResponse;
    }

    public static Page<NotificationResponse> attachNotificationMediaUrls(MediaService mediaService, Page<NotificationResponse> notificationResponsePage) {
        return notificationResponsePage.map(n -> attachNotificationMediaUrl(mediaService, n));
    }

    public static NotificationResponse attachNotificationMediaUrl(MediaService mediaService, NotificationResponse notificationResponse) {
        if (notificationResponse == null) return null;
        notificationResponse.setPreviewImage(mediaService.constructFileUrl(notificationResponse.getPreviewImage()));
        return notificationResponse;
    }
}
