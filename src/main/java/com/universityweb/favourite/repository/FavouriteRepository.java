package com.universityweb.favourite.repository;

import com.universityweb.common.auth.entity.User;
import com.universityweb.course.entity.Course;
import com.universityweb.favourite.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long>{
    List<Favourite> findByUser(User user);

    Favourite findByUserAndCourse(User user, Course course);
}
