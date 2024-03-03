package com.sjna.teamup.config;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsConfig {

    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
        /**
         * 다음 순서대로 Credentials 로드
         * 1. 환경변수 (AWS_ACCESS_KEY_ID or AWS_ACCESS_KEY, AWS_SECRET_KEY or AWS_SECRET_ACCESS_KEY)
         * 2. 자바 시스템 프로퍼티 (java -jar application.jar -Daws.accessKeyId=... -Daws.secretKey=... -Daws.sessionToken=...)
         * 3. WebIdentityToken 사용
         * 4. aws credentials 파일 사용 (~/.aws/credentials)
         * 5. Amazon ECS 컨테이너 크리덴셜 (환경변수 AWS_CONTAINER_CREDENTIALS_RELATIVE_URI or AWS_CONTAINER_CREDENTIALS_FULL_URI)
         * 6. 인스턴스 프로파일 자격 증명 (EC2 인스턴스에 열결된 credentials 사용)
         */
        return AmazonSimpleEmailServiceClient.builder()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();
    }

}
