package com.example.testsapi.controller;

import com.example.testsapi.entity.QuestionImage;
import com.example.testsapi.service.QuestionImageService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/question_image")
public class QuestionImageController {

    @Autowired
    private QuestionImageService questionImageService;

    //http://localhost:8080/api/question_image/CQ400124/image
    @PostMapping("/{questionId}/image")
    public ResponseEntity<String> uploadImage(
            @PathVariable String questionId,
            @RequestParam("file") MultipartFile file) {

        questionImageService.uploadQuestionImage(questionId, file);
        return ResponseEntity.ok("Imaginea a fost încărcată cu succes.");
    }

    @GetMapping("/allImages")
    public List<QuestionImage> getAllQuestionImages() {
        return questionImageService.getAllImages();
    }


    @GetMapping("/images/questions/{id}/{filename}")
    public ResponseEntity<Resource> getImage(
            @PathVariable String id,
            @PathVariable String filename
    ) {
        Path path = Paths.get("D:/LICENTA/TestsSecureApiApplication/uploads/images/questions/" + id + "/" + filename);
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }


}
