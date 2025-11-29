package com.example.testsapi.service;

import com.example.testsapi.entity.*;
import com.example.testsapi.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Autowired
    private QuestionLabelsRepository questionLabelRepository;

    //metoda aceasta este utilizata in controller, aceasta apeland generarea idului testului
    public String generateIdForUser(String userId, List<Question> questions) {
        return generateId(userId, questions);
    }

    //genereaza id pentru fiecare test
//    private String generateId(String userId, List<Question> questions) {
//        String idTest = "";
//
//        Random random = new Random();
//        List<Integer> generateRandomNumber = new ArrayList<Integer>();
//        int number = 0;
//        do {
//            number = random.nextInt(900) + 100;
//        } while (generateRandomNumber.contains(number));
//        generateRandomNumber.add(number);
//
//        System.out.println("==============================" + number + "===========================");
//        List<Object> objectives = extractObjectivesFromQuestions(questions);
//        System.out.println("=========================" + objectives.size() + "===========================================");
//
//        if (objectives.size() == 1) {
//            idTest = userId + "ob" + number;
//        } else if (objectives.size() > 1 && objectives.size() < 4) {
//            StringBuilder sb = new StringBuilder(userId);
//            for (Object obj : objectives) {
//                if (obj instanceof MainObjective mo)
//                    sb.append(mo.getIdObjectiv());
//                else if (obj instanceof SecondaryObjective so)
//                    sb.append(so.getIdObjectiv());
//            }
//            sb.append(number);
//            idTest = sb.toString();
//        }
//        return idTest;
//    }

    private String generateId(String userId, List<Question> questions) {
        String idTest = "";

        Random random = new Random();
        int number = random.nextInt(900) + 100;

        System.out.println("==================== Generare ID pentru test ====================");
        List<Object> objectivesP = extractObjectivesFromQuestions(questions);
        Set<Object> setObj = new TreeSet<>(objectivesP);
        List<Object> objectives = new ArrayList<>(setObj);

        if (objectives.isEmpty()) {
            System.out.println("⚠️ Nu s-au găsit obiective pentru întrebările din test!");
            idTest = userId + "_NOOBJ_" + number;
        } else if (objectives.size() == 1) {
            Object obj = objectives.get(0);
            if (obj instanceof MainObjective mo) {
                idTest = userId + "_OB" + mo.getIdObjectiv() + "_" + number;
            } else if (obj instanceof SecondaryObjective so) {
                idTest = userId + "_OB" + so.getIdObjectiv() + "_" + number;
            }
        } else {
            StringBuilder sb = new StringBuilder(userId);
            for (Object obj : objectives) {
                if (obj instanceof MainObjective mo)
                    sb.append("_").append(mo.getIdObjectiv());
                else if (obj instanceof SecondaryObjective so)
                    sb.append("_").append(so.getIdObjectiv());
            }
            sb.append("_").append(number);
            idTest = sb.toString();
        }
//        if (objectives.isEmpty()) {
//            System.out.println("⚠️ Nu s-au găsit obiective pentru întrebările din test!");
//            idTest = userId + "_NOOBJ_" + number;
//        } else {
//            StringBuilder sb = new StringBuilder(userId);
//            for (Object obj : objectives) {
//                if (obj instanceof MainObjective) {
//                    sb.append("_").append(((MainObjective) obj).getIdObjectiv());
//                } else if (obj instanceof SecondaryObjective) {
//                    sb.append("_").append(((SecondaryObjective) obj).getIdObjectiv());
//                }
//            }
//            sb.append("_").append(number);
//            idTest = sb.toString();
//        }


        System.out.println("ID test generat: " + idTest);
        return idTest;
    }


    //extrage obiectuvele peincipale ale intrebarilor retunate in lista de intrebari
    //se poate testa in controller dand o lista de id ale unor intrebari din bd in ruta
