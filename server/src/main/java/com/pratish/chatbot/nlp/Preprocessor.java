package com.pratish.chatbot.nlp;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
 
public class Preprocessor {
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9\s]");
    private static final Set<String> STOP = new HashSet<>(Arrays.asList(
            "a","an","the","and","or","but","if","then","this","that","to","for","of","on","in","at","by","with",
            "is","am","are","was","were","be","been","do","does","did","it","as","from","your","you","me","my","we","our",
            "i","im","i'm","u","us","they","them","their","he","she","his","her","what","when","where","which","who","how"
    ));

    public static List<String> tokenize(String text) {
        if (text == null) return Collections.emptyList();
        String t = text.toLowerCase(Locale.ROOT);
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\p{M}", "");
        t = NON_ALNUM.matcher(t).replaceAll(" ");
        String[] parts = t.trim().split("\s+");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p.isBlank()) continue;
            if (!STOP.contains(p)) out.add(p);
        }
        return out;
    }
}
