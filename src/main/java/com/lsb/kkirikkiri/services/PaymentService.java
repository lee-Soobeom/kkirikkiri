package com.lsb.kkirikkiri.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lsb.kkirikkiri.entities.PaymentEntity;
import com.lsb.kkirikkiri.entities.UserWalletEntity;
import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.exceptions.TransactionalException;
import com.lsb.kkirikkiri.mappers.PaymentMapper;
import com.lsb.kkirikkiri.mappers.UserMapper;
import com.lsb.kkirikkiri.mappers.WalletMapper;
import com.lsb.kkirikkiri.results.CommonResult;
import com.lsb.kkirikkiri.results.PaymentResult;
import com.lsb.kkirikkiri.results.Result;
import com.lsb.kkirikkiri.validators.PaymentValidator;
import com.lsb.kkirikkiri.validators.UserValidator;
import com.lsb.kkirikkiri.validators.ValidatorUtils;
import com.lsb.kkirikkiri.vos.PaymentVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserMapper userMapper;
    private final PaymentMapper paymentMapper;
    private final WalletMapper walletMapper;

    private final String SECRET_KEY = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R";

    public PaymentVo cancelPayment(String paymentKey, String cancelReason, int cancelAmount) {
        try {
            // Base64 인코딩
            String encodedAuth = Base64.getEncoder()
                    .encodeToString((SECRET_KEY + ":").getBytes());

            URL url = new URL("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // 요청 본문
            JSONObject requestBody = new JSONObject();
            requestBody.put("cancelReason", cancelReason);
            requestBody.put("cancelAmount", cancelAmount);

            OutputStream os = connection.getOutputStream();
            os.write(requestBody.toString().getBytes());
            os.flush();

            // 응답 처리
            int responseCode = connection.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            return new ObjectMapper().readValue(response.toString(), PaymentVo.class);

        } catch (Exception e) {
            throw new RuntimeException("결제 취소 실패", e);
        }
    }

    public boolean checkUser(UserEntity sessionUser) {
        return sessionUser != null
                && this.userMapper.selectByEmail(sessionUser.getEmail()) != null;
    }

    public Pair<CommonResult, String> createPayment(UserEntity sessionUser, PaymentEntity paymentEntity) {
        if (sessionUser == null ||
                this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        if (paymentEntity == null ||
                !ValidatorUtils.validateUUID(paymentEntity.getCustomerKey()) ||
                !PaymentValidator.validateAmount(paymentEntity) ||
                !UserValidator.validateEmail(paymentEntity.getUserEmail()) ||
                !UserValidator.validateName(paymentEntity.getUserName())) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        paymentEntity.setOrderId(UUID.randomUUID().toString());
        paymentEntity.setStatus("READY");
        paymentEntity.setPaymentKey(null);
        return this.paymentMapper.insertPayment(paymentEntity) > 0
                ? Pair.of(CommonResult.SUCCESS, paymentEntity.getOrderId())
                : Pair.of(CommonResult.FAILURE, null);
    }

    public PaymentEntity getPayment(String orderId) {
        if (!ValidatorUtils.validateUUID(orderId)) {
            return null;
        }
        return this.paymentMapper.selectPaymentByOrderId(orderId);
    }

    @Transactional
    public Pair<Result, PaymentVo> updateCharge(UserEntity sessionUser, PaymentEntity paymentEntity) {
        if (sessionUser == null ||
                this.userMapper.selectByEmail(sessionUser.getEmail()) == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        if (paymentEntity == null ||
                paymentEntity.getPaymentKey() == null ||
                !PaymentValidator.validateOrderId(paymentEntity) ||
                !PaymentValidator.validateAmount(paymentEntity)) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        // orderId, amount, paymentKey in paymentEntity
        PaymentEntity dbPaymentEntity = this.paymentMapper.selectPaymentByOrderId(paymentEntity.getOrderId());
        dbPaymentEntity.setPaymentKey(paymentEntity.getPaymentKey());
        dbPaymentEntity.setStatus("DONE");

        // update wallet (by customerKey)
        UserWalletEntity dbWalletEntity = this.walletMapper.selectUserWalletByCustomerKey(dbPaymentEntity.getCustomerKey());
        if (dbWalletEntity == null) {
            return Pair.of(CommonResult.FAILURE, null);
        }
        dbWalletEntity.setCash(dbWalletEntity.getCash() + dbPaymentEntity.getAmount());
        dbWalletEntity.setLastCharge(LocalDateTime.now());
        dbWalletEntity.setLastPay(null);
        if (this.walletMapper.modifyUserWallet(dbWalletEntity) < 0) {
            // TossPayments 결제 성공 & wallet table에 update 실패 > TossPayments 환불
            PaymentVo paymentVo = cancelPayment(paymentEntity.getPaymentKey(), "서비스 오류로 인한 환불", paymentEntity.getAmount());
            return Pair.of(PaymentResult.FAILURE_CANCEL, paymentVo);
        }
        if (this.paymentMapper.modifyPayment(dbPaymentEntity) < 0) {
            // TossPayments 결제 성공 & wallet table update 성공 & payment table update 실패 > 환불 & transaction
            PaymentVo paymentVo = cancelPayment(paymentEntity.getPaymentKey(), "서비스 오류로 인한 환불", paymentEntity.getAmount());
            throw new TransactionalException(PaymentResult.FAILURE_CANCEL);
        }
        return Pair.of(CommonResult.SUCCESS, null);
    }
}
