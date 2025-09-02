package com.example.testsapi.controller;

import com.example.testsapi.entity.Responses;
import com.example.testsapi.service.ResponsesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/responses")
public class ResponsesController {
    @Autowired
    private ResponsesService responseService;

    @PostMapping
    public Responses createReponse(@RequestBody Responses reponse) {
        return responseService.addReponse(reponse.getIdQuestion(), reponse.getIdResponse());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteResponses(@PathVariable Integer id){
        try{
            responseService.deleteResponses(id);
            return ResponseEntity.ok("Responses delete successfully");
        }catch (RuntimeException e){
            return ResponseEntity.ok("Deleted not found " + e.getMessage());
        }
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<Responses> displayResponses(@PathVariable Integer id){
        return ResponseEntity.ok(responseService.getResponses(id));
    }
}
