package com.xf_mingsu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configuration
@ServletComponentScan
@EnableScheduling
@MapperScan("com.xf_mingsu.mapper")
public class XfMingsuApplication {

    public static void main(String[] args) {
        SpringApplication.run(XfMingsuApplication.class, args);
    }

}
