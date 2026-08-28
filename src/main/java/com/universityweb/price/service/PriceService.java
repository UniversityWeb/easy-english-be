package com.universityweb.price.service;

import com.universityweb.common.exception.CustomException;
import com.universityweb.common.exception.ResourceNotFoundException;
import com.universityweb.course.repository.CourseRepository;
import com.universityweb.course.entity.Course;
import com.universityweb.price.PriceRepository;
import com.universityweb.price.entity.Price;
import com.universityweb.price.request.PriceRequest;
import com.universityweb.price.response.PriceResponse;
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
        if (priceRequest.getId() == null) {
            Price price = new Price();
            BeanUtils.copyProperties(priceRequest, price, "id");
            price = priceRepository.save(price);
            
            if (priceRequest.getCourseId() != null) {
                Course course = courseRepository.findById(priceRequest.getCourseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                course.setPriceId(price.getId());
                courseRepository.save(course);
            }
        } else {
            Optional<Price> priceOptional = priceRepository.findById(priceRequest.getId());
            if (priceOptional.isPresent()) {
                Price price = priceOptional.get();
                BeanUtils.copyProperties(priceRequest, price, "courseId");
                priceRepository.save(price);
            } else {
                throw new ResourceNotFoundException("Price not found");
            }
        }
    }

    public PriceResponse getPriceByCourse(PriceRequest priceRequest) {
        courseRepository.findById(priceRequest.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + priceRequest.getCourseId()));

        Price price = priceRepository.findByCourseId(priceRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Price not found for course with ID: " + priceRequest.getCourseId()));
        PriceResponse priceResponse = new PriceResponse();
        BeanUtils.copyProperties(price, priceResponse);
        return priceResponse;
    }

}