//    private List<Object> extractObjectivesFromQuestions(List<Question> questions) {
//        List<Object> objectives = new ArrayList<>();
//
//        for (Question q : questions) {
//
//            var mainObjs = mainObjectivesRepository.findByIdQuestion(
//                    q.getQuestionId().getIdQuestion().toString()
//            );
//
//
//            objectives.addAll(mainObjs);
//
//            for (MainObjective mo : mainObjs) {
//                System.out.println("Obiectiv principal: " + mo.getIdObjectiv() + " - " + mo.getNameObjectiv());
//            }
//        }
//        System.out.println("Total obiective extrase: " + objectives.size());
//        return objectives;
//    }

    private List<Object> extractObjectivesFromQuestions(List<Question> questions) {
        List<Object> objectives = new ArrayList<>();

        for (Question q : questions) {
            String qId = q.getQuestionId().getIdQuestion();
            System.out.println("Caut obiective pentru întrebarea: " + qId);

            var mainObjs = mainObjectivesRepository.findByIdQuestion(qId);

            if (mainObjs != null && !mainObjs.isEmpty()) {
                objectives.addAll(mainObjs);
                for (MainObjective mo : mainObjs) {
                    System.out.println("Obiectiv principal (DB): " + mo.getIdObjectiv() + " - " + mo.getNameObjectiv());
                }
            } else {
                // 2️⃣ Dacă nu există în DB, extragem din JSON-ul întrebării
                try {
                    String questionJson = q.getQuestion(); // presupunem că ai un câmp question cu JSON-ul complet
                    if (questionJson != null && questionJson.contains("\"mainObjectives\"")) {
                        // folosim Jackson pentru a extrage id-urile
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(questionJson);

                        if (root.has("mainObjectives")) {
                            for (com.fasterxml.jackson.databind.JsonNode objNode : root.get("mainObjectives")) {
                                int idObj = objNode.path("id").asInt();
                                String nameObj = objNode.path("name").asText();
                                MainObjective mo = new MainObjective();
                                mo.setIdObjectiv(idObj);
                                mo.setNameObjectiv(nameObj);
                                mo.setIdQuestion(qId);
                                objectives.add(mo);
                                System.out.println("Obiectiv principal (JSON): " + idObj + " - " + nameObj);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(" Eroare la parsarea JSON-ului pentru întrebarea " + qId + ": " + e.getMessage());
                }
            }
        }

        System.out.println("🔢 Total obiective extrase: " + objectives.size());
        return objectives;
    }


    // ca metoda de mai sus doar ca pentru obiectuve secundare
//    private List<Object> extractSecondaryObjectivesFromQuestion(List<Question> questions){
//        List<Object> subObjectives = new ArrayList<>();
//        for(Question q : questions){
//            var secondaryObjs = secondaryObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion());
//            subObjectives.addAll(secondaryObjectivesRepository.findByIdQuestion(q.getQuestionId().getIdQuestion()));
//
//            for (SecondaryObjective so : secondaryObjs) {
//                System.out.println("Obiectiv secundar: " + so.getIdObjectiv() + " - " + so.getNameObjectiv());
//            }
//        }
//        System.out.println("Total sub obiective extrase: " + subObjectives.size());
//        return subObjectives;
//    }
    public List<Object> extractSecondaryObjectivesFromQuestions(List<Question> questions) {
        List<Object> subObjectives = new ArrayList<>();

        for (Question q : questions) {
            String qId = q.getQuestionId().getIdQuestion();
            System.out.println("🔍 Caut sub-obiective pentru întrebarea: " + qId);

            // Încearcă să le ia din baza de date
            var secondaryObjs = secondaryObjectivesRepository.findByIdQuestion(qId);

            if (secondaryObjs != null && !secondaryObjs.isEmpty()) {
                subObjectives.addAll(secondaryObjs);
                for (SecondaryObjective so : secondaryObjs) {
                    System.out.println("Obiectiv secundar (DB): " + so.getIdObjectiv() + " - " + so.getNameObjectiv());
                }
            }
            //  Dacă nu există în DB, caută în JSON-ul întrebării
            else {
                try {
                    String questionJson = q.getQuestion();
                    if (questionJson != null && questionJson.contains("\"secondaryObjectives\"")) {
                        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        com.fasterxml.jackson.databind.JsonNode root = mapper.readTree(questionJson);

                        if (root.has("secondaryObjectives")) {
                            for (com.fasterxml.jackson.databind.JsonNode objNode : root.get("secondaryObjectives")) {
                                int idObj = objNode.path("id").asInt();
                                String nameObj = objNode.path("name").asText();
                                SecondaryObjective so = new SecondaryObjective();
                                so.setIdObjectiv(idObj);
                                so.setNameObjectiv(nameObj);
                                so.setIdQuestion(qId);
                                subObjectives.add(so);
                                System.out.println(" Obiectiv secundar (JSON): " + idObj + " - " + nameObj);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println(" Eroare la parsarea JSON-ului pentru întrebarea " + qId + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Total sub-obiective extrase: " + subObjectives.size());
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



    private Set<String> extractLabelsForQuestion(Question question) {
        Set<String> labels = new HashSet<>();

        try {
            var questionLabels = questionLabelRepository.findByIdQuestion(
                    question.getQuestionId().getIdQuestion()
            );
            for (QuestionLabels label : questionLabels) {
                labels.add(label.getLabel().toLowerCase());
            }
        } catch (Exception e) {
            System.err.println("⚠ Eroare la extragerea labelurilor pentru " +
                    question.getQuestionId().getIdQuestion() + ": " + e.getMessage());
        }

        return labels;
    }
    private String extractGuiInterface(Question question) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(question.getQuestion());
            JsonNode formatNode = node.get("format");
            if (formatNode != null) {
                return formatNode.asText();
            }
        } catch (Exception e) {
            System.err.println("️ Eroare la extragerea GUI interface-ului din întrebare: "
                    + question.getQuestionId().getIdQuestion() + " - " + e.getMessage());
        }
        return null;
    }


    public double distanceToQuestion(Question q1, Question q2) {
        double distance = 0.0;

        if (q1 == null || q2 == null) {
            return 0.0;
        }

        // 1Dacă au același ID → 1.0
        if (Objects.equals(q1.getQuestionId().getIdQuestion(), q2.getQuestionId().getIdQuestion())) {
            return 1.0;
        }

        // Extragem obiectivele principale, secundare și label-urile
        List<Object> mainObj1 = extractObjectivesFromQuestions(List.of(q1));
        List<Object> mainObj2 = extractObjectivesFromQuestions(List.of(q2));
        List<Object> secObj1 = extractSecondaryObjectivesFromQuestions(List.of(q1));
        List<Object> secObj2 = extractSecondaryObjectivesFromQuestions(List.of(q2));

        Set<String> mainIds1 = new HashSet<>();
        Set<String> mainIds2 = new HashSet<>();
        Set<String> secIds1 = new HashSet<>();
        Set<String> secIds2 = new HashSet<>();

        for (Object o : mainObj1)
            if (o instanceof MainObjective mo) mainIds1.add(String.valueOf(mo.getIdObjectiv()));
        for (Object o : mainObj2)
            if (o instanceof MainObjective mo) mainIds2.add(String.valueOf(mo.getIdObjectiv()));
        for (Object o : secObj1)
            if (o instanceof SecondaryObjective so) secIds1.add(String.valueOf(so.getIdObjectiv()));
        for (Object o : secObj2)
            if (o instanceof SecondaryObjective so) secIds2.add(String.valueOf(so.getIdObjectiv()));

        boolean sameMain = !Collections.disjoint(mainIds1, mainIds2);
        boolean sameSec = !Collections.disjoint(secIds1, secIds2);

        // același obiectiv, dar fără subobiective comune
        if (sameMain && !sameSec) {
            distance += 0.1;
        }

        // același obiectiv și subobiective comune, dar nivel diferit
        if (sameMain && sameSec && !Objects.equals(q1.getLevel(), q2.getLevel())) {
            Set<String> commonSec = new HashSet<>(secIds1);
            commonSec.retainAll(secIds2);
            distance += 0.1 * commonSec.size();
        }

        // Etichete (labels)
        Set<String> labels1 = extractLabelsForQuestion(q1);
        Set<String> labels2 = extractLabelsForQuestion(q2);
        Set<String> commonLabels = new HashSet<>(labels1);
        commonLabels.retainAll(labels2);

        //  Aceleași obiective, subobiective și labeluri + același nivel
        if (sameMain && sameSec && Objects.equals(q1.getLevel(), q2.getLevel())) {
            distance += 0.1 * commonLabels.size();
        }

        //  Același nivel de dificultate
        if (Objects.equals(q1.getLevel(), q2.getLevel())) {
            distance += 0.2;
        }

        //  Aceeași interfață grafică
        String gui1 = extractGuiInterface(q1);
        String gui2 = extractGuiInterface(q2);
        if (Objects.equals(gui1, gui2)) {
            distance += 0.3;
        }

        return Math.min(distance, 1.0);
    }

    //generate test / objectiv beginner and intermediate
    public List<Question> generateTestBeginnerAndIntermediate(String level, int obj) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);


        System.out.println("Obiectiv test===================================================" + obj);
        System.out.println("Level Test=====================================================" + level);
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



    public  List<Question> generateHardTest(int obj) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        List<List<Question>> softQ = selectToGenerateBeginnerTest("SOFTQUESTION", obj);
        List<List<Question>> midQ = selectToGenerateBeginnerTest("MIDLELEVELQUESTION", obj);
        List<List<Question>> hardQ = selectToGenerateBeginnerTest("HARDQUESTION", obj);

        List<Question> allSoft = new ArrayList<>();
        List<Question> allMid = new ArrayList<>();
        List<Question> allHard = new ArrayList<>();

        softQ.forEach(allSoft::addAll);
        midQ.forEach(allMid::addAll);
        hardQ.forEach(allHard::addAll);


        int pSoft = random.nextInt(10, 30);
        int pMid  = random.nextInt(10, 30);
        int pHard = 100 - pSoft - pMid;


        if (pHard < 50) {
            pHard = 50;
            int rest = 50;
            pSoft = random.nextInt(0, rest + 1);
            pMid = rest - pSoft;
        }

        int nrSoft = (pSoft * numberQuestions) / 100;
        int nrMid  = (pMid * numberQuestions) / 100;
        int nrHard = numberQuestions - nrSoft - nrMid;

        System.out.println("========= Distribuție întrebări =========");
        System.out.println("SOFT: " + nrSoft + " (" + pSoft + "%)");
        System.out.println("MID : " + nrMid  + " (" + pMid  + "%)");
        System.out.println("HARD: " + nrHard + " (" + pHard + "%)");
        System.out.println("=========================================");



        if(nrSoft > allSoft.size() || nrMid > allMid.size()){
            System.err.println("Insuficiente intrebari");
        }

        if (nrHard > allHard.size()) {
            int deficit = nrHard - allHard.size();
            nrHard = allHard.size();

            while (deficit > 0) {
                if (nrSoft < allSoft.size()) {
                    nrSoft++;
                } else if (nrMid < allMid.size()) {
                    nrMid++;
                }
                deficit--;
            }
        }

        int totalTime = nrSoft * 60 + nrMid * 100 + nrHard * 150;
        int epsilon = random.nextInt(0, 6); // între 0 și 5
        int maxTime = 150 * 60 - epsilon;

        if (totalTime > maxTime) {
            System.out.println("Timp depășit (" + totalTime + "s). Regenerăm...");
            return generateHardTest(obj); // recursiv, regenerăm testul
        }

        List<Question> test = new ArrayList<>();
        Collections.shuffle(allSoft);
        Collections.shuffle(allMid);
        Collections.shuffle(allHard);

        test.addAll(allSoft.subList(0, Math.min(nrSoft, allSoft.size())));
        test.addAll(allMid.subList(0, Math.min(nrMid, allMid.size())));
        test.addAll(allHard.subList(0, Math.min(nrHard, allHard.size())));

        Collections.shuffle(test);
        return test;
    }

    //extract questions for beginner and intermediate test / more objectives
    private List<List<Question>> selectToGenerateBeginnerIntermediateTestMoreObj(String level, int[] idObjectiv) {
        List<List<Question>> questionsSubObjectives = new ArrayList<>();
        try {
            String xmlPath = new ClassPathResource("objectives.xml").getPath();
            for (int objId : idObjectiv) {
                List<Integer> subObjIds = getSubObjectivesById(objId, xmlPath);
                for (Integer subObjId : subObjIds) {
                    List<Question> questions = questionRepository.findByLevelAndObjectives(level, objId, subObjId);
                    questionsSubObjectives.add(questions);
                    System.out.println("Găsite " + questions.size() +
                            " întrebări pentru obiectivul " + objId +
                            ", subobiectivul " + subObjId);
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la generarea testului: " + e.getMessage());
            e.printStackTrace();
        }
        return questionsSubObjectives;
    }

    // generate test for beginner and intermediate using more main objectives
    public  List<Question> generateTestBeginnerAndIntermediateMoreObj(String level, int[] obj) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        System.out.println("Level Test=====================================================" + level);
        List<List<Question>> extractperSubObjective = selectToGenerateBeginnerIntermediateTestMoreObj(level, obj);

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
                test.add(subList.get(random.nextInt(subList.size())));
                questRemaining--;
            }
            idx++;
        }
        Collections.shuffle(test);
        return test;
    }

    //generate hard test for more objectives
    public List<Question> generateHardTestMoreObj(int[] obj) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        List<List<Question>> softQ = selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj);
        List<List<Question>> midQ = selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj);
        List<List<Question>> hardQ = selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj);

        List<Question> allSoft = new ArrayList<>();
        List<Question> allMid = new ArrayList<>();
        List<Question> allHard = new ArrayList<>();

        softQ.forEach(allSoft::addAll);
        midQ.forEach(allMid::addAll);
        hardQ.forEach(allHard::addAll);


        int pSoft = random.nextInt(10, 30);
        int pMid  = random.nextInt(10, 30);
        int pHard = 100 - pSoft - pMid;


        if (pHard < 50) {
            pHard = 50;
            int rest = 50;
            pSoft = random.nextInt(0, rest + 1);
            pMid = rest - pSoft;
        }

        int nrSoft = (pSoft * numberQuestions) / 100;
        int nrMid  = (pMid * numberQuestions) / 100;
        int nrHard = numberQuestions - nrSoft - nrMid;


        if(nrSoft > allSoft.size() || nrMid > allMid.size()){
            System.err.println("Insuficiente intrebari");
        }

        if (nrHard > allHard.size()) {
            int deficit = nrHard - allHard.size();
            nrHard = allHard.size();

            while (deficit > 0) {
                if (nrSoft < allSoft.size()) {
                    nrSoft++;
                } else if (nrMid < allMid.size()) {
                    nrMid++;
                }
                deficit--;
            }
        }

        int totalTime = nrSoft * 60 + nrMid * 100 + nrHard * 150;
        int epsilon = random.nextInt(0, 6); // între 0 și 5
        int maxTime = 150 * 60 - epsilon;

        if (totalTime > maxTime) {
            System.out.println("Timp depășit (" + totalTime + "s). Regenerăm...");
            return generateHardTestMoreObj(obj);
        }

        List<Question> test = new ArrayList<>();
        Collections.shuffle(allSoft);
        Collections.shuffle(allMid);
        Collections.shuffle(allHard);

        test.addAll(allSoft.subList(0, Math.min(nrSoft, allSoft.size())));
        test.addAll(allMid.subList(0, Math.min(nrMid, allMid.size())));
        test.addAll(allHard.subList(0, Math.min(nrHard, allHard.size())));

        Collections.shuffle(test);
        return test;
    }

    public int[] extractAllObjectivesId(){
        try {
            File xmlFile = new ClassPathResource("objectives.xml").getFile();
            List<Objectiv> listObj = Objectiv.readObjectiv(xmlFile.getAbsolutePath());
            int[] idObjectives = new int[listObj.size()];
            for(int i = 0; i < listObj.size(); i++){
                idObjectives[i] = Integer.parseInt(listObj.get(i).getId());
            }
            return idObjectives;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //generate the final test using the total objectives - level beginner and intermediate
    public List<Question> generateTestBeginnerAndIntermediateAllObjectives(String level) {
        int[] obj = extractAllObjectivesId();
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);


        List<List<Question>> extractperSubObjective = selectToGenerateBeginnerIntermediateTestMoreObj(level, obj);

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
                test.add(subList.get(random.nextInt(subList.size())));
                questRemaining--;
            }
            idx++;
        }

        Collections.shuffle(test);

        return test;
    }

    //generate the final test of the hard level using tha total objectives
    public List<Question> generateHardTestAllObj() {
        int[] obj = extractAllObjectivesId();
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        List<List<Question>> softQ = selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj);
        List<List<Question>> midQ = selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj);
        List<List<Question>> hardQ = selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj);

        List<Question> allSoft = new ArrayList<>();
        List<Question> allMid = new ArrayList<>();
        List<Question> allHard = new ArrayList<>();

        softQ.forEach(allSoft::addAll);
        midQ.forEach(allMid::addAll);
        hardQ.forEach(allHard::addAll);


        int pSoft = random.nextInt(10, 30);
        int pMid  = random.nextInt(10, 30);
        int pHard = 100 - pSoft - pMid;


        if (pHard < 50) {
            pHard = 50;
            int rest = 50;
            pSoft = random.nextInt(0, rest + 1);
            pMid = rest - pSoft;
        }

        int nrSoft = (pSoft * numberQuestions) / 100;
        int nrMid  = (pMid * numberQuestions) / 100;
        int nrHard = numberQuestions - nrSoft - nrMid;


        if(nrSoft > allSoft.size() || nrMid > allMid.size()){
            System.err.println("Insuficiente intrebari");
        }

        if (nrHard > allHard.size()) {
            int deficit = nrHard - allHard.size();
            nrHard = allHard.size();

            while (deficit > 0) {
                if (nrSoft < allSoft.size()) {
                    nrSoft++;
                } else if (nrMid < allMid.size()) {
                    nrMid++;
                }
                deficit--;
            }
        }

        int totalTime = nrSoft * 60 + nrMid * 100 + nrHard * 150;
        int epsilon = random.nextInt(0, 6); // între 0 și 5
        int maxTime = 150 * 60 - epsilon;

        if (totalTime > maxTime) {
            System.out.println("⚠️ Timp depășit (" + totalTime + "s). Regenerăm...");
            return generateHardTestMoreObj(obj);
        }

        List<Question> test = new ArrayList<>();
        Collections.shuffle(allSoft);
        Collections.shuffle(allMid);
        Collections.shuffle(allHard);

        test.addAll(allSoft.subList(0, Math.min(nrSoft, allSoft.size())));
        test.addAll(allMid.subList(0, Math.min(nrMid, allMid.size())));
        test.addAll(allHard.subList(0, Math.min(nrHard, allHard.size())));

        Collections.shuffle(test);
        return test;
    }

    //write the test with all objectives
    public TestOfQuestions writingFinalTest(String userId, String level){
        List<Question> questions = new ArrayList<>();
        if(level.equals("SOFTQUESTION")) {
            questions = generateTestBeginnerAndIntermediateAllObjectives("SOFTQUESTION");
        } else if(level.equals("MIDLELEVELQUESTION")){
            questions = generateTestBeginnerAndIntermediateAllObjectives("MIDLELEVELQUESTION");
        } else if(level.equals(LevelTest.HARD.getDenumire())){
            questions = generateHardTestAllObj();
        }
        String idTest = generateId(userId, questions);
        int nrOfQuestions = questions.size();
        int duration = extractdurationOfTest(questions);
        UserTest userTest = new UserTest();
        userTest.setIdTest(idTest);
        userTest.setIdUser(userId);
        userTestRepository.save(userTest);

        Test test = new Test();
        test.setIdTest(idTest);
        test.setNrOfObjectives(extractAllObjectivesId().length);
        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION")) {
            test.setLevel(LevelTest.BEGINNER);
        }
        else if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION")) {
            test.setLevel(LevelTest.INTERMEDIATE);
        }
        else if (level.equalsIgnoreCase("HARD") || level.equalsIgnoreCase("HARDQUESTION")) {
            test.setLevel(LevelTest.HARD);
        }
        test.setNrOfQuestions(nrOfQuestions);
        test.setEstimatedDuration(duration);
        testRepository.save(test);

        for (Question q : questions) {
            QuestionTest link = new QuestionTest();
            link.setIdTest(idTest);
            link.setIdQuestion(q.getQuestionId().getIdQuestion());
            questionTestRepository.save(link);
        }

        TestOfQuestions testOfQuestions = new TestOfQuestions(test, questions);
        return testOfQuestions;
    }

   // write the test of the single and more objectives
