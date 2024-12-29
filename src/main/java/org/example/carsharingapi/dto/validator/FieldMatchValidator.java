package org.example.carsharingapi.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Objects;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(FieldMatch constraintAnnotation) {
        this.firstFieldName = constraintAnnotation.first();
        this.secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object field = new BeanWrapperImpl(value).getPropertyValue(firstFieldName);
        Object fieldMatch = new BeanWrapperImpl(value).getPropertyValue(secondFieldName);
        return Objects.equals(field, fieldMatch);
    }
}
