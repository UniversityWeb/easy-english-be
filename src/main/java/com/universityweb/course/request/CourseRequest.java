package com.universityweb.course.request;

import com.universityweb.course.entity.Course;
import com.universityweb.price.response.PriceResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseRequest {
    @Schema(description = "Username of the course owner", example = "john_doe")
    String username;

    @Schema(description = "ID of the favorite course", example = "12345")
    Long favoriteId;

    @Schema(description = "Unique ID of the course", example = "1")
    Long id;

    @Schema(description = "Title of the course", example = "Introduction to Java Programming")
    String title;

    @Schema(description = "ID of the course level", example = "2")
    Long levelId;

    @Schema(description = "ID of the course topic", example = "3")
    Long topicId;

    @Schema(description = "Image preview URL for the course", example = "https://example.com/course-image.jpg")
    String imagePreview;

    @Schema(description = "Video preview URL for the course", example = "https://example.com/course-video.mp4")
    String videoPreview;

    @Schema(description = "Short description preview for the course", example = "Learn the basics of Java programming in this comprehensive course.")
    String descriptionPreview;

    @Schema(description = "Full description of the course", example = "This course covers everything from basic Java syntax to advanced object-oriented programming concepts.")
    String description;

    @Schema(description = "Duration of the course in hours", example = "40")
    int duration;

    @Schema(description = "Number of views the course has", example = "null")
    int countView;

    @Schema(description = "Additional notes for the course", example = "null")
    String notice;

    @Schema(description = "Status of the course", allowableValues = {"PUBLISHED", "REJECTED", "PENDING_APPROVAL", "DRAFT", "DELETED"}, example = "PUBLISHED")
    Course.EStatus status;

    @Schema(description = "Username of the course owner", example = "admin_user")
    String ownerUsername;

    @Schema(description = "Price of the course", example = "199.99")
    BigDecimal price;

    @Schema(description = "Average rating of the course", example = "4.5")
    Double rating;

    @Schema(description = "List of category IDs associated with the course", example = "[1, 2, 3]")
    List<Long> categoryIds;

    @Schema(description = "Page number for pagination", example = "0")
    int pageNumber = 0;

    @Schema(description = "Number of items per page for pagination", example = "8")
    int size = 8;
}
