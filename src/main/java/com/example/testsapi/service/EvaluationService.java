package com.example.testsapi.service;

import com.example.testsapi.entity.ObjectiveAnalysis;
import com.example.testsapi.entity.TestDuration;
import com.example.testsapi.entity.TestResults;
import weka.clusterers.SimpleKMeans;
import weka.core.*;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private List<String> extractObjectivesFromId(String idTest) {
        if (idTest == null || !idTest.contains("_")) return Collections.emptyList();
        String[] parts = idTest.split("_");
        List<String> objs = new ArrayList<>();
        for (int i = 1; i < parts.length - 1; i++) {
            objs.add(parts[i].replace("OB", ""));
        }
        return objs;
    }

    private Instances createDataset(Map<String, double[]> userAverages) {
        ArrayList<Attribute> attrs = new ArrayList<>();
        attrs.add(new Attribute("punctaj"));
        attrs.add(new Attribute("timp"));
        attrs.add(new Attribute("dificultate"));

        Instances dataset = new Instances("ClusterData", attrs, userAverages.size());
        for (double[] values : userAverages.values()) {
            dataset.add(new DenseInstance(1.0, values));
        }
        return dataset;
    }

    private String mapNumToDifficulty(double diff) {
        if (diff >= 2.5) return "Hard";
        if (diff >= 1.5) return "Medium";
        return "Easy";
    }

    public List<ObjectiveAnalysis> computeDetailedCluster(String targetUserId,
                                                          List<TestResults> allResults,
                                                          List<TestDuration> allDurations) throws Exception {

        Map<String, TestDuration> durationMap = allDurations.stream()
                .collect(Collectors.toMap(d -> d.getIdTest() + d.getIdUser(), d -> d, (a, b) -> a));

        Map<String, Map<String, List<double[]>>> objectiveData = new HashMap<>();

        for (TestResults res : allResults) {
            TestDuration dur = durationMap.get(res.getIdTest() + res.getIdUser());
            if (dur == null) continue;

            List<String> objs = extractObjectivesFromId(res.getIdTest());
            double diffNum = mapDifficultyToNum(dur.getDifficulty());

            for (String obj : objs) {
                objectiveData.computeIfAbsent(obj, k -> new HashMap<>())
                        .computeIfAbsent(res.getIdUser(), k -> new ArrayList<>())
                        .add(new double[]{res.getPunctaj(), (double)dur.getTimeTakenSeconds(), diffNum});
            }
        }

        List<ObjectiveAnalysis> finalReport = new ArrayList<>();

        for (String obj : objectiveData.keySet()) {
            Map<String, List<double[]>> userEntries = objectiveData.get(obj);

            if (!userEntries.containsKey(targetUserId)) continue;

            Map<String, double[]> userAverages = new HashMap<>();
            userEntries.forEach((uid, list) -> {
                double avgPunctaj = list.stream().mapToDouble(d -> d[0]).average().orElse(0);
                double avgTimp = list.stream().mapToDouble(d -> d[1]).average().orElse(0);
                double avgDiff = list.stream().mapToDouble(d -> d[2]).average().orElse(0);
                userAverages.put(uid, new double[]{avgPunctaj, avgTimp, avgDiff});
            });

            double[] targetStats = userAverages.get(targetUserId);
            String nivel;

            if (userAverages.size() >= 4) {
                Instances dataset = createDataset(userAverages);
                SimpleKMeans kmeans = new SimpleKMeans();
                kmeans.setNumClusters(4);
                kmeans.buildClusterer(dataset);

                Instance userInst = new DenseInstance(1.0, targetStats);
                userInst.setDataset(dataset);
                int clusterIdx = kmeans.clusterInstance(userInst);

                nivel = labelByKMeansCentroid(kmeans, clusterIdx);
            } else {
                nivel = labelByFixedThresholds(targetStats[0]);
            }

            finalReport.add(new ObjectiveAnalysis(
                    obj,
                    nivel,
                    targetStats[0],
                    targetStats[1],
                    "At the level " + mapNumToDifficulty(targetStats[2])
            ));
        }
        return finalReport;
    }
    private String labelByFixedThresholds(double score) {
        if (score >= 85) return "Excellent";
        if (score >= 70) return "Advanced";
        if (score >= 50) return "Medium";
        return "Beginner";
    }

    private String labelByKMeansCentroid(SimpleKMeans kmeans, int clusterIdx) throws Exception {
        Instances centroids = kmeans.getClusterCentroids();
        double clusterAvgScore = centroids.instance(clusterIdx).value(0);

        return labelByFixedThresholds(clusterAvgScore);
    }

    private double mapDifficultyToNum(String diff) {
        if (diff == null) return 1.0;
        return switch (diff.toLowerCase()) {
            case "hard" -> 3.0;
            case "medium" -> 2.0;
            default -> 1.0;
        };
    }

    public ObjectiveAnalysis computeGeneralCluster(String targetUserId,
                                                 List<TestResults> allResults,
                                                 List<TestDuration> allDurations) throws Exception {


        Map<String, TestDuration> durationMap = allDurations.stream()
                .collect(Collectors.toMap(d -> d.getIdTest() + d.getIdUser(), d -> d, (a, b) -> a));


        Map<String, List<double[]>> globalUserData = new HashMap<>();

        for (TestResults res : allResults) {
            TestDuration dur = durationMap.get(res.getIdTest() + res.getIdUser());
            if (dur == null) continue;

            double diffNum = mapDifficultyToNum(dur.getDifficulty());
            globalUserData.computeIfAbsent(res.getIdUser(), k -> new ArrayList<>())
                    .add(new double[]{res.getPunctaj(), (double)dur.getTimeTakenSeconds(), diffNum});
        }

        Map<String, double[]> userAverages = new HashMap<>();
        globalUserData.forEach((uid, list) -> {
            double avgP = list.stream().mapToDouble(d -> d[0]).average().orElse(0);
            double avgT = list.stream().mapToDouble(d -> d[1]).average().orElse(0);
            double avgD = list.stream().mapToDouble(d -> d[2]).average().orElse(0);
            userAverages.put(uid, new double[]{avgP, avgT, avgD});
        });

        if (!userAverages.containsKey(targetUserId)) return null;

        double[] targetStats = userAverages.get(targetUserId);
        String nivel;


        if (userAverages.size() >= 4) {
            Instances dataset = createDataset(userAverages);
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setNumClusters(4);
            kmeans.buildClusterer(dataset);

            Instance userInst = new DenseInstance(1.0, targetStats);
            userInst.setDataset(dataset);
            int clusterIdx = kmeans.clusterInstance(userInst);
            nivel = labelByKMeansCentroid(kmeans, clusterIdx);
        } else {
            nivel = labelByFixedThresholds(targetStats[0]);
        }

        return new ObjectiveAnalysis(
                targetUserId,
                nivel,
                targetStats[0],
                targetStats[1],
                "General development " + mapNumToDifficulty(targetStats[2])
        );
    }
}