package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.ServiceEntity;
import com.lsb.kkirikkiri.enums.ServiceFilter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceValidator {
    private static final String QUESTION_REGEX = "^[\\s\\S]{1,50}$";
    private static final String ANSWER_REGEX = "^[\\s\\S]{1,1000}$";

    public boolean validateQuestion(String question) {
        return question != null
                && question.matches(QUESTION_REGEX);
    }

    public boolean validateQuestion(@NonNull ServiceEntity serviceEntity) {
        return validateQuestion(serviceEntity.getQuestion());
    }

    public boolean validateAnswer(String answer) {
        return answer != null
                && answer.matches(ANSWER_REGEX);
    }

    public boolean validateAnswer(@NonNull ServiceEntity serviceEntity) {
        return validateAnswer(serviceEntity.getAnswer());
    }

    public boolean validateFilter(String filter) {
        if (filter == null) {
            return false;
        }
        boolean result = false;
        for (ServiceFilter f : ServiceFilter.values()) {
            if (filter.equals(f.code)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean validateFilter(@NonNull ServiceEntity serviceEntity) {
        return validateFilter(serviceEntity.getFilter());
    }
}
