package com.example.testsapi.service;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.MainObjectiveRepository;
import com.example.testsapi.repository.QuestionTestRepository;
import com.example.testsapi.repository.SecondaryObjectiveRepository;
import com.example.testsapi.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TestService {

    @Autowired
    private TestRepository testRepository;
    @Autowired
    private QuestionTestRepository questionTestRepository;
    @Autowired
    private MainObjectiveRepository mainObjectivesRepository;
    @Autowired
    private SecondaryObjectiveRepository secondaryObjectivesRepository;

    //metoda aceasta este utilizata in controller, aceasta apeland generarea idului testului
    public String generateIdForUser(String userId, List<Question> questions) {
        return generateId(userId, questions);
    }

    //genereaza id pentru fiecare test
    private String generateId(String userId, List<Question> questions) {
        String idTest = "";

        Random random = new Random();
        List<Integer> generateRandomNumber = new ArrayList<Integer>();
        int number = 0;
        do {
            number = random.nextInt(900) + 100;
        } while (generateRandomNumber.contains(number));
        generateRandomNumber.add(number);

        List<Object> objectives = extractObjectivesFromQuestions(questions);
        List<Object> subobj = extractSecondaryObjectivesFromQuestion(questions);

        if (objectives.size() == 1) {
            idTest = userId + "ob" + number;
        } else if (objectives.size() > 1 && objectives.size() < 4) {
            StringBuilder sb = new StringBuilder(userId);
            for (Object obj : objectives) {
                if (obj instanceof MainObjective mo)
                    sb.append(mo.getIdObjectiv());
                else if (obj instanceof SecondaryObjective so)
                    sb.append(so.getIdObjectiv());
            }
            sb.append(number);
            idTest = sb.toString();
        } else {
            idTest = userId + "F" + number;
        }

        while (testRepository.existsByIdTest(idTest)) {
            number = new Random().nextInt(900) + 100;
            idTest = userId + "F" + number;
        }

        return idTest;
    }

    //extrage obiectuvele peincipale ale intrebarilor retunate inlista de intrebari
    //se poate testa in controller dand o lista de id ale unor intrebari din bd in ruta
    private List<Object> extractObjectivesFromQuestions(List<Question> questions) {
        List<Object> objectives = new ArrayList<>();

        for (Question q : questions) {

            var mainObjs = mainObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion());;

            objectives.addAll(mainObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion()));

            for (MainObjective mo : mainObjs) {
                System.out.println("Obiectiv principal: " + mo.getIdObjectiv() + " - " + mo.getNameObjectiv());
            }
        }
        System.out.println("Total obiective extrase: " + objectives.size());
        return objectives;
    }

    // ca metoda de mai sus doar ca pentru obiectuve secundare
    private List<Object> extractSecondaryObjectivesFromQuestion(List<Question> questions){
        List<Object> subObjectives = new ArrayList<>();
        for(Question q : questions){
            var secondaryObjs = secondaryObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion());
            subObjectives.addAll(secondaryObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion()));

            for (SecondaryObjective so : secondaryObjs) {
                System.out.println("Obiectiv secundar: " + so.getIdObjectiv() + " - " + so.getNameObjectiv());
            }
        }
        System.out.println("Total sub obiective extrase: " + subObjectives.size());
        return subObjectives;
    }

    // extrage idul subobiectivelor
    private List<String> determinateObjectives(List<Objectiv> subObj){
        List<String> subobsId = new ArrayList<>();
        for(Objectiv oj : subObj){
            String idO = oj.getId();
            subobsId.add(idO);
        }
        return subobsId;
    }


    //extrage obiectivele pricipale corespunzatoare obiectivelor secundare
    public List<Objectiv> addPrinciplesObjectivesInList(List<Objectiv> secObj) {
        List<Objectiv> objectives = new ArrayList<>();
        Set<Objectiv> uniqObj = new TreeSet<>();
        List<String> idSubobj = determinateObjectives(secObj);

        try {
            File xmlFile = new ClassPathResource("objectives.xml").getFile();
            List<Objectiv> mainObj = Objectiv.readObjectiv(xmlFile.getAbsolutePath());

            for (Objectiv mo : mainObj) {
                for (String str : idSubobj) {
                    if (mo.getId().charAt(0) == str.charAt(0)) {
                        uniqObj.add(mo);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        objectives.addAll(uniqObj);
        return objectives;
    }

    private int extractdurationOfTest(List<Question> questions){
        int duration = 0;
        for(Question q : questions){
            if(q.getLevel().equals("SOFTQUESTION".trim())){
                duration += 60;
            }
            else if(q.getLevel().equals("MIDLELEVELQUESTION".trim())){
                duration += 100;
            }
            else if(q.getLevel().equals("HARDQUESTION".trim())){
                duration += 150;
            }
        }
        return duration;
    }
}

