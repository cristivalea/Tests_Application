package com.example.testsapi.service;

import com.example.testsapi.entity.QuestionCodeLink;
import com.example.testsapi.entity.QuestionId;
import com.example.testsapi.entity.QuestionImage;
import com.example.testsapi.repository.QuestionImageRepository;
import com.example.testsapi.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.net.MalformedURLException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class QuestionImageService {

    @Autowired
    private QuestionImageRepository questionImageRepository;
    @Autowired
    private QuestionRepository questionRepository;
    private final String ROOT_PATH = "D:/LICENTA/TestsSecureApiApplication/uploads/";

    public QuestionImageService(QuestionImageRepository questionImageRepository, QuestionRepository questionRepository) {
        this.questionImageRepository = questionImageRepository;
        this.questionRepository = questionRepository;
    }

    private final Path imageStoragePath = Paths.get("src/main/resources/images");

//    @Transactional
//    public void uploadQuestionImage(String idQuestion, MultipartFile file) {
//
//        if (file.isEmpty()) {
//            throw new IllegalArgumentException("Fișierul este gol");
//        }
//
//        // verificare existență DOAR pe string
//        if (!questionRepository.existsByQuestionId_IdQuestion(idQuestion)) {
//            throw new EntityNotFoundException("Întrebarea nu există");
//        }
//
//        try {
//            Files.createDirectories(imageStoragePath);
//
//            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
//            Path targetPath = imageStoragePath.resolve(fileName);
//
//            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
//
//            QuestionImage image = new QuestionImage();
//            image.setIdQuestion(idQuestion);
//            image.setImagePath("images/" + fileName);
//
//            questionImageRepository.save(image);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Eroare la salvarea imaginii", e);
//        }
//    }

    @Transactional
    public void uploadQuestionImage(String idQuestion, MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Fișierul este gol");
        }

        if (!questionRepository.existsByQuestionId_IdQuestion(idQuestion)) {
            throw new EntityNotFoundException("Întrebarea nu există");
        }

        try {
            String original = file.getOriginalFilename();
            String extension = original.substring(original.lastIndexOf('.'));

            String fileName = UUID.randomUUID() + extension;

            Path basePath = Paths.get("uploads/images/questions", idQuestion);
            Files.createDirectories(basePath);

            Path targetPath = basePath.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            QuestionImage image = new QuestionImage();
            image.setIdQuestion(idQuestion);
            image.setImagePath("/images/questions/" + idQuestion + "/" + fileName);

            questionImageRepository.save(image);

        } catch (IOException e) {
            throw new RuntimeException("Eroare la salvarea imaginii", e);
        }
    }

    public List<QuestionImage> getAllImages() {
        return questionImageRepository.findAll();
    }

    public List<String> getImagesForQuestion(String idQuestion) {
        return questionImageRepository.findByIdQuestion(idQuestion)
                .stream()
                .map(img -> "http://localhost:8080" + img.getImagePath())
                .toList();
    }

// În interiorul clasei QuestionImageService:

    public Resource loadImageAsResource(String questionId, String fileName) {
        try {
            // Deoarece în DB ai deja "/images/questions/...", trebuie să extragem DOAR numele fișierului
            // sau să ajustăm cum folosim path-ul.

            // Varianta cea mai sigură: extragem doar numele fișierului din ce vine de la client/Node
            Path pathObj = Paths.get(fileName);
            String pureFileName = pathObj.getFileName().toString();

            Path filePath = Paths.get(ROOT_PATH)
                    .resolve("images")
                    .resolve("questions")
                    .resolve(questionId)
                    .resolve(pureFileName)
                    .normalize();

            System.out.println("DEBUG: Java caută la: " + filePath.toAbsolutePath());

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Fișierul nu există fizic pe disc la: " + filePath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare: " + e.getMessage());
        }
    }
}
