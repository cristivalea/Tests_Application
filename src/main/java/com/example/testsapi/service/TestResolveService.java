////package com.example.testsapi.service;
////
////import com.example.testsapi.dto.TestResolveItemDTO;
////import com.example.testsapi.dto.TestResolveResultDTO;
////import com.example.testsapi.entity.*;
////import com.example.testsapi.repository.*;
////import jakarta.transaction.Transactional;
////import org.springframework.stereotype.Service;
////
////import java.util.HashSet;
////import java.util.List;
////import java.util.Set;
////import java.util.stream.Collectors;
////
////@Service
////@Transactional
////public class TestResolveService {
////
////    private final ResponsesRepository responsesRepository;
////    private final ResponseRepository responseRepository;
////    private final TestResolveRepository testResolveRepository;
////
////    public TestResolveService(ResponsesRepository responsesRepository,
////                              ResponseRepository responseRepository,
////                              TestResolveRepository testResolveRepository) {
////        this.responsesRepository = responsesRepository;
////        this.responseRepository = responseRepository;
////        this.testResolveRepository = testResolveRepository;
////    }
////
////    // 🔹 Evaluează tot testul
////    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
////
////        int total = items.size();
////        int correct = 0;
////
////        for (TestResolveItemDTO item : items) {
////
////            boolean isCorrect = evaluateQuestion(
////                    item.getIdQuestion(),
////                    item.getSelectedAnswers()
////            );
////
////            TestResolve tr = new TestResolve();
////            tr.setIdUser(item.getIdUser());
////            tr.setIdTest(item.getIdTest());
////            tr.setIdQuestion(item.getIdQuestion());
////            tr.setResponse(isCorrect ? true : false);
////
////            testResolveRepository.save(tr);
////
////            if (isCorrect) {
////                correct++;
////            }
////        }
////
////        return new TestResolveResultDTO(
////                total,
////                correct,
////                (correct * 100.0) / total
////        );
////    }
////
////    // 🔹 Evaluează o întrebare pe baza TEXTULUI
////    private boolean evaluateQuestion(String idQuestion,
////                                     List<String> selectedAnswers) {
////
////        Set<String> correctAnswers = getCorrectAnswersForQuestion(idQuestion);
////        Set<String> userAnswers = selectedAnswers.stream()
////                .map(String::trim)
////                .collect(Collectors.toSet());
////
////        return correctAnswers.equals(userAnswers);
////    }
////
////    // 🔹 Ia răspunsurile CORECTE din DB
////    private Set<String> getCorrectAnswersForQuestion(String idQuestion) {
////
////        return responsesRepository.findByIdQuestion(idQuestion)
////                .stream()
////                .map(Responses::getIdResponse)
////                .map(id -> responseRepository.findById(id)
////                        .orElseThrow(() ->
////                                new RuntimeException("Response not found")))
////                .filter(r -> r.getResponseValue() == 1)
////                .map(r -> r.getContent().trim())
////                .collect(Collectors.toSet());
////    }
////}
////
////////@Service
////////@Transactional
////////public class TestResolveService {
////////
////////    private final ResponsesRepository responsesRepository;
////////    private final ResponseRepository responseRepository;
////////    private final TestResolveRepository rezolvareTestRepository;
////////    private TestResultsRepository testResultsRepository;
////////    private QuestionRepository questionRepository;
////////
////////    public TestResolveService(ResponsesRepository responsesRepository,
////////                              ResponseRepository responseRepository,
////////                              TestResolveRepository rezolvareTestRepository,
////////                              TestResultsRepository testResultsRepository,
////////                              QuestionRepository questionRepository) {
////////        this.responsesRepository = responsesRepository;
////////        this.responseRepository = responseRepository;
////////        this.rezolvareTestRepository = rezolvareTestRepository;
////////        this.testResultsRepository = testResultsRepository;
////////        this.questionRepository = questionRepository;
////////    }
////////
////////// Correction without taking into account whether questions are marked as gradeable or not
//////////    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//////////
//////////        int correct = 0;
//////////
//////////        for (TestResolveItemDTO item : items) {
//////////
//////////            boolean isCorrect = evaluateQuestion(
//////////                    item.getIdQuestion(),
//////////                    item.getSelectedAnswers()
//////////            );
//////////
//////////            if (isCorrect) {
//////////                correct++;
//////////            }
//////////
//////////            TestResolve rez = new TestResolve();
//////////            rez.setIdUser(item.getIdUser());
//////////            rez.setIdTest(item.getIdTest());
//////////            rez.setIdQuestion(item.getIdQuestion());
//////////            rez.setResponse(isCorrect ? true : false);
//////////
//////////            rezolvareTestRepository.save(rez);
//////////        }
//////////
//////////        double percentage = correct * 100.0 / items.size();
//////////
//////////        this.testResultsRepository.save(new TestResults(0, items.get(0).getIdUser(), items.get(0).getIdTest(), percentage));
//////////        return new TestResolveResultDTO(
//////////                items.size(),
//////////                correct,
//////////                percentage
//////////        );
//////////    }
//////////public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//////////    if (items == null || items.isEmpty()) {
//////////        return new TestResolveResultDTO(0, 0, 0.0);
//////////    }
//////////
//////////    String currentTestId = items.get(0).getIdTest();
//////////    String currentUserId = items.get(0).getIdUser();
//////////
//////////
//////////    int totalPossiblePoints = questionRepository.countGradedQuestionsByTestId(currentTestId);
//////////
//////////    int correctCount = 0;
//////////
//////////    for (TestResolveItemDTO item : items) {
//////////
//////////        Question question = questionRepository.findByQuestionId_IdQuestion(item.getIdQuestion())
//////////                .orElseThrow(() -> new RuntimeException("Question not found: " + item.getIdQuestion()));
//////////
//////////        boolean isCorrect = evaluateQuestion(
//////////                item.getIdQuestion(),
//////////                item.getSelectedAnswers()
//////////        );
//////////
//////////        if (question.getScore() == 1 && isCorrect) {
//////////            correctCount++;
//////////        }
//////////
//////////        TestResolve rez = new TestResolve();
//////////        rez.setIdUser(currentUserId);
//////////        rez.setIdTest(currentTestId);
//////////        rez.setIdQuestion(item.getIdQuestion());
//////////        rez.setResponse(isCorrect);
//////////        rezolvareTestRepository.save(rez);
//////////    }
//////////
//////////    double percentage = (totalPossiblePoints > 0)
//////////            ? (correctCount * 100.0 / totalPossiblePoints)
//////////            : 0.0;
//////////
//////////    this.testResultsRepository.save(new TestResults(0, currentUserId, currentTestId, percentage));
//////////
//////////    return new TestResolveResultDTO(
//////////            totalPossiblePoints,
//////////            correctCount,
//////////            percentage
//////////    );
//////////}
//////////    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//////////        if (items == null || items.isEmpty()) {
//////////            return new TestResolveResultDTO(0, 0, 0.0);
//////////        }
//////////
//////////        String currentTestId = items.get(0).getIdTest();
//////////        String currentUserId = items.get(0).getIdUser();
//////////
//////////
//////////        int totalPossiblePoints = questionRepository.countGradedQuestionsByTestId(currentTestId);
//////////
//////////        int correctCount = 0;
//////////
//////////        for (TestResolveItemDTO item : items) {
//////////
//////////            Question question = questionRepository.findByQuestionId_IdQuestion(item.getIdQuestion())
//////////                    .orElseThrow(() -> new RuntimeException("Question not found: " + item.getIdQuestion()));
//////////
//////////            boolean isCorrect = evaluateQuestion(
//////////                    item.getIdQuestion(),
//////////                    item.getSelectedAnswers()
//////////            );
//////////
//////////            if (question.getScore() == 1 && isCorrect) {
//////////                correctCount++;
//////////            }
//////////
//////////            TestResolve rez = new TestResolve();
//////////            rez.setIdUser(currentUserId);
//////////            rez.setIdTest(currentTestId);
//////////            rez.setIdQuestion(item.getIdQuestion());
//////////            rez.setResponse(isCorrect);
//////////            rezolvareTestRepository.save(rez);
//////////
//////////        }
//////////
//////////        double percentage = (totalPossiblePoints > 0)
//////////                ? (correctCount * 100.0 / totalPossiblePoints)
//////////                : 0.0;
//////////
//////////        this.testResultsRepository.save(new TestResults(0, currentUserId, currentTestId, percentage));
//////////
//////////        return new TestResolveResultDTO(
//////////                totalPossiblePoints,
//////////                correctCount,
//////////                percentage
//////////        );
//////////    }
////////public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
////////    ystem.out.println(">>> evaluateTest START, items=" + (items == null ? "null" : items.size()));
////////    if (items == null || items.isEmpty()) {
////////        System.out.println(">>> items gol, ies");
////////        return new TestResolveResultDTO(0, 0, 0.0);
////////    }
////////
////////    String currentTestId = items.get(0).getIdTest();
////////    String currentUserId = items.get(0).getIdUser();
////////    System.out.println(">>> testId=" + currentTestId + " userId=" + currentUserId);
////////
////////    int totalPossiblePoints = questionRepository.countGradedQuestionsByTestId(currentTestId);
////////    int correctCount = 0;
////////
////////    for (TestResolveItemDTO item : items) {
////////        System.out.println(">>> procesez intrebarea: " + item.getIdQuestion());
////////        // NU mai arunca excepție care face rollback la tot
////////        Question question = questionRepository
////////                .findByQuestionId_IdQuestion(item.getIdQuestion())
////////                .orElse(null);
////////        if (question == null) {
////////            System.err.println("Question not found: " + item.getIdQuestion());
////////            continue;
////////        }
////////
////////        boolean isCorrect = evaluateQuestion(item.getIdQuestion(), item.getSelectedAnswers());
////////        if (question.getScore() == 1 && isCorrect) {
////////            correctCount++;
////////        }
////////
////////        TestResolve rez = new TestResolve();
////////        rez.setIdUser(currentUserId);
////////        rez.setIdTest(currentTestId);
////////        rez.setIdQuestion(item.getIdQuestion());
////////        rez.setResponse(isCorrect);
////////        rezolvareTestRepository.save(rez);
////////        System.out.println(">>> SALVAT TestResolve id=" + re.getId());
////////    }
////////
////////    double percentage = (totalPossiblePoints > 0)
////////            ? (correctCount * 100.0 / totalPossiblePoints)
////////            : 0.0;
////////
////////    // NU pasa 0 daca id e @GeneratedValue — foloseste constructor fara id
////////    TestResults result = new TestResults();
////////    result.setIdUser(currentUserId);
////////    result.setIdTest(currentTestId);
////////    result.setPunctaj(percentage);   // adapteaza la numele real al setterului
////////    testResultsRepository.save(result);
////////
////////    return new TestResolveResultDTO(totalPossiblePoints, correctCount, percentage);
////////}
////////
////////    private boolean evaluateQuestion(String idQuestion, List<String> userAnswers) {
////////        if (userAnswers == null || userAnswers.isEmpty()) {
////////            System.out.println("ATENȚIE: Nu s-au selectat răspunsuri pentru întrebarea: " + idQuestion);
////////            return false;
////////        }
////////
////////        // toate răspunsurile pentru întrebare
////////        List<Responses> links =
////////                responsesRepository.findByIdQuestion(idQuestion);
////////
////////        // răspunsurile CORECTE din DB (content)
////////        Set<String> correctAnswers = links.stream()
////////                .map(r -> responseRepository.findById(r.getIdResponse()).orElse(null))
////////                .filter(r -> r != null && r.getResponseValue() == 1)
////////                .map(r -> r.getContent().trim())
////////                .collect(Collectors.toSet());
////////
////////        // răspunsurile utilizatorului
////////        Set<String> user = userAnswers.stream()
////////                .map(String::trim)
////////                .collect(Collectors.toSet());
////////
////////        return correctAnswers.equals(user);
////////    }
////////}
////////
//////
//////@Service
//////@Transactional
//////public class TestResolveService {
//////
//////    private final ResponsesRepository responsesRepository;
//////    private final ResponseRepository responseRepository;
//////    private final TestResolveRepository rezolvareTestRepository;
//////    private TestResultsRepository testResultsRepository;
//////    private QuestionRepository questionRepository;
//////
//////    public TestResolveService(ResponsesRepository responsesRepository,
//////                              ResponseRepository responseRepository,
//////                              TestResolveRepository rezolvareTestRepository,
//////                              TestResultsRepository testResultsRepository,
//////                              QuestionRepository questionRepository) {
//////        this.responsesRepository = responsesRepository;
//////        this.responseRepository = responseRepository;
//////        this.rezolvareTestRepository = rezolvareTestRepository;
//////        this.testResultsRepository = testResultsRepository;
//////        this.questionRepository = questionRepository;
//////    }
//////
//////    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//////        if (items == null || items.isEmpty()) {
//////            return new TestResolveResultDTO(0, 0, 0.0);
//////        }
//////
//////        String currentTestId = items.get(0).getIdTest();
//////        String currentUserId = items.get(0).getIdUser();
//////
//////        int totalPossiblePoints = questionRepository.countGradedQuestionsByTestId(currentTestId);
//////
//////        int correctCount = 0;
//////
//////        for (TestResolveItemDTO item : items) {
//////
//////            // ÎNAINTE: orElseThrow arunca excepție și oprea metoda înainte de orice save
//////            Question question = questionRepository.findByQuestionId_IdQuestion(item.getIdQuestion())
//////                    .orElse(null);
//////
//////            boolean isCorrect = evaluateQuestion(
//////                    item.getIdQuestion(),
//////                    item.getSelectedAnswers()
//////            );
//////
//////            if (question != null && question.getScore() == 1 && isCorrect) {
//////                correctCount++;
//////            }
//////
//////            TestResolve rez = new TestResolve();
//////            rez.setIdUser(currentUserId);
//////            rez.setIdTest(currentTestId);
//////            rez.setIdQuestion(item.getIdQuestion());
//////            rez.setResponse(isCorrect);
//////            rezolvareTestRepository.save(rez);
//////        }
//////
//////        double percentage = (totalPossiblePoints > 0)
//////                ? (correctCount * 100.0 / totalPossiblePoints)
//////                : 0.0;
//////
//////        this.testResultsRepository.save(new TestResults(0, currentUserId, currentTestId, percentage));
//////
//////        return new TestResolveResultDTO(
//////                totalPossiblePoints,
//////                correctCount,
//////                percentage
//////        );
//////    }
//////
//////    private boolean evaluateQuestion(String idQuestion, List<String> userAnswers) {
//////        if (userAnswers == null || userAnswers.isEmpty()) {
//////            System.out.println("ATENȚIE: Nu s-au selectat răspunsuri pentru întrebarea: " + idQuestion);
//////            return false;
//////        }
//////
//////        List<Responses> links =
//////                responsesRepository.findByIdQuestion(idQuestion);
//////
//////        Set<String> correctAnswers = links.stream()
//////                .map(r -> responseRepository.findById(r.getIdResponse()).orElse(null))
//////                .filter(r -> r != null && r.getResponseValue() == 1)
//////                .map(r -> r.getContent().trim())
//////                .collect(Collectors.toSet());
//////
//////        Set<String> user = userAnswers.stream()
//////                .map(String::trim)
//////                .collect(Collectors.toSet());
//////
//////        return correctAnswers.equals(user);
//////    }
//////}
////
//package com.example.testsapi.service;
//
//import com.example.testsapi.dto.TestResolveItemDTO;
//import com.example.testsapi.dto.TestResolveResultDTO;
//import com.example.testsapi.entity.*;
//import com.example.testsapi.repository.*;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class TestResolveService {
//
//    private final ResponsesRepository responsesRepository;
//    private final ResponseRepository responseRepository;
//    private final TestResolveRepository rezolvareTestRepository;
//    private final TestResultsRepository testResultsRepository;
//    private final QuestionRepository questionRepository;
//
//    public TestResolveService(ResponsesRepository responsesRepository,
//                              ResponseRepository responseRepository,
//                              TestResolveRepository rezolvareTestRepository,
//                              TestResultsRepository testResultsRepository,
//                              QuestionRepository questionRepository) {
//        this.responsesRepository = responsesRepository;
//        this.responseRepository = responseRepository;
//        this.rezolvareTestRepository = rezolvareTestRepository;
//        this.testResultsRepository = testResultsRepository;
//        this.questionRepository = questionRepository;
//    }
//
//    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {
//
//        // log de diagnostic — sa vezi ce primesti
//        System.out.println(">>> evaluateTest a primit items = "
//                + (items == null ? "NULL" : items.size()));
//
//        if (items == null || items.isEmpty()) {
//            return new TestResolveResultDTO(0, 0, 0.0);
//        }
//
//        String currentTestId = items.get(0).getIdTest();
//        String currentUserId = items.get(0).getIdUser();
//
//        int totalQuestions = 0;   // cate intrebari evaluabile am procesat
//        int correctCount = 0;     // cate au fost corecte
//
//        for (TestResolveItemDTO item : items) {
//
//            // cautam intrebarea, dar daca nu o gasim NU oprim tot testul
//            Question question = questionRepository
//                    .findByQuestionId_IdQuestion(item.getIdQuestion())
//                    .orElse(null);
//
//            if (question == null) {
//                System.err.println("Intrebare negasita, o sar: " + item.getIdQuestion());
//                continue;
//            }
//
//            // numaram doar intrebarile gradabile (score == 1)
//            if (question.getScore() != 1) {
//                // salvam totusi raspunsul, dar nu o numaram la total
//                boolean isCorrectNonGraded =
//                        evaluateQuestion(item.getIdQuestion(), item.getSelectedAnswers());
//                saveResolve(currentUserId, currentTestId, item.getIdQuestion(), isCorrectNonGraded);
//                continue;
//            }
//
//            totalQuestions++;
//
//            boolean isCorrect =
//                    evaluateQuestion(item.getIdQuestion(), item.getSelectedAnswers());
//
//            if (isCorrect) {
//                correctCount++;
//            }
//
//            saveResolve(currentUserId, currentTestId, item.getIdQuestion(), isCorrect);
//        }
//
//        int wrongCount = totalQuestions - correctCount;
//
//        double percentage = (totalQuestions > 0)
//                ? (correctCount * 100.0 / totalQuestions)
//                : 0.0;
//
//        System.out.println(">>> total=" + totalQuestions
//                + " corecte=" + correctCount
//                + " gresite=" + wrongCount
//                + " procent=" + percentage);
//
//        // salvam rezultatul final (constructor fara id — vezi nota de jos)
//        TestResults result = new TestResults();
//        result.setIdUser(currentUserId);
//        result.setIdTest(currentTestId);
//        result.setPunctaj(percentage);   // adapteaza la numele real al setterului
//        testResultsRepository.save(result);
//
//        return new TestResolveResultDTO(totalQuestions, correctCount, percentage);
//    }
//
//    // helper care salveaza un raspuns in tabela test_resolve
//    private void saveResolve(String userId, String testId, String questionId, boolean isCorrect) {
//        TestResolve rez = new TestResolve();
//        rez.setIdUser(userId);
//        rez.setIdTest(testId);
//        rez.setIdQuestion(questionId);
//        rez.setResponse(isCorrect);
//        rezolvareTestRepository.save(rez);
//    }
//
//    private boolean evaluateQuestion(String idQuestion, List<String> userAnswers) {
//        if (userAnswers == null || userAnswers.isEmpty()) {
//            return false;
//        }
//
//        List<Responses> links = responsesRepository.findByIdQuestion(idQuestion);
//
//        Set<String> correctAnswers = links.stream()
//                .map(r -> responseRepository.findById(r.getIdResponse()).orElse(null))
//                .filter(r -> r != null && r.getResponseValue() == 1)
//                .map(r -> r.getContent().trim())
//                .collect(Collectors.toSet());
//
//        Set<String> user = userAnswers.stream()
//                .map(String::trim)
//                .collect(Collectors.toSet());
//
//        return correctAnswers.equals(user);
//    }
//}

