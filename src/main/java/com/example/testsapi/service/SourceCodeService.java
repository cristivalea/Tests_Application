package com.example.testsapi.service;

import com.example.testsapi.entity.SourceCode;
import com.example.testsapi.repository.SourceCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class SourceCodeService {

    @Autowired
    private SourceCodeRepository sourceCodeRepository;

    public SourceCode addSourceCode(SourceCode sourceCode){
        return sourceCodeRepository.save(sourceCode);
    }

    public void deleteSourceCode(Integer id){
        if(sourceCodeRepository.existsById(id)){
            sourceCodeRepository.deleteById(id);
        }
        else {
            throw new RuntimeException("Error delete source code with id: " + id);
        }
    }

    public SourceCode getSourceCode(Integer id){
        Optional<SourceCode> sourceCode = sourceCodeRepository.findById(id);
        return sourceCode.orElse(null);
    }
}
