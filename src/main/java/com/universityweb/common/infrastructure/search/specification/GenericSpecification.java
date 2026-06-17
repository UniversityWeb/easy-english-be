package com.universityweb.common.infrastructure.search.specification;

import com.universityweb.common.infrastructure.search.dto.FilterRequest;
import com.universityweb.common.infrastructure.search.dto.SearchRequest;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GenericSpecification<T> implements Specification<T> {

    private final SearchRequest request;

    public GenericSpecification(SearchRequest request) {
        this.request = request;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        for (FilterRequest filter : request.getFilters()) {
            if (filter.getField() == null || filter.getOperator() == null) {
                continue;
            }

            Path<?> path = getPath(root, filter.getField());
            Class<?> type = path.getJavaType();
            Object value = castValue(type, filter.getValue());

            Predicate predicate = buildPredicate(path, cb, filter, value);
            if (predicate != null) {
                predicates.add(predicate);
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private Path<?> getPath(Root<T> root, String fieldName) {
        if (!fieldName.contains(".")) {
            return root.get(fieldName);
        }
        String[] parts = fieldName.split("\\.");
        From<?, ?> join = root;
        for (int i = 0; i < parts.length - 1; i++) {
            join = join.join(parts[i]);
        }
        return join.get(parts[parts.length - 1]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Predicate buildPredicate(Path<?> path, CriteriaBuilder cb, FilterRequest filter, Object value) {
        Expression<String> stringPath = (Expression<String>) path;
        Comparable comparableValue = (Comparable) value;
        Expression<Comparable> compPath = (Expression<Comparable>) path;

        switch (filter.getOperator()) {
            case EQ:
                return cb.equal(path, value);
            case NE:
                return cb.notEqual(path, value);
            case GT:
                return cb.greaterThan(compPath, comparableValue);
            case GTE:
                return cb.greaterThanOrEqualTo(compPath, comparableValue);
            case LT:
                return cb.lessThan(compPath, comparableValue);
            case LTE:
                return cb.lessThanOrEqualTo(compPath, comparableValue);
            case CONTAINS:
                if (value == null) return null;
                return cb.like(cb.lower(stringPath), "%" + value.toString().toLowerCase() + "%");
            case STARTS_WITH:
                if (value == null) return null;
                return cb.like(cb.lower(stringPath), value.toString().toLowerCase() + "%");
            case ENDS_WITH:
                if (value == null) return null;
                return cb.like(cb.lower(stringPath), "%" + value.toString().toLowerCase());
            case IN:
                return path.in((Collection<?>) value);
            case BETWEEN:
                List<?> range = (List<?>) filter.getValue();
                Object min = castValue(path.getJavaType(), range.get(0));
                Object max = castValue(path.getJavaType(), range.get(1));
                return cb.between(compPath, (Comparable) min, (Comparable) max);
            case IS_NULL:
                return cb.isNull(path);
            case IS_NOT_NULL:
                return cb.isNotNull(path);
            default:
                return null;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object castValue(Class<?> type, Object value) {
        if (value == null) return null;

        if (value instanceof Collection) {
            Collection<?> rawList = (Collection<?>) value;
            List<Object> castedList = new ArrayList<>();
            for (Object obj : rawList) {
                castedList.add(castValue(type, obj));
            }
            return castedList;
        }

        String strValue = value.toString();
        if (type.equals(Integer.class) || type.equals(int.class)) {
            return Integer.parseInt(strValue);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(strValue);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(strValue);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(strValue);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(strValue);
        } else if (type.equals(LocalDate.class)) {
            return LocalDate.parse(strValue);
        } else if (type.equals(LocalDateTime.class)) {
            return LocalDateTime.parse(strValue);
        } else if (type.isEnum()) {
            return Enum.valueOf((Class<Enum>) type, strValue);
        }
        return value;
    }
}
