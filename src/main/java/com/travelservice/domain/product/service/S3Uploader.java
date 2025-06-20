package com.travelservice.domain.product.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {
	private final AmazonS3Client amazonS3Client;

	@Value("${spring.cloud.aws.s3.bucket}")
	private String bucket;

	/**
	 * S3에 이미지 업로드 하기
	 */
	public String upload(MultipartFile file, String dirName) throws IOException {
		String fileName = dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(file.getSize());
		metadata.setContentType(file.getContentType());

		amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file.getInputStream(), metadata)
			.withCannedAcl(CannedAccessControlList.PublicRead));
		return amazonS3Client.getUrl(bucket, fileName).toString(); // 이미지 URL 반환
	}

	public void delete(String fileUrl) {
		String fileKey = extractKeyFromUrl(fileUrl);
		amazonS3Client.deleteObject(bucket, fileKey);
	}

	private String extractKeyFromUrl(String fileUrl) {
		return fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
	}
}
