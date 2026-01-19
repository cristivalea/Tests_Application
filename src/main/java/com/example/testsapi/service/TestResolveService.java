package com.example.testsapi.service;

import com.example.testsapi.dto.TestResolveItemDTO;
import com.example.testsapi.dto.TestResolveResultDTO;
import com.example.testsapi.entity.Response;
import com.example.testsapi.entity.Responses;
import com.example.testsapi.entity.TestResolve;
import com.example.testsapi.repository.ResponseRepository;
import com.example.testsapi.repository.ResponsesRepository;
import com.example.testsapi.repository.TestResolveRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Service
//@Transactional
//public class TestResolveService {
//
//    private final ResponsesRepository responsesRepository;
//    private final ResponseRepository responseRepository;
//    private final TestResolveRepository testResolveRepository;
//
//    public TestResolveService(ResponsesRepository responsesRepository,
//                              ResponseRepository responseRepository,
//                              TestResolveRepository testResolveRepository) {
//        this.responsesRepository = responsesRepository;
//        this.responseRepository = responseRepository;
//        this.testResolveRepository = testResolveRepository;
//    }
//
//    // 🔹 Evaluează tot testul
//    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//
//        int total = items.size();
//        int correct = 0;
//
//        for (TestResolveItemDTO item : items) {
//
//            boolean isCorrect = evaluateQuestion(
//                    item.getIdQuestion(),
//                    item.getSelectedAnswers()
//            );
//
//            TestResolve tr = new TestResolve();
//            tr.setIdUser(item.getIdUser());
//            tr.setIdTest(item.getIdTest());
//            tr.setIdQuestion(item.getIdQuestion());
//            tr.setResponse(isCorrect ? true : false);
//
//            testResolveRepository.save(tr);
//
//            if (isCorrect) {
//                correct++;
//            }
//        }
//
//        return new TestResolveResultDTO(
//                total,
//                correct,
//                (correct * 100.0) / total
//        );
//    }
//
//    // 🔹 Evaluează o întrebare pe baza TEXTULUI
//    private boolean evaluateQuestion(String idQuestion,
//                                     List<String> selectedAnswers) {
//
//        Set<String> correctAnswers = getCorrectAnswersForQuestion(idQuestion);
//        Set<String> userAnswers = selectedAnswers.stream()
//                .map(String::trim)
//                .collect(Collectors.toSet());
//
//        return correctAnswers.equals(userAnswers);
//    }
//
//    // 🔹 Ia răspunsurile CORECTE din DB
//    private Set<String> getCorrectAnswersForQuestion(String idQuestion) {
//
//        return responsesRepository.findByIdQuestion(idQuestion)
//                .stream()
//                .map(Responses::getIdResponse)
//                .map(id -> responseRepository.findById(id)
//                        .orElseThrow(() ->
//                                new RuntimeException("Response not found")))
//                .filter(r -> r.getResponseValue() == 1)
//                .map(r -> r.getContent().trim())
//                .collect(Collectors.toSet());
//    }
//}

@Service
public class TestResolveService {

    private final ResponsesRepository responsesRepository;
    private final ResponseRepository responseRepository;
    private final TestResolveRepository rezolvareTestRepository;

    public TestResolveService(ResponsesRepository responsesRepository,
                              ResponseRepository responseRepository,
                              TestResolveRepository rezolvareTestRepository) {
        this.responsesRepository = responsesRepository;
        this.responseRepository = responseRepository;
        this.rezolvareTestRepository = rezolvareTestRepository;
    }

    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {

        int correct = 0;

        for (TestResolveItemDTO item : items) {

            boolean isCorrect = evaluateQuestion(
                    item.getIdQuestion(),
                    item.getSelectedAnswers()
            );

            if (isCorrect) {
                correct++;
            }

            TestResolve rez = new TestResolve();
            rez.setIdUser(item.getIdUser());
            rez.setIdTest(item.getIdTest());
            rez.setIdQuestion(item.getIdQuestion());
            rez.setResponse(isCorrect ? true : false);

            rezolvareTestRepository.save(rez);
        }

        return new TestResolveResultDTO(
                items.size(),
                correct,
                correct * 100.0 / items.size()
        );
    }

    private boolean evaluateQuestion(String idQuestion, List<String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            System.out.println("ATENȚIE: Nu s-au selectat răspunsuri pentru întrebarea: " + idQuestion);
            return false;
        }

        // toate răspunsurile pentru întrebare
        List<Responses> links =
                responsesRepository.findByIdQuestion(idQuestion);

        // răspunsurile CORECTE din DB (content)
        Set<String> correctAnswers = links.stream()
                .map(r -> responseRepository.findById(r.getIdResponse()).orElse(null))
                .filter(r -> r != null && r.getResponseValue() == 1)
                .map(r -> r.getContent().trim())
                .collect(Collectors.toSet());

        // răspunsurile utilizatorului
        Set<String> user = userAnswers.stream()
                .map(String::trim)
                .collect(Collectors.toSet());

        return correctAnswers.equals(user);
    }
}

