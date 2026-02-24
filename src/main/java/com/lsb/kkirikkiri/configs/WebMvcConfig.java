package com.lsb.kkirikkiri.configs;

import com.lsb.kkirikkiri.interceptors.WalletInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.walletInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/**");
    }

    @Bean
    public WalletInterceptor walletInterceptor() {
        return new WalletInterceptor();
    }
}
