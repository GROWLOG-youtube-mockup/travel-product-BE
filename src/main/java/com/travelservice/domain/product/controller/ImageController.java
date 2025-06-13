package com.travelservice.domain.product.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.travelservice.domain.product.service.S3Uploader;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "이미지 API", description = "이미지 관련 API입니다")
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
	private final S3Uploader s3Uploader;

	@Operation(summary = "이미지 업로드", description = "이미지 파일을 S3에 업로드하고 URL 리스트 반환")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "업로드 성공"),
	})
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<String>> uploadImages(@RequestParam("files") List<MultipartFile> files) throws
		IOException {
		List<String> urls = new ArrayList<>();
		for (MultipartFile file : files) {
			String url = s3Uploader.upload(file, "product-images");
			urls.add(url);
		}
		return ResponseEntity.ok(urls);
	}

	@DeleteMapping("/{imageId}")
	public ResponseEntity<Void> deleteImages(@RequestBody List<String> imageUrls) {
		for (String url : imageUrls) {
			s3Uploader.delete(url);
		}
		return ResponseEntity.noContent().build();
	}
}
