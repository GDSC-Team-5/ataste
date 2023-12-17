package com.ataste.ataste;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class S3Application {
    static { // 이 설정을 하지 않을 경우 서비스가 실행되는 시점에 약간의 지연과 에러 메세지가 뜨게 된다.
        System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");
    }

    public static void main(String[] args) {
        SpringApplication.run(S3Application.class, args);
    }
}
