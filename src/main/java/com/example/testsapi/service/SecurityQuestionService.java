package com.example.testsapi.service;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.SecurityQuestionRepository;
import com.example.testsapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public SecurityQuestion addSecurityQuestion(String userId, String sq, String resp) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        SecurityQuestion sq1 = new SecurityQuestion();
        sq1.setQuestion(sq);
        sq1.setResponse(resp);
        sq1.setStatus(Status.ACTIVE);
        sq1.setUser(userOpt.get());
        return securityQuestionRepository.save(sq1);
    }

    public void deleteSequrityQuestion(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }

        List<SecurityQuestion> questions = securityQuestionRepository.getAllByUserId(userId);
        if (questions.isEmpty()) {
            throw new RuntimeException("No security questions found for user " + userId);
        }

        for (SecurityQuestion sq : questions) {
            sq.setStatus(Status.INACTIV);
            securityQuestionRepository.save(sq);
        }
    }

}
