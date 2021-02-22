package ru.vk.sladkiipirojok.visits.validator;

import org.apache.commons.validator.routines.UrlValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class URIOptionalSchemaValidator implements
        ConstraintValidator<URIOptionalSchema, String> {
    private final UrlValidator urlValidator;

    public URIOptionalSchemaValidator(UrlValidator urlValidator) {
        this.urlValidator = urlValidator;
    }

    @Override
    public boolean isValid(String link, ConstraintValidatorContext context) {
        return urlValidator.isValid(link) || urlValidator.isValid("http://" + link);
    }
}