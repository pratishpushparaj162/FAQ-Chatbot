package com.pratish.chatbot.nlp;

import java.util.*;

public class TfIdfFaqMatcher {
    public static class FaqItem {
        public String question;
        public String answer;
        public Set<String> tokens;
        public Map<String, Double> tfidf;
    }

    private final List<FaqItem> items = new ArrayList<>();
    private final Map<String, Integer> df = new HashMap<>();
    private final Set<String> vocab = new HashSet<>();
    private int N = 0;
    private double[] idf; // parallel to vocabList
    private List<String> vocabList;

    public void fit(List<Map<String, String>> faqData) {
        // Build tokens and DF
        for (Map<String, String> row : faqData) {
            FaqItem it = new FaqItem();
            it.question = row.getOrDefault("question","");
            it.answer = row.getOrDefault("answer","");
            it.tokens = new HashSet<>(Preprocessor.tokenize(it.question));
            items.add(it);
        }
        // DF
        for (FaqItem it : items) {
            Set<String> seen = new HashSet<>();
            for (String tok : it.tokens) {
                if (seen.add(tok)) {
                    df.put(tok, df.getOrDefault(tok, 0) + 1);
                }
                vocab.add(tok);
            }
        }
        N = items.size();
        vocabList = new ArrayList<>(vocab);
        idf = new double[vocabList.size()];
        for (int i = 0; i < vocabList.size(); i++) {
            String w = vocabList.get(i);
            int d = df.getOrDefault(w, 0);
            idf[i] = Math.log((N + 1.0) / (d + 1.0)) + 1.0; // smooth idf
        }
        // Compute TF-IDF vectors for items
        for (FaqItem it : items) {
            Map<String, Double> tf = new HashMap<>();
            for (String tok : it.tokens) tf.put(tok, tf.getOrDefault(tok, 0.0) + 1.0);
            double sum = 0.0;
            Map<String, Double> vec = new HashMap<>();
            for (Map.Entry<String, Double> e : tf.entrySet()) {
                String w = e.getKey();
                int idx = vocabList.indexOf(w);
                double val = e.getValue() * idf[idx];
                vec.put(w, val);
                sum += val * val;
            }
            double norm = Math.sqrt(sum) + 1e-9;
            for (String k : vec.keySet()) vec.put(k, vec.get(k) / norm);
            it.tfidf = vec;
        }
    }

    public MatchResult bestMatch(String query) {
        List<String> toks = Preprocessor.tokenize(query);
        if (toks.isEmpty() || items.isEmpty()) return new MatchResult(null, 0);
        Map<String, Double> tf = new HashMap<>();
        for (String t : toks) tf.put(t, tf.getOrDefault(t, 0.0) + 1.0);

        Map<String, Double> qvec = new HashMap<>();
        double sum = 0.0;
        for (Map.Entry<String, Double> e : tf.entrySet()) {
            String w = e.getKey();
            int idx = vocabList.indexOf(w);
            if (idx < 0) continue;
            double val = e.getValue() * idf[idx];
            qvec.put(w, val);
            sum += val * val;
        }
        double qnorm = Math.sqrt(sum) + 1e-9;
        for (String k : qvec.keySet()) qvec.put(k, qvec.get(k) / qnorm);

        double best = 0.0;
        FaqItem bestItem = null;
        for (FaqItem it : items) {
            double dot = 0.0;
            for (String w : qvec.keySet()) {
                Double a = qvec.get(w);
                Double b = it.tfidf.get(w);
                if (a != null && b != null) dot += a * b;
            }
            if (dot > best) {
                best = dot;
                bestItem = it;
            }
        }
        return new MatchResult(bestItem, best);
    }

    public static class MatchResult {
        public final FaqItem item;
        public final double score;
        public MatchResult(FaqItem item, double score) { this.item = item; this.score = score; }
    }
}
