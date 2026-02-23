package com.lsb.kkirikkiri.mappers;

import com.lsb.kkirikkiri.entities.PaymentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PaymentMapper {
    int insertPayment(@Param(value = "payment") PaymentEntity paymentEntity);

    PaymentEntity selectPaymentByOrderId(@Param(value = "orderId") String orderId);

    int modifyPayment(@Param(value = "payment") PaymentEntity paymentEntity);
}
