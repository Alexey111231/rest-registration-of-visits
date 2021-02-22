package ru.vk.sladkiipirojok.visits.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;

@Documented
@Constraint(validatedBy = URIOptionalSchemaValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface URIOptionalSchema {
    String message() default "Invalid link";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
