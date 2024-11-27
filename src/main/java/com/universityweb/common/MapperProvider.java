package com.universityweb.common;

import com.universityweb.cart.mapper.CartItemMapper;
import com.universityweb.cart.mapper.CartMapper;
import com.universityweb.category.mapper.CategoryMapper;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.course.mapper.CourseMapper;
import com.universityweb.drip.DripMapper;
import com.universityweb.enrollment.entity.Enrollment;
import com.universityweb.enrollment.mapper.EnrollmentMapper;
import com.universityweb.favourite.FavouriteMapper;
import com.universityweb.lesson.mapper.LessonMapper;
import com.universityweb.lessontracker.LessonTrackerMapper;
import com.universityweb.level.mapper.LevelMapper;
import com.universityweb.message.MessageMapper;
import com.universityweb.notification.NotificationMapper;
import com.universityweb.order.mapper.OrderMapper;
import com.universityweb.questiongroup.QuestionGroupMapper;
import com.universityweb.test.TestMapper;
import com.universityweb.testpart.mapper.TestPartMapper;
import com.universityweb.topic.mapper.TopicMapper;
import com.universityweb.useranswer.UserAnswerMapper;
import org.aspectj.weaver.patterns.TypePatternQuestions;
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

    @Bean
    @Primary
    public TestPartMapper testPartMapper() {
        return Mappers.getMapper(TestPartMapper.class);
    }

    @Bean
    @Primary
    public QuestionGroupMapper questionGroupMapper() {
        return Mappers.getMapper(QuestionGroupMapper.class);
    }

    @Bean
    @Primary
    public NotificationMapper notificationMapper() {
        return Mappers.getMapper(NotificationMapper.class);
    }

    @Bean
    @Primary
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    @Primary
    public EnrollmentMapper enrollmentMapper() {
        return Mappers.getMapper(EnrollmentMapper.class);
    }

    @Bean
    @Primary
    public TestMapper testMapper() {
        return Mappers.getMapper(TestMapper.class);
    }

    @Bean
    @Primary
    public MessageMapper messageMapper() {
        return Mappers.getMapper(MessageMapper.class);
    }

    @Bean
    @Primary
    public CourseMapper courseMapper() {
        return Mappers.getMapper(CourseMapper.class);
    }

    @Bean
    @Primary
    public CartMapper cartMapper() {
        return Mappers.getMapper(CartMapper.class);
    }

    @Bean
    @Primary
    public CartItemMapper cartItemMapper() {
        return Mappers.getMapper(CartItemMapper.class);
    }

    @Bean
    @Primary
    public TopicMapper topicMapper() {
        return Mappers.getMapper(TopicMapper.class);
    }

    @Bean
    @Primary
    public LevelMapper levelMapper() {
        return Mappers.getMapper(LevelMapper.class);
    }

    @Bean
    @Primary
    public CategoryMapper categoryMapper() {
        return Mappers.getMapper(CategoryMapper.class);
    }

    @Bean
    @Primary
    public OrderMapper orderMapper() {
        return Mappers.getMapper(OrderMapper.class);
    }
}
