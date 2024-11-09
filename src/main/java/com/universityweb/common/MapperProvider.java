package com.universityweb.common;

import com.universityweb.drip.DripMapper;
import com.universityweb.favourite.FavouriteMapper;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lessontracker.LessonTrackerMapper;
import com.universityweb.useranswer.UserAnswerMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MapperProvider {
    @Bean
    @Primary
    public DripMapper dripMapper() {
        return Mappers.getMapper(DripMapper.class);
    }

    @Bean
    @Primary
    public LessonMapper lessonMapper() {
        return Mappers.getMapper(LessonMapper.class);
    }

    @Bean
    @Primary
    public LessonTrackerMapper lessonTrackerMapper() {
        return Mappers.getMapper(LessonTrackerMapper.class);
    }

    @Bean
    @Primary
    public UserAnswerMapper userAnswerMapper() {
        return Mappers.getMapper(UserAnswerMapper.class);
    }

    @Bean
    @Primary
    public FavouriteMapper favouriteMapper() {
        return Mappers.getMapper(FavouriteMapper.class);
    }
}