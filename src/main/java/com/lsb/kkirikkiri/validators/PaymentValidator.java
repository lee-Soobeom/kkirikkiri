package com.lsb.kkirikkiri.validators;

import com.lsb.kkirikkiri.entities.PaymentEntity;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentValidator {
    public boolean validateOrderId(@NonNull PaymentEntity paymentEntity) {
        return ValidatorUtils.validateUUID(paymentEntity.getOrderId());
    }

    public boolean validateAmount(@NonNull PaymentEntity paymentEntity) {
        return validateAmount(paymentEntity.getAmount());
    }

    public boolean validateAmount(int amount) {
        return amount > 0;
    }
}