//    public Test writeTestSingleObjective(String userId, String level, int[] obj){
//        List<Question> questions = new ArrayList<>();
//        System.err.println(obj.length);
//        if(obj.length == 1) {
//            if (level.equals(LevelTest.BEGINNER.getDenumire()) || level.equals(LevelTest.INTERMEDIATE.getDenumire())) {
//                questions = generateTestBeginnerAndIntermediate(level, obj[0]);
//            } else if (level.equals(LevelTest.HARD.getDenumire())) {
//                questions = generateHardTest(obj[0]);
//            }
//        }else if(obj.length > 1){
//            if(level.equals(LevelTest.BEGINNER.getDenumire()) || level.equals(LevelTest.INTERMEDIATE.getDenumire())){
//                questions = generateTestBeginnerAndIntermediateMoreObj(level, obj);
//            } else if(level.equals(LevelTest.HARD.getDenumire())){
//                questions = generateHardTestMoreObj(obj);
//            }
//        }
//        System.err.println(questions.size());
//
//        String idTest = generateId(userId, questions);
//        int nrOfQuestions = questions.size();
//        int duration = extractdurationOfTest(questions);
//
//        UserTest userTest = new UserTest();
//        userTest.setIdTest(idTest);
//        userTest.setIdUser(userId);
//        userTestRepository.save(userTest);
//
//        Test test = new Test();
//        test.setIdTest(idTest);
//        test.setNrOfObjectives(obj.length);
//        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION")) {
//            test.setLevel(LevelTest.BEGINNER);
//        }
//        else if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION")) {
//            test.setLevel(LevelTest.INTERMEDIATE);
//        }
//        else if (level.equalsIgnoreCase("HARD") || level.equalsIgnoreCase("HARDQUESTION")) {
//            test.setLevel(LevelTest.HARD);
//        }
//        test.setNrOfQuestions(nrOfQuestions);
//        test.setEstimatedDuration(duration);
//        testRepository.save(test);
//
//        for (Question q : questions) {
//            QuestionTest link = new QuestionTest();
//            link.setIdTest(idTest);
//            link.setIdQuestion(q.getQuestionId().getIdQuestion());
//            questionTestRepository.save(link);
//        }
//        return test;
//    }

    public TestOfQuestions writeTestSingleObjective(String userId, String level, int[] obj){
        List<Question> questions = new ArrayList<>();
        System.err.println(obj.length);
        if(obj.length == 1) {
            if (level.equals("SOFTQUESTION")) {
                questions = generateTestBeginnerAndIntermediate("SOFTQUESTION", obj[0]);
            } else if(level.equals("MIDLELEVELQUESTION")) {
                questions = generateTestBeginnerAndIntermediate("MIDLELEVELQUESTION", obj[0]);
            } else if (level.equals(LevelTest.HARD.getDenumire())) {
                questions = generateHardTest(obj[0]);
            }
        }else if(obj.length > 1){
            if (level.equals("SOFTQUESTION")) {
                questions = generateTestBeginnerAndIntermediateMoreObj("SOFTQUESTION", obj);
            } else if(level.equals("MIDLELEVELQUESTION")){
                questions = generateTestBeginnerAndIntermediateMoreObj("MIDLELEVELQUESTION", obj);
            } else if(level.equals(LevelTest.HARD.getDenumire())){
                questions = generateHardTestMoreObj(obj);
            }
        }
        System.err.println(questions.size());

        String idTest = generateId(userId, questions);
        int nrOfQuestions = questions.size();
        int duration = extractdurationOfTest(questions);

        UserTest userTest = new UserTest();
        userTest.setIdTest(idTest);
        userTest.setIdUser(userId);
        userTestRepository.save(userTest);

        Test test = new Test();
        test.setIdTest(idTest);
        test.setNrOfObjectives(obj.length);
        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION")) {
            test.setLevel(LevelTest.BEGINNER);
        }
        else if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION")) {
            test.setLevel(LevelTest.INTERMEDIATE);
        }
        else if (level.equalsIgnoreCase("HARD") || level.equalsIgnoreCase("HARDQUESTION")) {
            test.setLevel(LevelTest.HARD);
        }
        test.setNrOfQuestions(nrOfQuestions);
        test.setEstimatedDuration(duration);
        testRepository.save(test);


        for (Question q : questions) {
            QuestionTest link = new QuestionTest();
            link.setIdTest(idTest);
            link.setIdQuestion(q.getQuestionId().getIdQuestion());
            questionTestRepository.save(link);
        }

        TestOfQuestions testOfQuestions = new TestOfQuestions(test, questions);
        return testOfQuestions;
    }

    //extract question witch correspond the subobjectives
