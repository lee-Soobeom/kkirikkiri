package com.lsb.kkirikkiri.services;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserMapper userMapper;

    public boolean checkUser(UserEntity sessionUser) {
        return sessionUser != null
                && this.userMapper.selectByEmail(sessionUser.getEmail()) != null;
    }
}
