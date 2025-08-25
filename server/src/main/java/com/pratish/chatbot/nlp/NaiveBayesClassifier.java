package com.pratish.chatbot.nlp;

import java.util.*;

public class NaiveBayesClassifier {
    private final Map<String, Map<String, Integer>> labelWordCounts = new HashMap<>();
    private final Map<String, Integer> labelTotals = new HashMap<>();
    private final Map<String, Integer> labelCounts = new HashMap<>();
    private final Set<String> vocab = new HashSet<>();
    private int totalDocs = 0;
    private double alpha = 1.0; // Laplace smoothing

    public void setAlpha(double alpha) { this.alpha = alpha; }

    public void fit(Map<String, List<String>> labeledUtterances) {
        for (Map.Entry<String, List<String>> e : labeledUtterances.entrySet()) {
            String label = e.getKey();
            List<String> utts = e.getValue();
            labelCounts.put(label, labelCounts.getOrDefault(label,0) + utts.size());
            totalDocs += utts.size();
            Map<String, Integer> counts = labelWordCounts.computeIfAbsent(label, k -> new HashMap<>());
            int total = labelTotals.getOrDefault(label, 0);
            for (String u : utts) {
                for (String tok : Preprocessor.tokenize(u)) {
                    counts.put(tok, counts.getOrDefault(tok, 0) + 1);
                    total += 1;
                    vocab.add(tok);
                }
            }
            labelTotals.put(label, total);
        }
    }

    public String predictLabel(String text) {
        List<String> toks = Preprocessor.tokenize(text);
        if (toks.isEmpty()) return IntentLabel.UNKNOWN.name();

        double best = Double.NEGATIVE_INFINITY;
        String bestLabel = IntentLabel.UNKNOWN.name();
        int V = vocab.size();

        for (String label : labelWordCounts.keySet()) {
            double logPrior = Math.log((labelCounts.getOrDefault(label, 0) + alpha) / (totalDocs + alpha * labelWordCounts.size()));
            Map<String, Integer> counts = labelWordCounts.get(label);
            int total = labelTotals.getOrDefault(label, 0);
            double logLik = 0.0;
            for (String tok : toks) {
                int c = counts.getOrDefault(tok, 0);
                logLik += Math.log((c + alpha) / (total + alpha * V));
            }
            double score = logPrior + logLik;
            if (score > best) {
                best = score;
                bestLabel = label;
            }
        }
        return bestLabel;
    }
}
