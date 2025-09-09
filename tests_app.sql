-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Gazdă: 127.0.0.1
-- Timp de generare: sept. 09, 2025 la 11:55 AM
-- Versiune server: 10.4.32-MariaDB
-- Versiune PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Bază de date: `tests_app`
--

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `email`
--

CREATE TABLE `email` (
  `idUser` varchar(6) NOT NULL,
  `email` varchar(100) NOT NULL,
  `id` bigint(20) NOT NULL,
  `status` varchar(255) NOT NULL DEFAULT 'ACTIVE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `email`
--

INSERT INTO `email` (`idUser`, `email`, `id`, `status`) VALUES
('U00012', 'maria@example.com', 4, 'ACTIVE'),
('U00013', 'cristi.valea@example.com', 5, 'ACTIVE'),
('U00013', 'email.nou@example.com', 6, 'ACTIVE'),
('U00014', 'cristivalea@gmail.com', 7, 'ACTIVE'),
('U00015', 'cristivalea22@gmail.com', 8, 'ACTIVE'),
('U00016', 'valeacristian25@gmail.com', 9, 'ACTIVE'),
('U00017', 'valeacristian25@gmail.com', 10, 'ACTIVE'),
('U00014', 'Valy123@gmail.com', 11, 'ACTIVE'),
('U00018', 'valeacristian25@gmail.com', 12, 'ACTIVE'),
('U00018', 'cristivalea22@gmail.com', 13, 'ACTIVE'),
('U00019', 'cristi.valea@example.com', 14, 'ACTIVE'),
('U00020', 'cristi.valea@example.com', 15, 'ACTIVE'),
('U00021', 'valeacristian25@gmail.com', 16, 'ACTIVE'),
('U00022', 'valeacristian25@gmail.com', 17, 'ACTIVE'),
('U00021', 'cristi.valea@test.com', 18, 'ACTIVE'),
('U00023', 'valeacristian25@gmail.com', 19, 'ACTIVE'),
('U00024', 'ion.popescu@example.com', 20, 'ACTIVE'),
('U00025', 'valeacristian25@gmail.com', 21, 'ACTIVE'),
('U00026', 'valeacristian25@gmail.com', 22, 'ACTIVE'),
('U00027', 'valeacristian25@gmail.com', 23, 'ACTIVE'),
('U00028', 'valeacristian25@gmail.com', 24, 'ACTIVE'),
('U00029', 'valeacristian25@gmail.com', 25, 'ACTIVE'),
('U00024', 'valeacristian25@gmail.com', 26, 'ACTIVE');

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `main_objectives`
--

CREATE TABLE `main_objectives` (
  `id` int(11) NOT NULL COMMENT 'Primary_key',
  `idQuestion` varchar(100) NOT NULL,
  `idObjectiv` int(11) NOT NULL,
  `nameObjectiv` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `question`
--

CREATE TABLE `question` (
  `id` int(11) NOT NULL COMMENT 'Primary_Key',
  `idQuestion` varchar(100) NOT NULL COMMENT 'Primary_Comp_Key',
  `question` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`question`))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `question`
--

INSERT INTO `question` (`id`, `idQuestion`, `question`) VALUES
(1, 'ManipulateDataUsingStringBuilder100', '{\"format\":\"TEXT_UP_COD\",\"type\":\"MIDLELEVELQUESTION\",\"id\":\"ManipulateDataUsingStringBuilder100\",\"mainObjectives\":[{\"id\":\"9\",\"name\":\"Working with Selected classes from the Java API\"},{\"id\":20,\"name\":\"Learn StringBuilder\"},{\"id\":30,\"name\":\"Learn StringBuilderNew\"}],\"secondaryObjectives\":[{\"id\":\"91\",\"name\":\"Manipulate data using the StringBuilder class and its methods\"}],\"estimateSeconds\":100,\"Content\":[{\"textUP\":\"Explicația pentru această problemă...\",\"cod\":\"System.out.println(a+b);\\nSystem.out.println(a+b);\"}],\"responses\":[{\"respoonse\":\"cars carts\"},{\"respoonse\":\"ccars arts\"},{\"respoonse\":\"The code does not compile.\"},{\"respoonse\":\"The code compiles but throws an exception at runtime.\"},{\"response\":\"Nou raspuns\"},{\"response\":\"New answer\"}],\"answers\":[{\"answer\":false},{\"answer\":false},{\"answer\":false},{\"answer\":false},{\"answer\":\"New answer\"}],\"hint\":\"Use invalid index\"}'),
(3, 'JavaDataTypes101', '{\"format\":\"text_up\",\"type\":\"Test_hint\",\"id\":\"JavaDataTypes101\",\"mainObjectives\":[{\"id\":\"2\",\"name\":\"Working With Java Data Types\"}],\"secondaryObjectives\":[],\"estimateSeconds\":100,\"Content\":[{\"cod\":\"System.out.println(a+b);\"}],\"responses\":[{\"respoonse\":\"None\"},{\"respoonse\":\"One\"},{\"respoonse\":\"Two\"}],\"answers\":[{\"answer\":false},{\"answer\":false},{\"answer\":true}],\"hint\":\"Test_hint\"}'),
(4, 'StringBuilder101', '{\"format\":\"TEXT_UP_COD\",\"type\":\"HARDQUESTION\",\"id\":\"StringBuilder101\",\"mainObjectives\":[{\"id\":\"9\",\"name\":\"Working with Selected classes from the Java API\"}],\"secondaryObjectives\":[{\"id\":\"91\",\"name\":\"Manipulate data using the StringBuilder class and its methods\"}],\"estimateSeconds\":150,\"Content\":[{\"cod\":\"System.out.println(a+b);\",\"textUP\":\"Explicația pentru această problemă...\"}],\"responses\":[{\"respoonse\":\"Leaves growing\"},{\"respoonse\":\"ing\"},{\"respoonse\":\"wing\"}],\"answers\":[{\"answer\":false},{\"answer\":false},{\"answer\":true}],\"hint\":\"NUL\"}');

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `response`
--

CREATE TABLE `response` (
  `idResponse` int(11) NOT NULL COMMENT 'Primary_key',
  `type` varchar(100) NOT NULL,
  `content` varchar(200) NOT NULL,
  `selectedType` varchar(100) NOT NULL,
  `responseValue` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `response`
--

INSERT INTO `response` (`idResponse`, `type`, `content`, `selectedType`, `responseValue`) VALUES
(2, 'Correct', 'Răspuns corect', 'one choice', 1),
(3, 'multiple_choice', 'Two', 'option', 1);

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `responses`
--

CREATE TABLE `responses` (
  `id` int(11) NOT NULL,
  `IdQuestion` varchar(100) NOT NULL,
  `idResponse` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `responses`
--

INSERT INTO `responses` (`id`, `IdQuestion`, `idResponse`) VALUES
(3, 'JavaDataTypes101', 3);

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `secondary_objectives`
--

CREATE TABLE `secondary_objectives` (
  `id` int(11) NOT NULL COMMENT 'Primary_key',
  `idQuestion` varchar(100) NOT NULL,
  `idObjective` int(11) NOT NULL,
  `nameObjective` varchar(10000) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `security_questions`
--

CREATE TABLE `security_questions` (
  `idUser` varchar(10) NOT NULL,
  `question` varchar(500) NOT NULL,
  `response` varchar(500) NOT NULL,
  `question_id` bigint(20) NOT NULL,
  `status` enum('INACTIV','ACTIVE') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `security_questions`
--

INSERT INTO `security_questions` (`idUser`, `question`, `response`, `question_id`, `status`) VALUES
('U00012', 'What is your favorite color?', 'Red', 4, 'INACTIV'),
('U00013', 'Care este numele primului tău animal de companie?', 'Tom', 5, 'ACTIVE'),
('U00014', 'Care este culoarea favorita?', 'Alb', 6, 'INACTIV'),
('U00018', 'Care este culoarea preferata', 'Verde', 7, 'ACTIVE'),
('U00022', 'Masina Preferata?', 'BMW', 8, 'ACTIVE'),
('U00023', 'Care este numele mamei?', 'Care este culoarea preferata?', 9, 'ACTIVE'),
('U00023', 'Mihaela', 'Rosu', 10, 'ACTIVE'),
('U00029', 'Cati ani am?', '22', 11, 'ACTIVE'),
('U00029', 'In ce an sunt la facultate?', '4', 12, 'ACTIVE');

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `session`
--

CREATE TABLE `session` (
  `id` int(11) NOT NULL COMMENT 'Primary_Key',
  `token` varchar(500) NOT NULL COMMENT 'Primary_Key',
  `userId` varchar(100) NOT NULL,
  `start_session` datetime NOT NULL,
  `end_session` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `session`
--

INSERT INTO `session` (`id`, `token`, `userId`, `start_session`, `end_session`) VALUES
(1, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25wb3Blc2N1MTIzIiwiaWF0IjoxNzU3MzQ4MzI4LCJleHAiOjE3NTc0MzQ3Mjh9.zCw4HjGwdipPd_lRV1n7aQkfNzQ3pehiN37orWAFSCk', 'U00024', '2025-09-08 16:19:25', '2025-09-08 17:19:25'),
(2, 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJpb25wb3Blc2N1MTIzIiwiaWF0IjoxNzU3NDAwODgyLCJleHAiOjE3NTc0ODcyODJ9.BVMCbATdYMVcN2XIOCESwSQWTWd9OoJ2pM4ZyQkuAS8', 'U00024', '2025-09-09 06:55:40', '2025-09-09 07:55:40');

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `source_code`
--

CREATE TABLE `source_code` (
  `idSourceCode` int(11) NOT NULL COMMENT 'Primary_key',
  `programmingLanguage` varchar(100) NOT NULL,
  `description` varchar(5000) NOT NULL,
  `content` longtext NOT NULL,
  `fileName` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `source_code`
--

INSERT INTO `source_code` (`idSourceCode`, `programmingLanguage`, `description`, `content`, `fileName`) VALUES
(1, 'Java', 'Java Code', 'package org.example.question;\n\nimport com.fasterxml.jackson.databind.ObjectMapper;\nimport com.fasterxml.jackson.databind.SerializationFeature;\nimport org.example.core.AbstractContent;\nimport org.example.core.AbstractResponse;\nimport org.example.core.Objectiv;\nimport org.example.core.Question;\n\nimport java.util.*;\n\npublic class WriteJSON {\n    ParseXMLQuestion parseXMLQuestion = new ParseXMLQuestion();\n//    protected  Question question = parseXMLQuestion.parseFromFile(\"D:\\\\LICENTA\\\\Intrebari_XML\\\\write_xml_java.xml\");\n\n    public  String writeJSON(Question question){\n        try {\n            ObjectMapper mapper = new ObjectMapper();\n            mapper.enable(SerializationFeature.INDENT_OUTPUT);\n\n            Map<String, Object> data = new LinkedHashMap<>();\n            data.put(\"format\", question.getGuiType());\n            data.put(\"type\", question.getDificulty());\n            data.put(\"id\", question.getIdQuestion());\n\n            List<Map<String, String>> mainObjectivesList = new ArrayList<>();\n            if (question.getMainObjectives() != null) {\n                for (Objectiv obj : question.getMainObjectives()) {\n                    Map<String, String> objMap = new LinkedHashMap<>();\n                    objMap.put(\"id\", obj.getId());\n                    objMap.put(\"name\", obj.getNume());\n                    mainObjectivesList.add(objMap);\n                }\n            }\n            data.put(\"mainObjectives\", mainObjectivesList);\n\n            List<Map<String, String>> secondaryObjective = new ArrayList<>();\n            if(question.getSecondaryObjectiv() != null){\n                for(Objectiv os : question.getSecondaryObjectiv()){\n                    Map<String, String> obS = new LinkedHashMap<>();\n                    obS.put(\"id\", os.getId());\n                    obS.put(\"name\", os.getNume());\n                    secondaryObjective.add(obS);\n                }\n            }\n            data.put(\"secondaryObjectives\", secondaryObjective);\n\n            data.put(\"estimateSeconds\", question.getTimeLength());\n\n            Map<String, AbstractContent> contentMap = new LinkedHashMap<>();\n            if(question.getConteent() != null){\n\n                contentMap.put(\"text\", question.getConteent());\n            }\n            data.put(\"Content\", contentMap);\n\n            List<Map<String, String>> responses = new ArrayList<>();\n            if(question.getResponses() != null) {\n                for (AbstractResponse ar : question.getResponses()) {\n                    Map<String, String> mapResp = new LinkedHashMap<>();\n                    mapResp.put(\"respoonse\", ar.getTxt());\n                    responses.add(mapResp);\n                }\n            }\n            data.put(\"responses\", responses);\n\n            List<Map<String, Boolean>> answers = new ArrayList<>();\n            if(question.getAnswers() != null){\n                for(boolean ans : question.getAnswers()){\n                    Map<String, Boolean> ansMap = new LinkedHashMap<>();\n                    ansMap.put(\"answer\", ans);\n                    answers.add(ansMap);\n                }\n            }\n            data.put(\"answers\", answers);\n\n            data.put(\"hint\", question.getHint());\n            String jsonString = mapper.writeValueAsString(data);\n\n//            System.out.println(jsonString);\n            return String.format(jsonString);\n        }catch (Exception e){\n            System.err.println(\"Error write JSON\");\n            return null;\n        }\n    }\n\n    public static void main(String[] args){\n        ParseXMLQuestion parseXMLQuestion = new ParseXMLQuestion();\n        Question question1 = parseXMLQuestion.parseFromFile(\"D:\\\\LICENTA\\\\Intrebari_XML\\\\intrebare_test.xml\");\n        WriteJSON writeJSON = new WriteJSON();\n        String string = writeJSON.writeJSON(question1);\n        System.out.println(string);\n    }\n}\n', 'WriteJSON.java'),
(4, 'Java', 'Java Code', '@PostMapping\npublic ResponseEntity<SourceCode> addSourceCode(@RequestBody SourceCode sourceCode) {\n    return ResponseEntity.ok(sourceCodeService.addSourceCode(sourceCode));\n}', 'WriteJSON.java');

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `time_length_question`
--

CREATE TABLE `time_length_question` (
  `id` int(11) NOT NULL COMMENT 'Primary_key',
  `idQuestion` varchar(100) NOT NULL COMMENT 'Primary_key',
  `timeLength` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structură tabel pentru tabel `users`
--

CREATE TABLE `users` (
  `idUser` varchar(6) NOT NULL COMMENT 'Primary key',
  `firstName` varchar(50) NOT NULL,
  `secondName` varchar(200) NOT NULL,
  `dataBirth` date NOT NULL,
  `registrationDate` date NOT NULL,
  `phoneNumber` varchar(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `passsword` varchar(500) NOT NULL,
  `status` enum('INACTIV','ACTIVE') NOT NULL,
  `role` enum('USER','ADMIN') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Eliminarea datelor din tabel `users`
--

INSERT INTO `users` (`idUser`, `firstName`, `secondName`, `dataBirth`, `registrationDate`, `phoneNumber`, `username`, `passsword`, `status`, `role`) VALUES
('U00012', 'Popescu', 'Marian', '1995-05-10', '2025-08-05', '0722123456', 'maria123', '$2a$10$847r8Uj6qaF2.gK0UpyILOopg7caAlrA.Z70qzpzYM6TuHWTDP5L2', 'INACTIV', 'USER'),
('U00013', 'Mihailescu', 'Mircea', '1995-08-13', '2025-08-13', '0722000000', 'cristi123', '$2a$10$Fz6frChD0aPYTlPyv8LN9eTmnREgRpy7oehe6v6z/xjTYFZP9w.Aq', 'INACTIV', 'ADMIN'),
('U00014', 'Valea', 'Cristian', '2003-07-28', '2025-08-18', '0751503251', 'cvalea1', '$2a$10$jylbmEBqlpY6EJRkjBMHAONHMuB9Bwq0WUm1FAX/axYTaajMu/i4S', 'ACTIVE', 'USER'),
('U00015', 'Valea', 'Cristian', '2003-07-28', '2025-08-18', '0751503251', 'valy2', '$2a$10$P7qALibHK2GYIaEs6mwNCeCAZF/i3r5lVp4ZOoRnbH3UsGlM0.rX2', 'ACTIVE', 'USER'),
('U00016', 'Valea', 'Valentin', '2003-07-28', '2025-08-18', '0751503251', 'valyv3', '$2a$10$.thrxGhSQoKYYN7c/bcureD8d6y5M9.YHTxAOrrbtWJo1r93SN8bW', 'ACTIVE', 'USER'),
('U00017', 'Valea', 'Cristian Valentin', '2003-07-28', '2025-08-18', '+40751503251', 'cristivaly', '$2a$10$4xNT7575n07aLKFnronB9.9rRMrdt8jKUHVDKk.Kk.hYAHpkmgaSa', 'ACTIVE', 'USER'),
('U00018', 'Valea', 'Cristian', '2003-07-28', '2025-08-21', '+40751503251', 'vcristi', '$2a$10$qmLn8bcdVmlCbMT0x3xid.nwEtbpmxEA.1YmuPW/TPo0lnr6hZ34C', 'ACTIVE', 'ADMIN'),
('U00019', 'Valea', 'Cristian', '2003-07-28', '2025-08-21', '0751503251', 'cvalea190', '$2a$10$Lbp3SA3NYMm1zqXSAb/5YuClSaSKgGg5PlF7qFwmSdJI5oPhoNghy', 'ACTIVE', 'ADMIN'),
('U00020', 'Valea', 'Cristian', '2003-07-28', '2025-08-21', '0751503251', 'cvalea17', '$2a$10$NWRstZv5pGV6G8lyPYDKx.w2ALLXS1gr0ytQ7zEBS60IhkNcj6Z5q', 'INACTIV', 'ADMIN'),
('U00021', 'ValeaValea', 'Valeantin', '2003-07-28', '2025-08-21', '+40751503251', 'vvaly', '$2a$10$J3MCRmWWRB4s3IDscgmnP.76J6tLJWnFlqFqcuNG1qtXJ4szlyZpC', 'ACTIVE', 'ADMIN'),
('U00022', 'Valea', 'valentin', '2003-07-28', '2025-08-21', '+40751503251', 'valyv', '$2a$10$drDQUUB8plfHwZB0sPrnw.zqcpGXe2ZoXSz9jE1ykJzxNby7IexVW', 'INACTIV', 'USER'),
('U00023', 'Vasilescu', 'Mircea', '2002-08-07', '2025-08-23', '+40751503251', 'mircea.vasi', '$2a$10$6gGCLAGo4eiBw5739GDKt.PDsraJXLmLrITPs681mm0ltof0i1IxW', 'ACTIVE', 'USER'),
('U00024', 'Ion', 'Popescu', '1990-05-15', '2025-09-08', '+40740123456', 'ionpopescu123', '$2a$10$NNbdX7oRFpuVn0xpYj520.5e903L7wrs5u527kGmGcv5g7OAr/KJ6', 'ACTIVE', 'USER'),
('U00025', 'Valea', 'Cristian', '2004-06-30', '2025-09-08', '+40751503251', 'cristi1', '$2a$10$k7ckiz6zuQ9btvtjtVN68.w0.IqnpFE2MzN./Rua9/3mU3iFI4cYK', 'ACTIVE', 'ADMIN'),
('U00026', 'Valea', 'Valentin', '2025-09-01', '2025-09-08', '+40751503251', 'valy23456', '$2a$10$A6Q5qgjD4MTbAKJo2ym/5OfGzgfVoj1krrDTyx4tYlsLpI/7DyAY6', 'ACTIVE', 'USER'),
('U00027', 'Valea', 'Valentin', '2025-09-01', '2025-09-08', '+40751503251', 'valy23122367890', '$2a$10$gbJHBEHVxMu9MA2wp2xVZekAJHd.tLN.1weEtAtQ0qp0nlpA231lG', 'ACTIVE', 'USER'),
('U00028', 'Valea', 'Valentin', '2000-07-29', '2025-09-08', '+40751503251', 'cvaly3918275430', '$2a$10$pHgPH3FV.3fs3DCruXXAYuTNywwGn3QHqMLAtBbhdFwOQCdCRfCtW', 'ACTIVE', 'USER'),
('U00029', 'Valea', 'Cristian', '2025-09-01', '2025-09-08', '+40751503251', 'cvaly3918275430987', '$2a$10$l4WMIpuLeE/CS2x1bH38i.fTmisQu3HqsBIun35ObmFFeLW9VHUaS', 'ACTIVE', 'USER');

--
-- Indexuri pentru tabele eliminate
--

--
-- Indexuri pentru tabele `email`
--
ALTER TABLE `email`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idUser_fk` (`idUser`);

--
-- Indexuri pentru tabele `main_objectives`
--
ALTER TABLE `main_objectives`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `idQuestion` (`idQuestion`);

--
-- Indexuri pentru tabele `question`
--
ALTER TABLE `question`
  ADD PRIMARY KEY (`id`,`idQuestion`),
  ADD UNIQUE KEY `uq_question_idQuestion` (`idQuestion`);

--
-- Indexuri pentru tabele `response`
--
ALTER TABLE `response`
  ADD PRIMARY KEY (`idResponse`);

--
-- Indexuri pentru tabele `responses`
--
ALTER TABLE `responses`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UK_ho977cf7w59y05jcud9q1hyxl` (`idResponse`),
  ADD KEY `IdQuestion` (`IdQuestion`);

--
-- Indexuri pentru tabele `secondary_objectives`
--
ALTER TABLE `secondary_objectives`
  ADD PRIMARY KEY (`id`) USING BTREE,
  ADD KEY `idQuestion` (`idQuestion`);

--
-- Indexuri pentru tabele `security_questions`
--
ALTER TABLE `security_questions`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `idUserSec_fk` (`idUser`);

--
-- Indexuri pentru tabele `session`
--
ALTER TABLE `session`
  ADD PRIMARY KEY (`id`,`token`);

--
-- Indexuri pentru tabele `source_code`
--
ALTER TABLE `source_code`
  ADD PRIMARY KEY (`idSourceCode`);

--
-- Indexuri pentru tabele `time_length_question`
--
ALTER TABLE `time_length_question`
  ADD PRIMARY KEY (`id`,`idQuestion`),
  ADD KEY `idQuestion` (`idQuestion`);

--
-- Indexuri pentru tabele `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`idUser`);

--
-- AUTO_INCREMENT pentru tabele eliminate
--

--
-- AUTO_INCREMENT pentru tabele `email`
--
ALTER TABLE `email`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=27;

--
-- AUTO_INCREMENT pentru tabele `main_objectives`
--
ALTER TABLE `main_objectives`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_key', AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pentru tabele `question`
--
ALTER TABLE `question`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_Key', AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pentru tabele `response`
--
ALTER TABLE `response`
  MODIFY `idResponse` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_key', AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pentru tabele `responses`
--
ALTER TABLE `responses`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pentru tabele `secondary_objectives`
--
ALTER TABLE `secondary_objectives`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_key', AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT pentru tabele `security_questions`
--
ALTER TABLE `security_questions`
  MODIFY `question_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT pentru tabele `session`
--
ALTER TABLE `session`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_Key', AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pentru tabele `source_code`
--
ALTER TABLE `source_code`
  MODIFY `idSourceCode` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_key', AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pentru tabele `time_length_question`
--
ALTER TABLE `time_length_question`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary_key', AUTO_INCREMENT=3;

--
-- Constrângeri pentru tabele eliminate
--

--
-- Constrângeri pentru tabele `email`
--
ALTER TABLE `email`
  ADD CONSTRAINT `idUser_fk` FOREIGN KEY (`idUser`) REFERENCES `users` (`idUser`);

--
-- Constrângeri pentru tabele `main_objectives`
--
ALTER TABLE `main_objectives`
  ADD CONSTRAINT `main_objectives_ibfk_1` FOREIGN KEY (`idQuestion`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constrângeri pentru tabele `responses`
--
ALTER TABLE `responses`
  ADD CONSTRAINT `responses_ibfk_1` FOREIGN KEY (`idResponse`) REFERENCES `response` (`idResponse`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `responses_ibfk_2` FOREIGN KEY (`IdQuestion`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constrângeri pentru tabele `secondary_objectives`
--
ALTER TABLE `secondary_objectives`
  ADD CONSTRAINT `secondary_objectives_ibfk_1` FOREIGN KEY (`idQuestion`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constrângeri pentru tabele `security_questions`
--
ALTER TABLE `security_questions`
  ADD CONSTRAINT `idUserSec_fk` FOREIGN KEY (`idUser`) REFERENCES `users` (`idUser`);

--
-- Constrângeri pentru tabele `time_length_question`
--
ALTER TABLE `time_length_question`
  ADD CONSTRAINT `time_length_question_ibfk_1` FOREIGN KEY (`idQuestion`) REFERENCES `question` (`idQuestion`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
