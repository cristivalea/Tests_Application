package com.example.testsapi.service;

import org.springframework.stereotype.Service;

@Service
public class BTKService {

    private static final double P_LEARN = 0.15; // Probabilitatea de a învăța după un test
    private static final double P_GUESS = 0.20; // Probabilitatea de a ghici dacă nu știe
    private static final double P_SLIP = 0.10;  // Probabilitatea de a greși dacă știe
    public double calculateNewMastery(double currentMastery, boolean isCorrect) {
        // 1. Probabilitatea de a cunoaște obiectivul după ce am văzut răspunsul (Bayes)
        double pKnownGivenResponse;
        if (isCorrect) {
            pKnownGivenResponse = (currentMastery * (1 - P_SLIP)) /
                    (currentMastery * (1 - P_SLIP) + (1 - currentMastery) * P_GUESS);
        } else {
            pKnownGivenResponse = (currentMastery * P_SLIP) /
                    (currentMastery * P_SLIP + (1 - currentMastery) * (1 - P_GUESS));
        }

        // 2. Probabilitatea de a cunoaște obiectivul la următorul test (Tranziția)
        return pKnownGivenResponse + (1 - pKnownGivenResponse) * P_LEARN;
    }

}
