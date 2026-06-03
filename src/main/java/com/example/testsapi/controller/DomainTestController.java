package com.example.testsapi.controller;

import com.example.testsapi.entity.TestOfQuestions;
import com.example.testsapi.service.DomainTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dedicat abordarii cu DOMENIU.
 *
 * Expune cele 3 categorii de teste, fiecare cu cele 3 niveluri de dificultate
 * (level: SOFTQUESTION / MIDLELEVELQUESTION / HARDQUESTION):
 *   1. obiective principale (unul sau mai multe)
 *   2. obiective secundare (unul sau mai multe)
 *   3. test final (toate obiectivele)
 *
 * Parametrul `domains` este OPTIONAL:
 *   - daca lipseste  -> testul se genereaza FARA filtru de domeniu
 *                       (comportament identic cu TestService clasic)
 *   - daca e prezent -> intrebarile se filtreaza si dupa domeniu
 *
 * Astfel acelasi endpoint demonstreaza ambele abordari, util pentru prototipul didactic.
 */
@RestController
@RequestMapping("/api/domain-tests")
public class DomainTestController {

    @Autowired
    private DomainTestService domainTestService;

    /**
     * CATEGORIA 1 - test centrat pe obiective principale.
     *
     * Exemplu (cu domeniu):
     *   POST /api/domain-tests/objectives?userId=u123&level=SOFTQUESTION&obj=3,5&domains=OOP,Exceptions
     * Exemplu (fara domeniu):
     *   POST /api/domain-tests/objectives?userId=u123&level=HARDQUESTION&obj=3
     */

    //http://localhost:8080/api/domain-tests/objectives?userId=U00035&level=SOFTQUESTION&obj=1,2&domains=java
    @PostMapping("/objectives")
    public ResponseEntity<TestOfQuestions> generateByMainObjectives(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam int[] obj,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writeTestSingleObjective(userId, level, obj, domains);
        return ResponseEntity.ok(test);
    }

    /**
     * CATEGORIA 2 - test centrat pe obiective secundare (sub-obiective).
     *
     * Exemplu (cu domeniu):
     *   POST /api/domain-tests/subobjectives?userId=u123&level=MIDLELEVELQUESTION&subobjectives=31,32&domains=OOP
     * Exemplu (fara domeniu):
     *   POST /api/domain-tests/subobjectives?userId=u123&level=SOFTQUESTION&subobjectives=31,32
     */

    // http://localhost:8080/api/domain-tests/subobjectives?userId=U00035&level=MIDLELEVELQUESTION&subobjectives=11&domains=java
    @PostMapping("/subobjectives")
    public ResponseEntity<TestOfQuestions> generateBySecondaryObjectives(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam int[] subobjectives,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writeTestSingleAndMoreSubobjectives(
                userId, level, subobjectives, domains);
        return ResponseEntity.ok(test);
    }

    /**
     * CATEGORIA 3 - test final (toate obiectivele).
     *
     * Exemplu (cu domeniu):
     *   POST /api/domain-tests/final?userId=u123&level=HARDQUESTION&domains=OOP,Collections
     * Exemplu (fara domeniu):
     *   POST /api/domain-tests/final?userId=u123&level=SOFTQUESTION
     */

    //http://localhost:8080/api/domain-tests/final?userId=U00035&level=HARDQUESTION&domains=java
    @PostMapping("/final")
    public ResponseEntity<TestOfQuestions> generateFinalTest(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writingFinalTest(userId, level, domains);
        return ResponseEntity.ok(test);
    }

    // ------------------------------------------------------------------
    //  Endpoint-uri auxiliare utile pentru demo / UI
    // ------------------------------------------------------------------

    /**
     * Lista domeniilor distincte disponibile in banca de intrebari.
     * Util pentru a popula un dropdown in UI inainte de generarea testului.
     *   GET /api/domain-tests/domains
     */
//    @GetMapping("/domains")
//    public ResponseEntity<java.util.List<String>> getAvailableDomains() {
//        return ResponseEntity.ok(domainTestService.getAvailableDomains());
//    }

    // ============================================================================
//  ████  MATE11 — RUTE NOI, PE OBIECTIVELE CLASEI 11  ████
// ============================================================================
//  Se adauga ACESTE metode in clasa DomainTestController, langa rutele de Mate9.
//  Sunt identice ca structura cu rutele existente; difera doar metoda din
//  serviciu apelata (...Mate11) si calea (sufix -mate11).
//
//  Reaminteste-ti context-path-ul: ruta reala include /api/v1, deci de pe site
//  apelul e catre /api/v1/api/domain-tests/final-mate11 etc.
// ============================================================================

// CATEGORIA 1 - obiective principale (Mate11)
// POST /api/domain-tests/objectives-mate11?userId=U00035&level=SOFTQUESTION&obj=1,2&domains=Mate11
    @PostMapping("/objectives-mate11")
    public ResponseEntity<TestOfQuestions> generateByMainObjectivesMate11(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam int[] obj,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writeTestSingleObjectiveMate11(userId, level, obj, domains);
        return ResponseEntity.ok(test);
    }

    // CATEGORIA 2 - obiective secundare (Mate11)
// POST /api/domain-tests/subobjectives-mate11?userId=U00035&level=MIDLELEVELQUESTION&subobjectives=11,21&domains=Mate11
    @PostMapping("/subobjectives-mate11")
    public ResponseEntity<TestOfQuestions> generateBySecondaryObjectivesMate11(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam int[] subobjectives,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writeTestSingleAndMoreSubobjectivesMate11(
                userId, level, subobjectives, domains);
        return ResponseEntity.ok(test);
    }

    // CATEGORIA 3 - test final (Mate11)
// POST /api/domain-tests/final-mate11?userId=U00035&level=HARDQUESTION&domains=Mate11
    @PostMapping("/final-mate11")
    public ResponseEntity<TestOfQuestions> generateFinalTestMate11(
            @RequestParam String userId,
            @RequestParam String level,
            @RequestParam(required = false) String[] domains) {

        TestOfQuestions test = domainTestService.writingFinalTestMate11(userId, level, domains);
        return ResponseEntity.ok(test);
    }
}