//    public List<List<Question>> serachQuestionBySecondaryObjectives(int[] obj, String level) {
//        List<List<Question>> questionBySecObj = new ArrayList<>();
//        for (int i = 0; i < obj.length; i++) {
//            System.err.println(obj[i]);
//            List<Question> quest = secondaryObjectivesRepository.findQuestionsBySecondaryObjectiveAndLevel(obj[i], level);
//            questionBySecObj.add(quest);
//        }
//        for(List<Question> q1 : questionBySecObj){
//            for(Question q2 : q1){
//                System.err.println(q2);
//            }
//        }
//        return questionBySecObj;
//    }

    public List<List<Question>> searchQuestionsBySecondaryObjectivesAndLevel(int[] obj, String level) {
        List<List<Question>> questionBySecObj = new ArrayList<>();

        for (int i = 0; i < obj.length; i++) {
            System.err.println("Id Objective: " + obj[i]);
            List<Question> quest = secondaryObjectivesRepository.findQuestionsBySecondaryObjectiveAndLevel(obj[i], level);
            questionBySecObj.add(quest);
        }

//        for (List<Question> q1 : questionBySecObj) {
//            for (Question q2 : q1) {
//                System.err.println(q2);
//            }
//        }

        return questionBySecObj;
    }


    //generate beginner and intermediate test - more subobjectives
    public  List<Question> generateTestBeginnerAndIntermediateMoreSubobjectives(String level, int[] subObjectives) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        List<List<Question>> extractperSubObjective = searchQuestionsBySecondaryObjectivesAndLevel(subObjectives, level);

        int sizeList = extractperSubObjective.size();
        int questRemaining = numberQuestions % sizeList;
        int numberExtract = numberQuestions / sizeList;

        System.out.println("Nr sub-obiective: " + sizeList);
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
                test.add(subList.get(random.nextInt(subList.size())));
                questRemaining--;
            }
            idx++;
        }
        Collections.shuffle(test);
        return test;
    }

    //generate hard test - more subobjectives
    public List<Question> generateHardTestMoreSubobjectives(int[] subobjectives) {
        Random random = new Random();
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr total intrebari: " + numberQuestions);

        List<List<Question>> softQ = searchQuestionsBySecondaryObjectivesAndLevel(subobjectives, "SOFTQUESTION");
        List<List<Question>> midQ = searchQuestionsBySecondaryObjectivesAndLevel(subobjectives, "MIDLELEVELQUESTION");
        List<List<Question>> hardQ = searchQuestionsBySecondaryObjectivesAndLevel(subobjectives, "HARDQUESTION");

        List<Question> allSoft = new ArrayList<>();
        List<Question> allMid = new ArrayList<>();
        List<Question> allHard = new ArrayList<>();

        softQ.forEach(allSoft::addAll);
        midQ.forEach(allMid::addAll);
        hardQ.forEach(allHard::addAll);


        int pSoft = random.nextInt(10, 30);
        int pMid  = random.nextInt(10, 30);
        int pHard = 100 - pSoft - pMid;


        if (pHard < 50) {
            pHard = 50;
            int rest = 50;
            pSoft = random.nextInt(0, rest + 1);
            pMid = rest - pSoft;
        }

        int nrSoft = (pSoft * numberQuestions) / 100;
        int nrMid  = (pMid * numberQuestions) / 100;
        int nrHard = numberQuestions - nrSoft - nrMid;


        if(nrSoft > allSoft.size() || nrMid > allMid.size()){
            System.err.println("Insuficiente intrebari");
        }

        if (nrHard > allHard.size()) {
            int deficit = nrHard - allHard.size();
            nrHard = allHard.size();

            while (deficit > 0) {
                if (nrSoft < allSoft.size()) {
                    nrSoft++;
                } else if (nrMid < allMid.size()) {
                    nrMid++;
                }
                deficit--;
            }
        }

        int totalTime = nrSoft * 60 + nrMid * 100 + nrHard * 150;
        int epsilon = random.nextInt(0, 6); // între 0 și 5
        int maxTime = 150 * 60 - epsilon;

        if (totalTime > maxTime) {
            System.out.println("Timp depășit (" + totalTime + "s). Regenerăm...");
            return generateHardTestMoreSubobjectives(subobjectives);
        }

        List<Question> test = new ArrayList<>();
        Collections.shuffle(allSoft);
        Collections.shuffle(allMid);
        Collections.shuffle(allHard);

        test.addAll(allSoft.subList(0, Math.min(nrSoft, allSoft.size())));
        test.addAll(allMid.subList(0, Math.min(nrMid, allMid.size())));
        test.addAll(allHard.subList(0, Math.min(nrHard, allHard.size())));

        Collections.shuffle(test);
        return test;
    }

    //write test with subobjectives
    public TestOfQuestions writeTestSingleAndMoreSubobjectives(String userId, String level, int[] subobjectives){
        List<Question> questions = new ArrayList<>();
        System.err.println("Subobj" + subobjectives.length);
        if (level.equals("SOFTQUESTION")) {
            System.err.println("==========================================================");
            questions = generateTestBeginnerAndIntermediateMoreSubobjectives("SOFTQUESTION", subobjectives);
        } else if(level.equals("MIDLELEVELQUESTION")) {
            questions = generateTestBeginnerAndIntermediateMoreSubobjectives("MIDLELEVELQUESTION", subobjectives);
        } else if (level.equals(LevelTest.HARD.getDenumire())) {
            questions = generateHardTestMoreSubobjectives(subobjectives);
        }
        System.err.println("Question size: " + questions.size());

        String idTest = generateId(userId, questions);
        int nrOfQuestions = questions.size();
        int duration = extractdurationOfTest(questions);

        UserTest userTest = new UserTest();
        userTest.setIdTest(idTest);
        userTest.setIdUser(userId);
        userTestRepository.save(userTest);

        Test test = new Test();
        test.setIdTest(idTest);
        test.setNrOfObjectives(subobjectives.length);
        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION")) {
            test.setLevel(LevelTest.BEGINNER);
        }
        else if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION")) {
            test.setLevel(LevelTest.INTERMEDIATE);
        }
        else if (level.equalsIgnoreCase("HARD") || level.equalsIgnoreCase("HARDQUESTION")) {
            test.setLevel(LevelTest.HARD);
        }
        test.setNrOfQuestions(nrOfQuestions);
        test.setEstimatedDuration(duration);
        testRepository.save(test);


        for (Question q : questions) {
            QuestionTest link = new QuestionTest();
            link.setIdTest(idTest);
            link.setIdQuestion(q.getQuestionId().getIdQuestion());
            questionTestRepository.save(link);
        }

        TestOfQuestions testOfQuestions = new TestOfQuestions(test, questions);
        return testOfQuestions;
    }
}

