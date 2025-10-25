package com.example.testsapi.service;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
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
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserTestRepository userTestRepository;

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

    public static List<Integer> getSubObjectivesById(int idObjectiv, String xmlFileName) {
        List<Integer> subIds = new ArrayList<>();
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            InputStream xmlInput = new ClassPathResource(xmlFileName).getInputStream();
            Document document = documentBuilder.parse(xmlInput);

            NodeList objectives = document.getElementsByTagName("obiectiv");
            for (int i = 0; i < objectives.getLength(); i++) {
                Element objectiv = (Element) objectives.item(i);
                if (String.valueOf(idObjectiv).equals(objectiv.getAttribute("id"))) {
                    NodeList subObjectives = objectiv.getElementsByTagName("obiectiv");
                    for (int j = 0; j < subObjectives.getLength(); j++) {
                        Element subvObj = (Element) subObjectives.item(j);
                        subIds.add(Integer.parseInt(subvObj.getAttribute("id")));
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return subIds;
    }


    private List<List<Question>> selectToGenerateBeginnerTest(String level, int idObjectiv) {
        List<List<Question>> questionsSubObjectives = new ArrayList<>();

        String xmlPath = new ClassPathResource("objectives.xml").getPath();
        System.out.println(xmlPath);
        List<Integer> subObjIds = getSubObjectivesById(idObjectiv, xmlPath);

        for (Integer subObjId : subObjIds) {
            List<Question> questions = questionRepository.findByLevelAndObjectives(level, idObjectiv, subObjId);
            questionsSubObjectives.add(questions);

            System.out.println("Găsite " + questions.size() +
                    " întrebări pentru subobiectivul " + subObjId);
        }

        return questionsSubObjectives;
    }

    //generate test / objectiv
    private List<Question> generateTestBeginnerAndIntermediate(String level, int obj) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);


        List<List<Question>> extractperSubObjective = selectToGenerateBeginnerTest(level, obj);

        int sizeList = extractperSubObjective.size();
        int questRemaining = numberQuestions % sizeList;
        int numberExtract = numberQuestions / sizeList;

        System.out.println("Nr sub-obiective: " + sizeList);
        System.out.println("Intrebari per sub-obiectiv: " + numberExtract);
        System.out.println("Rest de distribuit: " + questRemaining);

        List<Question> test = new ArrayList<>();

        for (List<Question> subList : extractperSubObjective) {
            Collections.shuffle(subList);
            for (int i = 0; i < Math.min(numberExtract, subList.size()); i++) {
                test.add(subList.get(i));
            }
        }

        int idx = 0;
        while (questRemaining > 0) {
            List<Question> subList = extractperSubObjective.get(idx % sizeList);
            if (!subList.isEmpty()) {
                test.add(subList.get(random.nextInt(subList.size()))); // luam random
                questRemaining--;
            }
            idx++;
        }

        Collections.shuffle(test);

        return test;
    }

    public Test writeBeginnerIntermediateTest(String userId, String level, int obj){
        List<Question> questions = generateTestBeginnerAndIntermediate(level, obj);
        String idTest = generateId(userId, questions);
        int nrOfQuestions = questions.size();
        int duration = extractdurationOfTest(questions);

        UserTest userTest = new UserTest();
        userTest.setIdTest(idTest);
        userTest.setIdUser(userId);
        userTestRepository.save(userTest);

        Test test = new Test();
        test.setIdTest(idTest);
        test.setNrOfObjectives(1);
        test.setLevel(LevelTest.BEGINNER);
        test.setNrOfQuestions(nrOfQuestions);
        test.setEstimatedDuration(duration);
        testRepository.save(test);

        for (Question q : questions) {
            QuestionTest link = new QuestionTest();
            link.setIdTest(idTest);
            link.setIdQuestion(q.getQuestionId().getIdQuestion());
            questionTestRepository.save(link);
        }
        return test;
    }
}

