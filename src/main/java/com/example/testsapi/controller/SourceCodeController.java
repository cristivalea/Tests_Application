package com.example.testsapi.controller;

import com.example.testsapi.entity.SourceCode;
import com.example.testsapi.service.SourceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/source_code")
public class SourceCodeController {
    @Autowired
    private SourceCodeService sourceCodeService;

    @PostMapping
    public ResponseEntity<SourceCode> addSourceCode(@RequestBody SourceCode sourceCode){
        return ResponseEntity.ok(sourceCodeService.addSourceCode(sourceCode));
    }

    @PostMapping("/uploadByPath")
    public ResponseEntity<?> uploadByPath(@RequestParam String filePath,
                                          @RequestParam String programmingLanguage,
                                          @RequestParam String description) {
        try {
            // Citește conținutul fișierului ca String
            Path path = Paths.get(filePath);
            String content = Files.readString(path);

            // Creează obiectul SourceCode
            SourceCode sourceCode = new SourceCode();
            sourceCode.setProgrammingLanguage(programmingLanguage);
            sourceCode.setDescription(description);
            sourceCode.setFileName(path.getFileName().toString());
            sourceCode.setContent(content);

            // Salvează în DB
            SourceCode saved = sourceCodeService.addSourceCode(sourceCode);
            return ResponseEntity.ok(saved);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Error reading file: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSourceCode(@PathVariable Integer id){
        try{
            sourceCodeService.deleteSourceCode(id);
            return ResponseEntity.ok("Source code delete successfully");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<SourceCode> getSourceCode(@PathVariable Integer id){
        return ResponseEntity.ok(sourceCodeService.getSourceCode(id));
    }
}
