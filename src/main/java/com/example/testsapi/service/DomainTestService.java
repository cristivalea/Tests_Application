//package com.example.testsapi.service;
//
//import com.example.testsapi.entity.*;
//import com.example.testsapi.repository.*;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.stereotype.Service;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
//import java.io.InputStream;
//import java.util.*;
//import java.util.function.Supplier;
//
//@Service
//public class DomainTestService {
//
//    @Autowired
//    private TestRepository testRepository;
//    @Autowired
//    private QuestionTestRepository questionTestRepository;
//    @Autowired
//    private MainObjectiveRepository mainObjectivesRepository;
//    @Autowired
//    private SecondaryObjectiveRepository secondaryObjectivesRepository;
//    @Autowired
//    private QuestionRepository questionRepository;
//    @Autowired
//    private UserTestRepository userTestRepository;
//    @Autowired
//    private QuestionLabelsRepository questionLabelRepository;
//
//    // ============================================================
//    //  ID & OBIECTIVE (helpere existente)
//    // ============================================================
//
//    // metoda aceasta este utilizata in controller, aceasta apeland generarea idului testului
//    public String generateIdForUser(String userId, List<Question> questions) {
//        return generateId(userId, questions);
//    }
//
//    private String generateId(String userId, List<Question> questions) {
//        String idTest = "";
//
//        Random random = new Random();
//        int number = random.nextInt(900) + 100;
//
//        System.out.println("==================== Generare ID pentru test ====================");
//        List<Object> objectivesP = extractObjectivesFromQuestions(questions);
//        Set<Object> setObj = new TreeSet<>(objectivesP);
//        List<Object> objectives = new ArrayList<>(setObj);
//
//        if (objectives.isEmpty()) {
//            System.out.println("Nu s-au gasit obiective pentru intrebarile din test!");
//            idTest = userId + "_NOOBJ_" + number;
//        } else if (objectives.size() == 1) {
//            Object obj = objectives.get(0);
//            if (obj instanceof MainObjective mo) {
//                idTest = userId + "_OB" + mo.getIdObjectiv() + "_" + number;
//            } else if (obj instanceof SecondaryObjective so) {
//                idTest = userId + "_OB" + so.getIdObjectiv() + "_" + number;
//            }
//        } else {
//            StringBuilder sb = new StringBuilder(userId);
//            for (Object obj : objectives) {
//                if (obj instanceof MainObjective mo)
//                    sb.append("_").append(mo.getIdObjectiv());
//                else if (obj instanceof SecondaryObjective so)
//                    sb.append("_").append(so.getIdObjectiv());
//            }
//            sb.append("_").append(number);
//            idTest = sb.toString();
//        }
//
//        System.out.println("ID test generat: " + idTest);
//        return idTest;
//    }
//
//    private List<Object> extractObjectivesFromQuestions(List<Question> questions) {
//        List<Object> objectives = new ArrayList<>();
//
//        for (Question q : questions) {
//            String qId = q.getQuestionId().getIdQuestion();
//            System.out.println("Caut obiective pentru intrebarea: " + qId);
//
//            var mainObjs = mainObjectivesRepository.findByIdQuestion(qId);
//
//            if (mainObjs != null && !mainObjs.isEmpty()) {
//                objectives.addAll(mainObjs);
//                for (MainObjective mo : mainObjs) {
//                    System.out.println("Obiectiv principal (DB): " + mo.getIdObjectiv() + " - " + mo.getNameObjectiv());
//                }
//            } else {
//                // Daca nu exista in DB, extragem din JSON-ul intrebarii
//                try {
//                    String questionJson = q.getQuestion();
//                    if (questionJson != null && questionJson.contains("\"mainObjectives\"")) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        JsonNode root = mapper.readTree(questionJson);
//
//                        if (root.has("mainObjectives")) {
//                            for (JsonNode objNode : root.get("mainObjectives")) {
//                                int idObj = objNode.path("id").asInt();
//                                String nameObj = objNode.path("name").asText();
//                                MainObjective mo = new MainObjective();
//                                mo.setIdObjectiv(idObj);
//                                mo.setNameObjectiv(nameObj);
//                                mo.setIdQuestion(qId);
//                                objectives.add(mo);
//                                System.out.println("Obiectiv principal (JSON): " + idObj + " - " + nameObj);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    System.err.println("Eroare la parsarea JSON-ului pentru intrebarea " + qId + ": " + e.getMessage());
//                }
//            }
//        }
//
//        System.out.println("Total obiective extrase: " + objectives.size());
//        return objectives;
//    }
//
//    public List<Object> extractSecondaryObjectivesFromQuestions(List<Question> questions) {
//        List<Object> subObjectives = new ArrayList<>();
//
//        for (Question q : questions) {
//            String qId = q.getQuestionId().getIdQuestion();
//            System.out.println("Caut sub-obiective pentru intrebarea: " + qId);
//
//            var secondaryObjs = secondaryObjectivesRepository.findByIdQuestion(qId);
//
//            if (secondaryObjs != null && !secondaryObjs.isEmpty()) {
//                subObjectives.addAll(secondaryObjs);
//                for (SecondaryObjective so : secondaryObjs) {
//                    System.out.println("Obiectiv secundar (DB): " + so.getIdObjectiv() + " - " + so.getNameObjectiv());
//                }
//            } else {
//                try {
//                    String questionJson = q.getQuestion();
//                    if (questionJson != null && questionJson.contains("\"secondaryObjectives\"")) {
//                        ObjectMapper mapper = new ObjectMapper();
//                        JsonNode root = mapper.readTree(questionJson);
//
//                        if (root.has("secondaryObjectives")) {
//                            for (JsonNode objNode : root.get("secondaryObjectives")) {
//                                int idObj = objNode.path("id").asInt();
//                                String nameObj = objNode.path("name").asText();
//                                SecondaryObjective so = new SecondaryObjective();
//                                so.setIdObjectiv(idObj);
//                                so.setNameObjectiv(nameObj);
//                                so.setIdQuestion(qId);
//                                subObjectives.add(so);
//                                System.out.println("Obiectiv secundar (JSON): " + idObj + " - " + nameObj);
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                    System.err.println("Eroare la parsarea JSON-ului pentru intrebarea " + qId + ": " + e.getMessage());
//                }
//            }
//        }
//
//        System.out.println("Total sub-obiective extrase: " + subObjectives.size());
//        return subObjectives;
//    }
//
//    // extrage idul subobiectivelor
//    private List<String> determinateObjectives(List<Objectiv> subObj) {
//        List<String> subobsId = new ArrayList<>();
//        for (Objectiv oj : subObj) {
//            subobsId.add(oj.getId());
//        }
//        return subobsId;
//    }
//
//    // extrage obiectivele principale corespunzatoare obiectivelor secundare
//    public List<Objectiv> addPrinciplesObjectivesInList(List<Objectiv> secObj) {
//        List<Objectiv> objectives = new ArrayList<>();
//        Set<Objectiv> uniqObj = new TreeSet<>();
//        List<String> idSubobj = determinateObjectives(secObj);
//
//        try {
//            File xmlFile = new ClassPathResource("obiective_9_mi.xml").getFile();
//            List<Objectiv> mainObj = Objectiv.readObjectiv(xmlFile.getAbsolutePath());
//
//            for (Objectiv mo : mainObj) {
//                for (String str : idSubobj) {
//                    if (mo.getId().charAt(0) == str.charAt(0)) {
//                        uniqObj.add(mo);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//
//        objectives.addAll(uniqObj);
//        return objectives;
//    }
//
//    private int extractdurationOfTest(List<Question> questions) {
//        int duration = 0;
//        for (Question q : questions) {
//            if (q.getLevel().equals("SOFTQUESTION".trim())) {
//                duration += 60;
//            } else if (q.getLevel().equals("MIDLELEVELQUESTION".trim())) {
//                duration += 100;
//            } else if (q.getLevel().equals("HARDQUESTION".trim())) {
//                duration += 150;
//            }
//        }
//        return duration;
//    }
//
//    public static List<Integer> getSubObjectivesById(int idObjectiv, String xmlFileName) {
//        List<Integer> subIds = new ArrayList<>();
//        try {
//            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
//
//            InputStream xmlInput = new ClassPathResource(xmlFileName).getInputStream();
//            Document document = documentBuilder.parse(xmlInput);
//
//            NodeList objectives = document.getElementsByTagName("obiectiv");
//            for (int i = 0; i < objectives.getLength(); i++) {
//                Element objectiv = (Element) objectives.item(i);
//                if (String.valueOf(idObjectiv).equals(objectiv.getAttribute("id"))) {
//                    NodeList subObjectives = objectiv.getElementsByTagName("obiectiv");
//                    for (int j = 0; j < subObjectives.getLength(); j++) {
//                        Element subvObj = (Element) subObjectives.item(j);
//                        subIds.add(Integer.parseInt(subvObj.getAttribute("id")));
//                    }
//                    break;
//                }
//            }
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//        }
//        return subIds;
//    }
//
//    public int[] extractAllObjectivesId() {
//        try {
//            File xmlFile = new ClassPathResource("obiective_9_mi.xml").getFile();
//            List<Objectiv> listObj = Objectiv.readObjectiv(xmlFile.getAbsolutePath());
//            int[] idObjectives = new int[listObj.size()];
//            for (int i = 0; i < listObj.size(); i++) {
//                idObjectives[i] = Integer.parseInt(listObj.get(i).getId());
//            }
//            return idObjectives;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    // ============================================================
//    //  LABELS / GUI / DISTANTA (helpere existente)
//    // ============================================================
//
//    private Set<String> extractLabelsForQuestion(Question question) {
//        Set<String> labels = new HashSet<>();
//        try {
//            var questionLabels = questionLabelRepository.findByIdQuestion(
//                    question.getQuestionId().getIdQuestion()
//            );
//            for (QuestionLabels label : questionLabels) {
//                labels.add(label.getLabel().toLowerCase());
//            }
//        } catch (Exception e) {
//            System.err.println("Eroare la extragerea labelurilor pentru " +
//                    question.getQuestionId().getIdQuestion() + ": " + e.getMessage());
//        }
//        return labels;
//    }
//
//    private String extractGuiInterface(Question question) {
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode node = mapper.readTree(question.getQuestion());
//            JsonNode formatNode = node.get("format");
//            if (formatNode != null) {
//                return formatNode.asText();
//            }
//        } catch (Exception e) {
//            System.err.println("Eroare la extragerea GUI interface-ului din intrebare: "
//                    + question.getQuestionId().getIdQuestion() + " - " + e.getMessage());
//        }
//        return null;
//    }
//
//    public double distanceToQuestion(Question q1, Question q2) {
//        double distance = 0.0;
//
//        if (q1 == null || q2 == null) {
//            return 0.0;
//        }
//
//        if (Objects.equals(q1.getQuestionId().getIdQuestion(), q2.getQuestionId().getIdQuestion())) {
//            return 1.0;
//        }
//
//        List<Object> mainObj1 = extractObjectivesFromQuestions(List.of(q1));
//        List<Object> mainObj2 = extractObjectivesFromQuestions(List.of(q2));
//        List<Object> secObj1 = extractSecondaryObjectivesFromQuestions(List.of(q1));
//        List<Object> secObj2 = extractSecondaryObjectivesFromQuestions(List.of(q2));
//
//        Set<String> mainIds1 = new HashSet<>();
//        Set<String> mainIds2 = new HashSet<>();
//        Set<String> secIds1 = new HashSet<>();
//        Set<String> secIds2 = new HashSet<>();
//
//        for (Object o : mainObj1)
//            if (o instanceof MainObjective mo) mainIds1.add(String.valueOf(mo.getIdObjectiv()));
//        for (Object o : mainObj2)
//            if (o instanceof MainObjective mo) mainIds2.add(String.valueOf(mo.getIdObjectiv()));
//        for (Object o : secObj1)
//            if (o instanceof SecondaryObjective so) secIds1.add(String.valueOf(so.getIdObjectiv()));
//        for (Object o : secObj2)
//            if (o instanceof SecondaryObjective so) secIds2.add(String.valueOf(so.getIdObjectiv()));
//
//        boolean sameMain = !Collections.disjoint(mainIds1, mainIds2);
//        boolean sameSec = !Collections.disjoint(secIds1, secIds2);
//
//        if (sameMain && !sameSec) {
//            distance += 0.1;
//        }
//
//        if (sameMain && sameSec && !Objects.equals(q1.getLevel(), q2.getLevel())) {
//            Set<String> commonSec = new HashSet<>(secIds1);
//            commonSec.retainAll(secIds2);
//            distance += 0.1 * commonSec.size();
//        }
//
//        Set<String> labels1 = extractLabelsForQuestion(q1);
//        Set<String> labels2 = extractLabelsForQuestion(q2);
//        Set<String> commonLabels = new HashSet<>(labels1);
//        commonLabels.retainAll(labels2);
//
//        if (sameMain && sameSec && Objects.equals(q1.getLevel(), q2.getLevel())) {
//            distance += 0.1 * commonLabels.size();
//        }
//
//        if (Objects.equals(q1.getLevel(), q2.getLevel())) {
//            distance += 0.2;
//        }
//
//        String gui1 = extractGuiInterface(q1);
//        String gui2 = extractGuiInterface(q2);
//        if (Objects.equals(gui1, gui2)) {
//            distance += 0.3;
//        }
//
//        return Math.min(distance, 1.0);
//    }
//
//    // ============================================================
//    //  HELPERE COMUNE (domain-aware + distributie + mix hard + persist)
//    // ============================================================
//
//    /** null / array gol -> lista goala = fara filtru de domeniu (comportament identic cu cel vechi). */
//    private List<String> toList(String[] domains) {
//        return (domains == null) ? List.of() : Arrays.asList(domains);
//    }
//
//    private List<Question> flatten(List<List<Question>> grouped) {
//        List<Question> all = new ArrayList<>();
//        grouped.forEach(all::addAll);
//        return all;
//    }
//
//    /** Distribuie echilibrat ~70-80 intrebari pe grupuri (sub-obiective / obiective). */
//    private List<Question> distributeQuestions(List<List<Question>> grouped) {
//        Random random = new Random();
//        int numberQuestions = random.nextInt(70, 81);
//        System.out.println("Nr total intrebari: " + numberQuestions);
//
//        int size = grouped.size();
//        if (size == 0)
//            throw new RuntimeException("Nu exista intrebari pentru criteriile selectate");
//
//        int perGroup = numberQuestions / size;
//        int remaining = numberQuestions % size;
//
//        System.out.println("Nr grupuri: " + size);
//        System.out.println("Intrebari per grup: " + perGroup);
//        System.out.println("Rest de distribuit: " + remaining);
//
//        List<Question> test = new ArrayList<>();
//        for (List<Question> subList : grouped) {
//            Collections.shuffle(subList);
//            for (int i = 0; i < Math.min(perGroup, subList.size()); i++) {
//                test.add(subList.get(i));
//            }
//        }
//
//        int idx = 0;
//        while (remaining > 0) {
//            List<Question> subList = grouped.get(idx % size);
//            if (!subList.isEmpty()) {
//                test.add(subList.get(random.nextInt(subList.size())));
//                remaining--;
//            }
//            idx++;
//        }
//
//        Collections.shuffle(test);
//        return test;
//    }
//
//    /** Mix soft/mid/hard cu buget de timp; daca se depaseste, retry recursiv. */
//    private List<Question> buildHardTest(List<Question> allSoft, List<Question> allMid,
//                                         List<Question> allHard, Supplier<List<Question>> retry) {
//        Random random = new Random();
//        int numberQuestions = random.nextInt(70, 81);
//        System.out.println("Nr total intrebari: " + numberQuestions);
//
//        int pSoft = random.nextInt(10, 30);
//        int pMid = random.nextInt(10, 30);
//        int pHard = 100 - pSoft - pMid;
//        if (pHard < 50) {
//            pHard = 50;
//            int rest = 50;
//            pSoft = random.nextInt(0, rest + 1);
//            pMid = rest - pSoft;
//        }
//
//        int nrSoft = (pSoft * numberQuestions) / 100;
//        int nrMid = (pMid * numberQuestions) / 100;
//        int nrHard = numberQuestions - nrSoft - nrMid;
//
//        System.out.println("========= Distributie intrebari =========");
//        System.out.println("SOFT: " + nrSoft + " (" + pSoft + "%)");
//        System.out.println("MID : " + nrMid + " (" + pMid + "%)");
//        System.out.println("HARD: " + nrHard + " (" + pHard + "%)");
//        System.out.println("=========================================");
//
//        if (nrSoft > allSoft.size() || nrMid > allMid.size()) {
//            System.err.println("Insuficiente intrebari");
//        }
//
//        if (nrHard > allHard.size()) {
//            int deficit = nrHard - allHard.size();
//            nrHard = allHard.size();
//            while (deficit > 0) {
//                if (nrSoft < allSoft.size()) nrSoft++;
//                else if (nrMid < allMid.size()) nrMid++;
//                deficit--;
//            }
//        }
//
//        int totalTime = nrSoft * 60 + nrMid * 100 + nrHard * 150;
//        int epsilon = random.nextInt(0, 6);
//        int maxTime = 150 * 60 - epsilon;
//        if (totalTime > maxTime) {
//            System.out.println("Timp depasit (" + totalTime + "s). Regeneram...");
//            return retry.get();
//        }
//
//        List<Question> test = new ArrayList<>();
//        Collections.shuffle(allSoft);
//        Collections.shuffle(allMid);
//        Collections.shuffle(allHard);
//        test.addAll(allSoft.subList(0, Math.min(nrSoft, allSoft.size())));
//        test.addAll(allMid.subList(0, Math.min(nrMid, allMid.size())));
//        test.addAll(allHard.subList(0, Math.min(nrHard, allHard.size())));
//        Collections.shuffle(test);
//        return test;
//    }
//
//    private LevelTest mapLevel(String level) {
//        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION"))
//            return LevelTest.BEGINNER;
//        if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION"))
//            return LevelTest.INTERMEDIATE;
//        return LevelTest.HARD;
//    }
//
//    /** Salvare unificata: UserTest -> Test -> QuestionTest -> TestOfQuestions. */
//    private TestOfQuestions persistTest(String userId, String level,
//                                        int nrOfObjectives, List<Question> questions) {
//        String idTest = generateId(userId, questions);
//        int duration = extractdurationOfTest(questions);
//
//        UserTest userTest = new UserTest();
//        userTest.setIdTest(idTest);
//        userTest.setIdUser(userId);
//        userTestRepository.save(userTest);
//
//        Test test = new Test();
//        test.setIdTest(idTest);
//        test.setNrOfObjectives(nrOfObjectives);
//        test.setLevel(mapLevel(level));
//        test.setNrOfQuestions(questions.size());
//        test.setEstimatedDuration(duration);
//        testRepository.save(test);
//
//        for (Question q : questions) {
//            QuestionTest link = new QuestionTest();
//            link.setIdTest(idTest);
//            link.setIdQuestion(q.getQuestionId().getIdQuestion());
//            questionTestRepository.save(link);
//        }
//
//        return new TestOfQuestions(test, questions);
//    }
//
//    // ============================================================
//    //  SELECTOARE (cu ramura cu/fara domeniu)
//    // ============================================================
//
//    // un singur obiectiv principal
//    private List<List<Question>> selectToGenerateBeginnerTest(String level, int idObjectiv,
//                                                              List<String> domains) {
//        List<List<Question>> grouped = new ArrayList<>();
//        String xmlPath = new ClassPathResource("obiective_9_mi.xml").getPath();
//        List<Integer> subObjIds = getSubObjectivesById(idObjectiv, xmlPath);
//
//        for (Integer subObjId : subObjIds) {
//            List<Question> questions = domains.isEmpty()
//                    ? questionRepository.findByLevelAndObjectives(level, idObjectiv, subObjId)
//                    : questionRepository.findByLevelAndObjectivesAndDomain(level, idObjectiv, subObjId, domains);
//            grouped.add(questions);
//            System.out.println("Gasite " + questions.size() +
//                    " intrebari pentru subobiectivul " + subObjId);
//        }
//        return grouped;
//    }
//
//    // mai multe obiective principale (folosit si de testul final)
//    private List<List<Question>> selectToGenerateBeginnerIntermediateTestMoreObj(String level, int[] idObjectiv,
//                                                                                 List<String> domains) {
//        List<List<Question>> grouped = new ArrayList<>();
//        try {
//            String xmlPath = new ClassPathResource("obiective_9_mi.xml").getPath();
//            for (int objId : idObjectiv) {
//                for (Integer subObjId : getSubObjectivesById(objId, xmlPath)) {
//                    List<Question> questions = domains.isEmpty()
//                            ? questionRepository.findByLevelAndObjectives(level, objId, subObjId)
//                            : questionRepository.findByLevelAndObjectivesAndDomain(level, objId, subObjId, domains);
//                    grouped.add(questions);
//                    System.out.println("Gasite " + questions.size() +
//                            " intrebari pentru obiectivul " + objId + ", subobiectivul " + subObjId);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("Eroare la generarea testului: " + e.getMessage());
//            e.printStackTrace();
//        }
//        return grouped;
//    }
//
//    // ============================================================
//    //  CATEGORIA 1: OBIECTIVE PRINCIPALE (unic / mai multe) x 3 dificultati
//    // ============================================================
//
//    // ---- un obiectiv principal ----
//    public List<Question> generateTestBeginnerAndIntermediate(String level, int obj, String[] domains) {
//        return distributeQuestions(selectToGenerateBeginnerTest(level, obj, toList(domains)));
//    }
//
//    public List<Question> generateHardTest(int obj, String[] domains) {
//        List<String> d = toList(domains);
//        List<Question> allSoft = flatten(selectToGenerateBeginnerTest("SOFTQUESTION", obj, d));
//        List<Question> allMid = flatten(selectToGenerateBeginnerTest("MIDLELEVELQUESTION", obj, d));
//        List<Question> allHard = flatten(selectToGenerateBeginnerTest("HARDQUESTION", obj, d));
//        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTest(obj, domains));
//    }
//
//    // ---- mai multe obiective principale ----
//    public List<Question> generateTestBeginnerAndIntermediateMoreObj(String level, int[] obj, String[] domains) {
//        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObj(level, obj, toList(domains)));
//    }
//
//    public List<Question> generateHardTestMoreObj(int[] obj, String[] domains) {
//        List<String> d = toList(domains);
//        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj, d));
//        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj, d));
//        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj, d));
//        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestMoreObj(obj, domains));
//    }
//
//    // ============================================================
//    //  CATEGORIA 2: OBIECTIVE SECUNDARE (unic / mai multe) x 3 dificultati
//    // ============================================================
//
//    public List<Question> generateTestBeginnerAndIntermediateMoreSubobjectives(int[] subobjectiveIds,
//                                                                               String level, String[] domains) {
//        List<String> d = toList(domains);
//        List<List<Question>> grouped = new ArrayList<>();
//        for (int subId : subobjectiveIds) {
//            List<Question> qs = d.isEmpty()
//                    ? questionRepository.findBySecondaryObjectiveAndLevel(subId, level)
//                    : questionRepository.findBySecondaryObjectiveAndLevelAndDomain(subId, level, d);
//            if (!qs.isEmpty()) grouped.add(qs);
//        }
//        if (grouped.isEmpty())
//            throw new RuntimeException("Nu exista intrebari pentru sub-obiectivele selectate");
//        return distributeQuestions(grouped).stream().distinct().toList();
//    }
//
//    public List<Question> generateHardTestMoreSubobjectives(int[] subobjectives, String[] domains) {
//        List<Question> soft = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "SOFTQUESTION", domains);
//        List<Question> mid = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "MIDLELEVELQUESTION", domains);
//        List<Question> hard = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "HARDQUESTION", domains);
//        List<Question> test = new ArrayList<>();
//        test.addAll(soft);
//        test.addAll(mid);
//        test.addAll(hard);
//        Collections.shuffle(test);
//        return test.stream().distinct().limit(80).toList();
//    }
//
//    // ============================================================
//    //  CATEGORIA 3: TEST FINAL (toate obiectivele) x 3 dificultati
//    // ============================================================
//
//    public List<Question> generateTestBeginnerAndIntermediateAllObjectives(String level, String[] domains) {
//        int[] obj = extractAllObjectivesId();
//        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObj(level, obj, toList(domains)));
//    }
//
//    public List<Question> generateHardTestAllObj(String[] domains) {
//        int[] obj = extractAllObjectivesId();
//        List<String> d = toList(domains);
//        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj, d));
//        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj, d));
//        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj, d));
//        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestAllObj(domains));
//    }
//
//    // ============================================================
//    //  WRITE METHODS (intrari publice) - domain-aware
//    // ============================================================
//
//    // CATEGORIA 1: obiectiv(e) principal(e)
//    public TestOfQuestions writeTestSingleObjective(String userId, String level, int[] obj, String[] domains) {
//        List<Question> questions = new ArrayList<>();
//        System.err.println("Nr obiective: " + obj.length);
//
//        if (obj.length == 1) {
//            if (level.equals("SOFTQUESTION"))
//                questions = generateTestBeginnerAndIntermediate("SOFTQUESTION", obj[0], domains);
//            else if (level.equals("MIDLELEVELQUESTION"))
//                questions = generateTestBeginnerAndIntermediate("MIDLELEVELQUESTION", obj[0], domains);
//            else if (level.equals(LevelTest.HARD.getDenumire()))
//                questions = generateHardTest(obj[0], domains);
//        } else if (obj.length > 1) {
//            if (level.equals("SOFTQUESTION"))
//                questions = generateTestBeginnerAndIntermediateMoreObj("SOFTQUESTION", obj, domains);
//            else if (level.equals("MIDLELEVELQUESTION"))
//                questions = generateTestBeginnerAndIntermediateMoreObj("MIDLELEVELQUESTION", obj, domains);
//            else if (level.equals(LevelTest.HARD.getDenumire()))
//                questions = generateHardTestMoreObj(obj, domains);
//        }
//        System.err.println("Question size: " + questions.size());
//        return persistTest(userId, level, obj.length, questions);
//    }
//
//    // CATEGORIA 2: obiective secundare
//    public TestOfQuestions writeTestSingleAndMoreSubobjectives(String userId, String level,
//                                                               int[] subobjectives, String[] domains) {
//        List<Question> questions = new ArrayList<>();
//        System.err.println("Subobj: " + subobjectives.length);
//
//        if (level.equals("SOFTQUESTION"))
//            questions = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "SOFTQUESTION", domains);
//        else if (level.equals("MIDLELEVELQUESTION"))
//            questions = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "MIDLELEVELQUESTION", domains);
//        else if (level.equals("HARDQUESTION"))
//            questions = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "HARDQUESTION", domains);
//
//        System.err.println("Question size: " + questions.size());
//        return persistTest(userId, level, subobjectives.length, questions);
//    }
//
//    // CATEGORIA 3: test final (toate obiectivele)
//    public TestOfQuestions writingFinalTest(String userId, String level, String[] domains) {
//        List<Question> questions = level.equals(LevelTest.HARD.getDenumire())
//                ? generateHardTestAllObj(domains)
//                : generateTestBeginnerAndIntermediateAllObjectives(level, domains);
//        return persistTest(userId, level, extractAllObjectivesId().length, questions);
//    }
//}

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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