package com.example.testsapi.service;

import com.example.testsapi.dto.TestResolveItemDTO;
import com.example.testsapi.dto.TestResolveResultDTO;
import com.example.testsapi.entity.*;
import com.example.testsapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestResolveService {

    private final ResponsesRepository responsesRepository;
    private final ResponseRepository responseRepository;
    private final TestResolveRepository rezolvareTestRepository;
    private final TestResultsRepository testResultsRepository;
    private final QuestionRepository questionRepository;

    public TestResolveService(ResponsesRepository responsesRepository,
                              ResponseRepository responseRepository,
                              TestResolveRepository rezolvareTestRepository,
                              TestResultsRepository testResultsRepository,
                              QuestionRepository questionRepository) {
        this.responsesRepository = responsesRepository;
        this.responseRepository = responseRepository;
        this.rezolvareTestRepository = rezolvareTestRepository;
        this.testResultsRepository = testResultsRepository;
        this.questionRepository = questionRepository;
    }

    public TestResolveResultDTO evaluateTest(List<TestResolveItemDTO> items) {

        System.out.println(">>> evaluateTest a primit items = "
                + (items == null ? "NULL" : items.size()));

        if (items == null || items.isEmpty()) {
            return new TestResolveResultDTO(0, 0, 0.0);
        }

        String currentTestId = items.get(0).getIdTest();
        String currentUserId = items.get(0).getIdUser();

        // TOTALUL REAL de intrebari ale testului (din DB, nu din items)
        int totalQuestions = questionRepository.countGradedQuestionsByTestId(currentTestId);

        int correctCount = 0;   // cate a nimerit userul

        for (TestResolveItemDTO item : items) {

            // cautam intrebarea, dar daca nu o gasim NU oprim tot testul
            Question question = questionRepository
                    .findByQuestionId_IdQuestion(item.getIdQuestion())
                    .orElse(null);

            if (question == null) {
                System.err.println("Intrebare negasita, o sar: " + item.getIdQuestion());
                continue;
            }

            boolean isCorrect =
                    evaluateQuestion(item.getIdQuestion(), item.getSelectedAnswers());

            // numaram corect doar daca intrebarea e gradabila si raspunsul e bun
            if (question.getScore() == 1 && isCorrect) {
                correctCount++;
            }

            // salvam raspunsul in test_resolve in orice caz
            saveResolve(currentUserId, currentTestId, item.getIdQuestion(), isCorrect);
        }

        int wrongCount = totalQuestions - correctCount;

        double percentage = (totalQuestions > 0)
                ? (correctCount * 100.0 / totalQuestions)
                : 0.0;

        System.out.println(">>> total=" + totalQuestions
                + " corecte=" + correctCount
                + " gresite=" + wrongCount
                + " procent=" + percentage);

        // salvam rezultatul final
        TestResults result = new TestResults();
        result.setIdUser(currentUserId);
        result.setIdTest(currentTestId);
        result.setPunctaj(percentage);
        testResultsRepository.save(result);

        return new TestResolveResultDTO(totalQuestions, correctCount, percentage);
    }

    private void saveResolve(String userId, String testId, String questionId, boolean isCorrect) {
        TestResolve rez = new TestResolve();
        rez.setIdUser(userId);
        rez.setIdTest(testId);
        rez.setIdQuestion(questionId);
        rez.setResponse(isCorrect);
        rezolvareTestRepository.save(rez);
    }

    private boolean evaluateQuestion(String idQuestion, List<String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return false;
        }

        List<Responses> links = responsesRepository.findByIdQuestion(idQuestion);

        Set<String> correctAnswers = links.stream()
                .map(r -> responseRepository.findById(r.getIdResponse()).orElse(null))
                .filter(r -> r != null && r.getResponseValue() == 1)
                .map(r -> r.getContent().trim())
                .collect(Collectors.toSet());

        Set<String> user = userAnswers.stream()
                .map(String::trim)
                .collect(Collectors.toSet());

        return correctAnswers.equals(user);
    }
}