package com.travelservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Slf4j
@Configuration
public class AwsS3Config {
	@Value("${spring.cloud.aws.credentials.access-key}")
	private String accessKey;
	@Value("${spring.cloud.aws.credentials.secret-key}")
	private String secretKey;
	@Value("${spring.cloud.aws.region.static}")
	private String region;

	@Bean
	public S3Client s3Client() {
		log.info("✅ AwsS3Config 로드됨");
		log.info("🔑 accessKey: {}", accessKey);
		log.info("🔐 secretKey: {}", secretKey);
		log.info("🌐 region: {}", region);
		return S3Client.builder()
			.region(Region.of(region))
			.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create(accessKey, secretKey)
			))
			.build();
	}
	// public AmazonS3Client amazonS3Client() {
	// 	System.out.println("✅ AwsS3Config 로드됨");
	// 	System.out.printf("🔑 accessKey: {}", accessKey);
	// 	System.out.printf("🔐 secretKey: {}", secretKey);
	// 	System.out.printf("🌐 region: {}", region);
	// 	BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
	// 	return (AmazonS3Client)AmazonS3ClientBuilder.standard()
	// 		.withRegion(region)
	// 		.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	// 		.build();
	// }
}
