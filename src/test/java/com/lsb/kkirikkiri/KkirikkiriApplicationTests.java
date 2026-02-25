package com.lsb.kkirikkiri;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class KkirikkiriApplicationTests {

    @Test // 해당 메서드를 테스트 가능한 메서드로 만든다
    void contextLoads() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String raw = "test1234";
        String hashed = encoder.encode(raw);
        System.out.println("Raw: " + raw);
        System.out.println("Hashed: " + hashed);

        System.out.println(BCrypt.checkpw("Hello World", "$2a$10$2eR95aYLFdiaEMLU5L46b.1ZZLVh8uObR3VuOwfbvXoh9GeVAiiMa"));
    }

}
