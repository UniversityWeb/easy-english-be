package com.universityweb.course.price.service;

import com.universityweb.course.common.entity.Course;
import com.universityweb.course.price.PriceRepository;
import com.universityweb.course.price.entity.Price;
import com.universityweb.course.price.request.PriceRequest;
import com.universityweb.course.price.response.PriceResponse;
import com.universityweb.course.common.repository.CourseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceService {
    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private CourseRepository courseRepository;

    public void updatePrice(PriceRequest priceRequest) {
        Optional<Price> priceOptional = priceRepository.findById(priceRequest.getId());
        if (priceOptional.isPresent()) {
            Price price = priceOptional.get();
            BeanUtils.copyProperties(priceRequest, price, "courseId");
            priceRepository.save(price);
        } else {
            throw new RuntimeException("Price not found");
        }
    }

    public PriceResponse getPriceByCourse(PriceRequest priceRequest) {
        Course course = courseRepository.findById(priceRequest.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + priceRequest.getCourseId()));

        Price price = priceRepository.findByCourse(course)
                .orElseThrow(() -> new RuntimeException("Price not found for course with ID: " + priceRequest.getCourseId()));
        PriceResponse priceResponse = new PriceResponse();
        BeanUtils.copyProperties(price, priceResponse);
        return priceResponse;
    }

}
