package com.example.testsapi.service;

import com.example.testsapi.entity.Pair;
import com.example.testsapi.entity.SecurityQuestion;
import com.example.testsapi.repository.SecurityQuestionRepository;
import com.example.testsapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityQuestionService {
    private final SecurityQuestionRepository securityQuestionRepository;
    private final UserRepository userRepository;

    public SecurityQuestionService(SecurityQuestionRepository securityQuestionRepository, UserRepository userRepository) {
        this.securityQuestionRepository = securityQuestionRepository;
        this.userRepository = userRepository;
    }

    public List<Pair<String, String>> getSecurityQuestions(String userId){
        List<Pair<String, String>> pairList = new ArrayList<>();
        List<SecurityQuestion> sq = securityQuestionRepository.getAllByUserId(userId);
        if(sq == null){
            System.err.println("Sequrity question - not exist");
        }
        for(SecurityQuestion s : sq) {
            Pair<String, String> pair = new Pair<>(s.getQuestion(), s.getResponse());
            pairList.add(pair);
            if (pairList.size() == 0) {
                System.err.println("Pair list is empty");
            }
        }
        return pairList;
    }
}
