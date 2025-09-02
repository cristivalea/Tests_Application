package com.example.testsapi.controller;

import com.example.testsapi.entity.Response;
import com.example.testsapi.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/response")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping("/add")
    public ResponseEntity<Response> addResponse(@RequestBody Response response) {
        Response savedResponse = responseService.addResponse(response);
        return ResponseEntity.ok(savedResponse);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteResponse(@PathVariable Integer id){
        try{
            responseService.deleteResponse(id);
            return ResponseEntity.ok("Response successfully deleted!");
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/display/{id}")
    public ResponseEntity<Response> displayResponse(@PathVariable Integer id){
        Response responses = responseService.displayResponse(id);
        return ResponseEntity.ok(responses);
    }
}