@Service
public class DomainTestService {

    // ============================================================
    //  CONSTANTE DE TIMP (durate per dificultate + plafon test)
    // ============================================================
    private static final int MAX_TEST_SECONDS = 50 * 60;   // 3000s = 50 minute
    private static final int SEC_SOFT = 180;   // 3 minute
    private static final int SEC_MID  = 360;   // 6 minute
    private static final int SEC_HARD = 600;   // 10 minute

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

    // ============================================================
    //  ID & OBIECTIVE (helpere existente)
    // ============================================================

    // metoda aceasta este utilizata in controller, aceasta apeland generarea idului testului
    public String generateIdForUser(String userId, List<Question> questions) {
        return generateId(userId, questions);
    }

    private String resolveXmlPath(String resourceName) throws Exception {
        InputStream is = new ClassPathResource(resourceName).getInputStream();
        File temp = File.createTempFile("obiective", ".xml");
        temp.deleteOnExit();
        Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
        is.close();
        return temp.getAbsolutePath();
    }

    private String generateId(String userId, List<Question> questions) {
        String idTest = "";

        Random random = new Random();
        int number = random.nextInt(900) + 100;

        System.out.println("==================== Generare ID pentru test ====================");
        List<Object> objectivesP = extractObjectivesFromQuestions(questions);
        Set<Object> setObj = new TreeSet<>(objectivesP);
        List<Object> objectives = new ArrayList<>(setObj);

        if (objectives.isEmpty()) {
            System.out.println("Nu s-au gasit obiective pentru intrebarile din test!");
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

        System.out.println("ID test generat: " + idTest);
        return idTest;
    }

    private List<Object> extractObjectivesFromQuestions(List<Question> questions) {
        List<Object> objectives = new ArrayList<>();

        for (Question q : questions) {
            String qId = q.getQuestionId().getIdQuestion();
            System.out.println("Caut obiective pentru intrebarea: " + qId);

            var mainObjs = mainObjectivesRepository.findByIdQuestion(qId);

            if (mainObjs != null && !mainObjs.isEmpty()) {
                objectives.addAll(mainObjs);
                for (MainObjective mo : mainObjs) {
                    System.out.println("Obiectiv principal (DB): " + mo.getIdObjectiv() + " - " + mo.getNameObjectiv());
                }
            } else {
                // Daca nu exista in DB, extragem din JSON-ul intrebarii
                try {
                    String questionJson = q.getQuestion();
                    if (questionJson != null && questionJson.contains("\"mainObjectives\"")) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(questionJson);

                        if (root.has("mainObjectives")) {
                            for (JsonNode objNode : root.get("mainObjectives")) {
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
                    System.err.println("Eroare la parsarea JSON-ului pentru intrebarea " + qId + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Total obiective extrase: " + objectives.size());
        return objectives;
    }

    public List<Object> extractSecondaryObjectivesFromQuestions(List<Question> questions) {
        List<Object> subObjectives = new ArrayList<>();

        for (Question q : questions) {
            String qId = q.getQuestionId().getIdQuestion();
            System.out.println("Caut sub-obiective pentru intrebarea: " + qId);

            var secondaryObjs = secondaryObjectivesRepository.findByIdQuestion(qId);

            if (secondaryObjs != null && !secondaryObjs.isEmpty()) {
                subObjectives.addAll(secondaryObjs);
                for (SecondaryObjective so : secondaryObjs) {
                    System.out.println("Obiectiv secundar (DB): " + so.getIdObjectiv() + " - " + so.getNameObjectiv());
                }
            } else {
                try {
                    String questionJson = q.getQuestion();
                    if (questionJson != null && questionJson.contains("\"secondaryObjectives\"")) {
                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode root = mapper.readTree(questionJson);

                        if (root.has("secondaryObjectives")) {
                            for (JsonNode objNode : root.get("secondaryObjectives")) {
                                int idObj = objNode.path("id").asInt();
                                String nameObj = objNode.path("name").asText();
                                SecondaryObjective so = new SecondaryObjective();
                                so.setIdObjectiv(idObj);
                                so.setNameObjectiv(nameObj);
                                so.setIdQuestion(qId);
                                subObjectives.add(so);
                                System.out.println("Obiectiv secundar (JSON): " + idObj + " - " + nameObj);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Eroare la parsarea JSON-ului pentru intrebarea " + qId + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Total sub-obiective extrase: " + subObjectives.size());
        return subObjectives;
    }

    // extrage idul subobiectivelor
    private List<String> determinateObjectives(List<Objectiv> subObj) {
        List<String> subobsId = new ArrayList<>();
        for (Objectiv oj : subObj) {
            subobsId.add(oj.getId());
        }
        return subobsId;
    }

    // extrage obiectivele principale corespunzatoare obiectivelor secundare
    public List<Objectiv> addPrinciplesObjectivesInList(List<Objectiv> secObj) {
        List<Objectiv> objectives = new ArrayList<>();
        Set<Objectiv> uniqObj = new TreeSet<>();
        List<String> idSubobj = determinateObjectives(secObj);

        try {
//            File xmlFile = new ClassPathResource("obiective_9_mi.xml").getFile();
////            List<Objectiv> mainObj = Objectiv.readObjectiv(xmlFile.getAbsolutePath());
            String xmlPath = resolveXmlPath("obiective_9_mi.xml");
            List<Objectiv> mainObj = Objectiv.readObjectiv(xmlPath);

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

    private int extractdurationOfTest(List<Question> questions) {
        int duration = 0;
        for (Question q : questions) {
            if (q.getLevel().equals("SOFTQUESTION".trim())) {
                duration += SEC_SOFT;
            } else if (q.getLevel().equals("MIDLELEVELQUESTION".trim())) {
                duration += SEC_MID;
            } else if (q.getLevel().equals("HARDQUESTION".trim())) {
                duration += SEC_HARD;
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

    public int[] extractAllObjectivesId() {
        try {
            String xmlPath = resolveXmlPath("obiective_9_mi.xml");
            List<Objectiv> listObj = Objectiv.readObjectiv(xmlPath);
            int[] idObjectives = new int[listObj.size()];
            for (int i = 0; i < listObj.size(); i++) {
                idObjectives[i] = Integer.parseInt(listObj.get(i).getId());
            }
            return idObjectives;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ============================================================
    //  LABELS / GUI / DISTANTA (helpere existente)
    // ============================================================

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
            System.err.println("Eroare la extragerea labelurilor pentru " +
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
            System.err.println("Eroare la extragerea GUI interface-ului din intrebare: "
                    + question.getQuestionId().getIdQuestion() + " - " + e.getMessage());
        }
        return null;
    }

    public double distanceToQuestion(Question q1, Question q2) {
        double distance = 0.0;

        if (q1 == null || q2 == null) {
            return 0.0;
        }

        if (Objects.equals(q1.getQuestionId().getIdQuestion(), q2.getQuestionId().getIdQuestion())) {
            return 1.0;
        }

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

        if (sameMain && !sameSec) {
            distance += 0.1;
        }

        if (sameMain && sameSec && !Objects.equals(q1.getLevel(), q2.getLevel())) {
            Set<String> commonSec = new HashSet<>(secIds1);
            commonSec.retainAll(secIds2);
            distance += 0.1 * commonSec.size();
        }

        Set<String> labels1 = extractLabelsForQuestion(q1);
        Set<String> labels2 = extractLabelsForQuestion(q2);
        Set<String> commonLabels = new HashSet<>(labels1);
        commonLabels.retainAll(labels2);

        if (sameMain && sameSec && Objects.equals(q1.getLevel(), q2.getLevel())) {
            distance += 0.1 * commonLabels.size();
        }

        if (Objects.equals(q1.getLevel(), q2.getLevel())) {
            distance += 0.2;
        }

        String gui1 = extractGuiInterface(q1);
        String gui2 = extractGuiInterface(q2);
        if (Objects.equals(gui1, gui2)) {
            distance += 0.3;
        }

        return Math.min(distance, 1.0);
    }

    // ============================================================
    //  HELPERE COMUNE (domain-aware + distributie + mix hard + persist)
    // ============================================================

    /** null / array gol -> lista goala = fara filtru de domeniu (comportament identic cu cel vechi). */
    private List<String> toList(String[] domains) {
        return (domains == null) ? List.of() : Arrays.asList(domains);
    }

    private List<Question> flatten(List<List<Question>> grouped) {
        List<Question> all = new ArrayList<>();
        grouped.forEach(all::addAll);
        return all;
    }

    /** Costul in secunde al unei intrebari, dupa dificultate. */
    private int questionCost(Question q) {
        String level = q.getLevel() == null ? "" : q.getLevel().trim();
        return switch (level) {
            case "SOFTQUESTION" -> SEC_SOFT;
            case "MIDLELEVELQUESTION" -> SEC_MID;
            case "HARDQUESTION" -> SEC_HARD;
            default -> SEC_SOFT;
        };
    }

    /** Pastreaza intrebarile, in ordine, pana cand bugetul de timp (in secunde) este atins. */
    private List<Question> capByDuration(List<Question> questions, int maxSeconds) {
        List<Question> result = new ArrayList<>();
        int total = 0;
        for (Question q : questions) {
            int cost = questionCost(q);
            if (total + cost > maxSeconds) {
                continue; // sarim intrebarea care nu incape, dar continuam (poate incape una mai ieftina)
            }
            result.add(q);
            total += cost;
        }
        System.out.println("Test plafonat: " + result.size() + " intrebari, "
                + total + "s (" + (total / 60) + " min)");
        return result;
    }

    /**
     * Distribuie echilibrat intrebari pe grupuri (sub-obiective / obiective), apoi
     * plafoneaza testul la MAX_TEST_SECONDS (50 min). Folosita pentru testele
     * SOFT si MIDDLE: intrebarile sunt de o singura dificultate (cea selectata),
     * iar numarul lor rezulta din cat incape in 50 de minute.
     */
    private List<Question> distributeQuestions(List<List<Question>> grouped) {
        Random random = new Random();
        // Pornim de la un numar generos; capByDuration taie automat ce nu incape in 50 min.
        int numberQuestions = random.nextInt(70, 81);
        System.out.println("Nr intrebari candidate (inainte de plafonare): " + numberQuestions);

        int size = grouped.size();
        if (size == 0)
            throw new RuntimeException("Nu exista intrebari pentru criteriile selectate");

        int perGroup = numberQuestions / size;
        int remaining = numberQuestions % size;

        System.out.println("Nr grupuri: " + size);
        System.out.println("Intrebari per grup: " + perGroup);
        System.out.println("Rest de distribuit: " + remaining);

        List<Question> test = new ArrayList<>();
        for (List<Question> subList : grouped) {
            Collections.shuffle(subList);
            for (int i = 0; i < Math.min(perGroup, subList.size()); i++) {
                test.add(subList.get(i));
            }
        }

        int idx = 0;
        int safety = 0;
        while (remaining > 0) {
            List<Question> subList = grouped.get(idx % size);
            if (!subList.isEmpty()) {
                test.add(subList.get(random.nextInt(subList.size())));
                remaining--;
            }
            idx++;
            if (++safety > size * 4) break; // siguranta: evitam bucla infinita daca listele sunt goale
        }

        Collections.shuffle(test);
        // Plafonare la 50 de minute: numarul final de intrebari rezulta din buget.
        test = capByDuration(test, MAX_TEST_SECONDS);
        return test;
    }

    /**
     * Test HARD: 50% din bugetul de timp pe intrebari HARD, 50% pe combinatie soft+middle.
     * Plafon total 50 min. (Parametrul 'retry' este pastrat pentru compatibilitatea
     * semnaturii cu apelurile existente, dar nu mai este necesar - generarea este determinista.)
     */
    private List<Question> buildHardTest(List<Question> allSoft, List<Question> allMid,
                                         List<Question> allHard, Supplier<List<Question>> retry) {
        int halfBudget = MAX_TEST_SECONDS / 2;   // 1500s pentru HARD + 1500s pentru soft/middle

        Collections.shuffle(allSoft);
        Collections.shuffle(allMid);
        Collections.shuffle(allHard);

        List<Question> test = new ArrayList<>();

        // ---- Jumatatea HARD (pana la 1500s) ----
        int hardTime = 0;
        for (Question q : allHard) {
            if (hardTime + SEC_HARD > halfBudget) break;
            test.add(q);
            hardTime += SEC_HARD;
        }

        // ---- Jumatatea SOFT + MIDDLE (alternam, pana la 1500s) ----
        int easyTime = 0;
        int si = 0, mi = 0;
        boolean pickSoft = true;
        while (easyTime < halfBudget && (si < allSoft.size() || mi < allMid.size())) {
            if (pickSoft && si < allSoft.size() && easyTime + SEC_SOFT <= halfBudget) {
                test.add(allSoft.get(si++));
                easyTime += SEC_SOFT;
            } else if (!pickSoft && mi < allMid.size() && easyTime + SEC_MID <= halfBudget) {
                test.add(allMid.get(mi++));
                easyTime += SEC_MID;
            } else if (si < allSoft.size() && easyTime + SEC_SOFT <= halfBudget) {
                // daca middle nu mai incape, umplem cu soft
                test.add(allSoft.get(si++));
                easyTime += SEC_SOFT;
            } else {
                break; // nu mai incape nimic
            }
            pickSoft = !pickSoft;
        }

        int totalTime = hardTime + easyTime;
        System.out.println("========= Test HARD generat =========");
        System.out.println("HARD: " + (hardTime / SEC_HARD) + " intrebari (" + hardTime + "s)");
        System.out.println("SOFT+MIDDLE: " + easyTime + "s");
        System.out.println("Total: " + test.size() + " intrebari, " + totalTime + "s ("
                + (totalTime / 60) + " min)");
        System.out.println("=====================================");

        Collections.shuffle(test);
        return test;
    }

    private LevelTest mapLevel(String level) {
        if (level.equalsIgnoreCase("BEGINNER") || level.equalsIgnoreCase("SOFTQUESTION"))
            return LevelTest.BEGINNER;
        if (level.equalsIgnoreCase("INTERMEDIATE") || level.equalsIgnoreCase("MIDLELEVELQUESTION"))
            return LevelTest.INTERMEDIATE;
        return LevelTest.HARD;
    }

    /** Salvare unificata: UserTest -> Test -> QuestionTest -> TestOfQuestions. */
    private TestOfQuestions persistTest(String userId, String level,
                                        int nrOfObjectives, List<Question> questions) {
        String idTest = generateId(userId, questions);
        int duration = extractdurationOfTest(questions);

        UserTest userTest = new UserTest();
        userTest.setIdTest(idTest);
        userTest.setIdUser(userId);
        userTestRepository.save(userTest);

        Test test = new Test();
        test.setIdTest(idTest);
        test.setNrOfObjectives(nrOfObjectives);
        test.setLevel(mapLevel(level));
        test.setNrOfQuestions(questions.size());
        test.setEstimatedDuration(duration);
        testRepository.save(test);

        for (Question q : questions) {
            QuestionTest link = new QuestionTest();
            link.setIdTest(idTest);
            link.setIdQuestion(q.getQuestionId().getIdQuestion());
            questionTestRepository.save(link);
        }

        return new TestOfQuestions(test, questions);
    }

    // ============================================================
    //  SELECTOARE (cu ramura cu/fara domeniu)
    // ============================================================

    // un singur obiectiv principal
    private List<List<Question>> selectToGenerateBeginnerTest(String level, int idObjectiv,
                                                              List<String> domains) {
        List<List<Question>> grouped = new ArrayList<>();
        String xmlPath = new ClassPathResource("obiective_9_mi.xml").getPath();
        List<Integer> subObjIds = getSubObjectivesById(idObjectiv, xmlPath);

        for (Integer subObjId : subObjIds) {
            List<Question> questions = domains.isEmpty()
                    ? questionRepository.findByLevelAndObjectives(level, idObjectiv, subObjId)
                    : questionRepository.findByLevelAndObjectivesAndDomain(level, idObjectiv, subObjId, domains);
            grouped.add(questions);
            System.out.println("Gasite " + questions.size() +
                    " intrebari pentru subobiectivul " + subObjId);
        }
        return grouped;
    }

    // mai multe obiective principale (folosit si de testul final)
    private List<List<Question>> selectToGenerateBeginnerIntermediateTestMoreObj(String level, int[] idObjectiv,
                                                                                 List<String> domains) {
        List<List<Question>> grouped = new ArrayList<>();
        try {
            String xmlPath = new ClassPathResource("obiective_9_mi.xml").getPath();
            for (int objId : idObjectiv) {
                for (Integer subObjId : getSubObjectivesById(objId, xmlPath)) {
                    List<Question> questions = domains.isEmpty()
                            ? questionRepository.findByLevelAndObjectives(level, objId, subObjId)
                            : questionRepository.findByLevelAndObjectivesAndDomain(level, objId, subObjId, domains);
                    grouped.add(questions);
                    System.out.println("Gasite " + questions.size() +
                            " intrebari pentru obiectivul " + objId + ", subobiectivul " + subObjId);
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la generarea testului: " + e.getMessage());
            e.printStackTrace();
        }
        return grouped;
    }

    // ============================================================
    //  CATEGORIA 1: OBIECTIVE PRINCIPALE (unic / mai multe) x 3 dificultati
    // ============================================================

    // ---- un obiectiv principal ----
    public List<Question> generateTestBeginnerAndIntermediate(String level, int obj, String[] domains) {
        return distributeQuestions(selectToGenerateBeginnerTest(level, obj, toList(domains)));
    }

    public List<Question> generateHardTest(int obj, String[] domains) {
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerTest("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerTest("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerTest("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTest(obj, domains));
    }

    // ---- mai multe obiective principale ----
    public List<Question> generateTestBeginnerAndIntermediateMoreObj(String level, int[] obj, String[] domains) {
        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObj(level, obj, toList(domains)));
    }

    public List<Question> generateHardTestMoreObj(int[] obj, String[] domains) {
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestMoreObj(obj, domains));
    }

    // ============================================================
    //  CATEGORIA 2: OBIECTIVE SECUNDARE (unic / mai multe) x 3 dificultati
    // ============================================================

    public List<Question> generateTestBeginnerAndIntermediateMoreSubobjectives(int[] subobjectiveIds,
                                                                               String level, String[] domains) {
        List<String> d = toList(domains);
        List<List<Question>> grouped = new ArrayList<>();
        for (int subId : subobjectiveIds) {
            List<Question> qs = d.isEmpty()
                    ? questionRepository.findBySecondaryObjectiveAndLevel(subId, level)
                    : questionRepository.findBySecondaryObjectiveAndLevelAndDomain(subId, level, d);
            if (!qs.isEmpty()) grouped.add(qs);
        }
        if (grouped.isEmpty())
            throw new RuntimeException("Nu exista intrebari pentru sub-obiectivele selectate");
        return distributeQuestions(grouped).stream().distinct().toList();
    }

    public List<Question> generateHardTestMoreSubobjectives(int[] subobjectives, String[] domains) {
        List<String> d = toList(domains);
        List<List<Question>> groupedSoft = new ArrayList<>();
        List<List<Question>> groupedMid = new ArrayList<>();
        List<List<Question>> groupedHard = new ArrayList<>();

        for (int subId : subobjectives) {
            List<Question> soft = d.isEmpty()
                    ? questionRepository.findBySecondaryObjectiveAndLevel(subId, "SOFTQUESTION")
                    : questionRepository.findBySecondaryObjectiveAndLevelAndDomain(subId, "SOFTQUESTION", d);
            List<Question> mid = d.isEmpty()
                    ? questionRepository.findBySecondaryObjectiveAndLevel(subId, "MIDLELEVELQUESTION")
                    : questionRepository.findBySecondaryObjectiveAndLevelAndDomain(subId, "MIDLELEVELQUESTION", d);
            List<Question> hard = d.isEmpty()
                    ? questionRepository.findBySecondaryObjectiveAndLevel(subId, "HARDQUESTION")
                    : questionRepository.findBySecondaryObjectiveAndLevelAndDomain(subId, "HARDQUESTION", d);
            if (!soft.isEmpty()) groupedSoft.add(soft);
            if (!mid.isEmpty()) groupedMid.add(mid);
            if (!hard.isEmpty()) groupedHard.add(hard);
        }

        List<Question> allSoft = flatten(groupedSoft);
        List<Question> allMid = flatten(groupedMid);
        List<Question> allHard = flatten(groupedHard);
        return buildHardTest(allSoft, allMid, allHard,
                () -> generateHardTestMoreSubobjectives(subobjectives, domains));
    }

    // ============================================================
    //  CATEGORIA 3: TEST FINAL (toate obiectivele) x 3 dificultati
    // ============================================================

    public List<Question> generateTestBeginnerAndIntermediateAllObjectives(String level, String[] domains) {
        int[] obj = extractAllObjectivesId();
        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObj(level, obj, toList(domains)));
    }

    public List<Question> generateHardTestAllObj(String[] domains) {
        int[] obj = extractAllObjectivesId();
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObj("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestAllObj(domains));
    }

    // ============================================================
    //  WRITE METHODS (intrari publice) - domain-aware
    // ============================================================

    // CATEGORIA 1: obiectiv(e) principal(e)
    public TestOfQuestions writeTestSingleObjective(String userId, String level, int[] obj, String[] domains) {
        List<Question> questions = new ArrayList<>();
        System.err.println("Nr obiective: " + obj.length);

        if (obj.length == 1) {
            if (level.equals("SOFTQUESTION"))
                questions = generateTestBeginnerAndIntermediate("SOFTQUESTION", obj[0], domains);
            else if (level.equals("MIDLELEVELQUESTION"))
                questions = generateTestBeginnerAndIntermediate("MIDLELEVELQUESTION", obj[0], domains);
            else if (level.equals(LevelTest.HARD.getDenumire()))
                questions = generateHardTest(obj[0], domains);
        } else if (obj.length > 1) {
            if (level.equals("SOFTQUESTION"))
                questions = generateTestBeginnerAndIntermediateMoreObj("SOFTQUESTION", obj, domains);
            else if (level.equals("MIDLELEVELQUESTION"))
                questions = generateTestBeginnerAndIntermediateMoreObj("MIDLELEVELQUESTION", obj, domains);
            else if (level.equals(LevelTest.HARD.getDenumire()))
                questions = generateHardTestMoreObj(obj, domains);
        }
        System.err.println("Question size: " + questions.size());
        return persistTest(userId, level, obj.length, questions);
    }

    // CATEGORIA 2: obiective secundare
    public TestOfQuestions writeTestSingleAndMoreSubobjectives(String userId, String level,
                                                               int[] subobjectives, String[] domains) {
        List<Question> questions = new ArrayList<>();
        System.err.println("Subobj: " + subobjectives.length);

        if (level.equals("SOFTQUESTION"))
            questions = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "SOFTQUESTION", domains);
        else if (level.equals("MIDLELEVELQUESTION"))
            questions = generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectives, "MIDLELEVELQUESTION", domains);
        else if (level.equals("HARDQUESTION"))
            questions = generateHardTestMoreSubobjectives(subobjectives, domains);

        System.err.println("Question size: " + questions.size());
        return persistTest(userId, level, subobjectives.length, questions);
    }

    // CATEGORIA 3: test final (toate obiectivele)
    public TestOfQuestions writingFinalTest(String userId, String level, String[] domains) {
        List<Question> questions = level.equals(LevelTest.HARD.getDenumire())
                ? generateHardTestAllObj(domains)
                : generateTestBeginnerAndIntermediateAllObjectives(level, domains);
        return persistTest(userId, level, extractAllObjectivesId().length, questions);
    }


    // ============================================================================
//  ████  MATE11 — DUPLICAT AL LOGICII MATE9, PE obiective_11_mi.xml  ████
// ============================================================================
//  Se adauga ACESTE metode in clasa DomainTestService, langa cele de Mate9.
//  Sunt copii fidele ale metodelor existente, cu singura diferenta ca citesc
//  fisierul "obiective_11_mi.xml" in loc de "obiective_9_mi.xml".
//  Metodele helper comune (distributeQuestions, buildHardTest, flatten,
//  toList, persistTest, getSubObjectivesById, resolveXmlPath) sunt
//  reutilizate ca atare — nu se dupleaza.
// ============================================================================

// ---- ID & OBIECTIVE (Mate11) ----

    public int[] extractAllObjectivesIdMate11() {
        try {
            String xmlPath = resolveXmlPath("obiective_11_mi.xml");
            List<Objectiv> listObj = Objectiv.readObjectiv(xmlPath);
            int[] idObjectives = new int[listObj.size()];
            for (int i = 0; i < listObj.size(); i++) {
                idObjectives[i] = Integer.parseInt(listObj.get(i).getId());
            }
            return idObjectives;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

// ---- SELECTOARE (Mate11) ----

    // un singur obiectiv principal
    private List<List<Question>> selectToGenerateBeginnerTestMate11(String level, int idObjectiv,
                                                                    List<String> domains) {
        List<List<Question>> grouped = new ArrayList<>();
        String xmlPath = new ClassPathResource("obiective_11_mi.xml").getPath();
        List<Integer> subObjIds = getSubObjectivesById(idObjectiv, xmlPath);

        for (Integer subObjId : subObjIds) {
            List<Question> questions = domains.isEmpty()
                    ? questionRepository.findByLevelAndObjectives(level, idObjectiv, subObjId)
                    : questionRepository.findByLevelAndObjectivesAndDomain(level, idObjectiv, subObjId, domains);
            grouped.add(questions);
            System.out.println("[Mate11] Gasite " + questions.size() +
                    " intrebari pentru subobiectivul " + subObjId);
        }
        return grouped;
    }

    // mai multe obiective principale (folosit si de testul final)
    private List<List<Question>> selectToGenerateBeginnerIntermediateTestMoreObjMate11(String level, int[] idObjectiv,
                                                                                       List<String> domains) {
        List<List<Question>> grouped = new ArrayList<>();
        try {
            String xmlPath = new ClassPathResource("obiective_11_mi.xml").getPath();
            for (int objId : idObjectiv) {
                for (Integer subObjId : getSubObjectivesById(objId, xmlPath)) {
                    List<Question> questions = domains.isEmpty()
                            ? questionRepository.findByLevelAndObjectives(level, objId, subObjId)
                            : questionRepository.findByLevelAndObjectivesAndDomain(level, objId, subObjId, domains);
                    grouped.add(questions);
                    System.out.println("[Mate11] Gasite " + questions.size() +
                            " intrebari pentru obiectivul " + objId + ", subobiectivul " + subObjId);
                }
            }
        } catch (Exception e) {
            System.err.println("Eroare la generarea testului (Mate11): " + e.getMessage());
            e.printStackTrace();
        }
        return grouped;
    }

// ---- CATEGORIA 1: OBIECTIVE PRINCIPALE (Mate11) ----

    // un obiectiv principal
    public List<Question> generateTestBeginnerAndIntermediateMate11(String level, int obj, String[] domains) {
        return distributeQuestions(selectToGenerateBeginnerTestMate11(level, obj, toList(domains)));
    }

    public List<Question> generateHardTestMate11(int obj, String[] domains) {
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerTestMate11("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerTestMate11("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerTestMate11("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestMate11(obj, domains));
    }

    // mai multe obiective principale
    public List<Question> generateTestBeginnerAndIntermediateMoreObjMate11(String level, int[] obj, String[] domains) {
        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObjMate11(level, obj, toList(domains)));
    }

    public List<Question> generateHardTestMoreObjMate11(int[] obj, String[] domains) {
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestMoreObjMate11(obj, domains));
    }

// ---- CATEGORIA 2: OBIECTIVE SECUNDARE (Mate11) ----
// Sub-obiectivele vin din frontend si se cauta direct in baza de date prin
// findBySecondaryObjective... — NU se citeste XML. Logica e identica cu Mate9,
// deci o reutilizam ca atare. (Metodele de mai jos sunt simple wrappere
// pentru a avea un punct de intrare separat, consistent cu restul Mate11.)

    public List<Question> generateTestBeginnerAndIntermediateMoreSubobjectivesMate11(int[] subobjectiveIds,
                                                                                     String level, String[] domains) {
        return generateTestBeginnerAndIntermediateMoreSubobjectives(subobjectiveIds, level, domains);
    }

    public List<Question> generateHardTestMoreSubobjectivesMate11(int[] subobjectives, String[] domains) {
        return generateHardTestMoreSubobjectives(subobjectives, domains);
    }

// ---- CATEGORIA 3: TEST FINAL (Mate11) ----

    public List<Question> generateTestBeginnerAndIntermediateAllObjectivesMate11(String level, String[] domains) {
        int[] obj = extractAllObjectivesIdMate11();
        return distributeQuestions(selectToGenerateBeginnerIntermediateTestMoreObjMate11(level, obj, toList(domains)));
    }

    public List<Question> generateHardTestAllObjMate11(String[] domains) {
        int[] obj = extractAllObjectivesIdMate11();
        List<String> d = toList(domains);
        List<Question> allSoft = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("SOFTQUESTION", obj, d));
        List<Question> allMid = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("MIDLELEVELQUESTION", obj, d));
        List<Question> allHard = flatten(selectToGenerateBeginnerIntermediateTestMoreObjMate11("HARDQUESTION", obj, d));
        return buildHardTest(allSoft, allMid, allHard, () -> generateHardTestAllObjMate11(domains));
    }

// ---- WRITE METHODS (intrari publice, Mate11) ----

    // CATEGORIA 1: obiectiv(e) principal(e)
    public TestOfQuestions writeTestSingleObjectiveMate11(String userId, String level, int[] obj, String[] domains) {
        List<Question> questions = new ArrayList<>();
        System.err.println("[Mate11] Nr obiective: " + obj.length);

        if (obj.length == 1) {
            if (level.equals("SOFTQUESTION"))
                questions = generateTestBeginnerAndIntermediateMate11("SOFTQUESTION", obj[0], domains);
            else if (level.equals("MIDLELEVELQUESTION"))
                questions = generateTestBeginnerAndIntermediateMate11("MIDLELEVELQUESTION", obj[0], domains);
            else if (level.equals(LevelTest.HARD.getDenumire()))
                questions = generateHardTestMate11(obj[0], domains);
        } else if (obj.length > 1) {
            if (level.equals("SOFTQUESTION"))
                questions = generateTestBeginnerAndIntermediateMoreObjMate11("SOFTQUESTION", obj, domains);
            else if (level.equals("MIDLELEVELQUESTION"))
                questions = generateTestBeginnerAndIntermediateMoreObjMate11("MIDLELEVELQUESTION", obj, domains);
            else if (level.equals(LevelTest.HARD.getDenumire()))
                questions = generateHardTestMoreObjMate11(obj, domains);
        }
        System.err.println("[Mate11] Question size: " + questions.size());
        return persistTest(userId, level, obj.length, questions);
    }

    // CATEGORIA 2: obiective secundare
    public TestOfQuestions writeTestSingleAndMoreSubobjectivesMate11(String userId, String level,
                                                                     int[] subobjectives, String[] domains) {
        List<Question> questions = new ArrayList<>();
        System.err.println("[Mate11] Subobj: " + subobjectives.length);

        if (level.equals("SOFTQUESTION"))
            questions = generateTestBeginnerAndIntermediateMoreSubobjectivesMate11(subobjectives, "SOFTQUESTION", domains);
        else if (level.equals("MIDLELEVELQUESTION"))
            questions = generateTestBeginnerAndIntermediateMoreSubobjectivesMate11(subobjectives, "MIDLELEVELQUESTION", domains);
        else if (level.equals("HARDQUESTION"))
            questions = generateHardTestMoreSubobjectivesMate11(subobjectives, domains);

        System.err.println("[Mate11] Question size: " + questions.size());
        return persistTest(userId, level, subobjectives.length, questions);
    }

    // CATEGORIA 3: test final (toate obiectivele din obiective_11_mi.xml)
    public TestOfQuestions writingFinalTestMate11(String userId, String level, String[] domains) {
        List<Question> questions = level.equals(LevelTest.HARD.getDenumire())
                ? generateHardTestAllObjMate11(domains)
                : generateTestBeginnerAndIntermediateAllObjectivesMate11(level, domains);
        return persistTest(userId, level, extractAllObjectivesIdMate11().length, questions);
    }
}