package com.pratish.chatbot.service;

import com.pratish.chatbot.data.DataLoader;
import com.pratish.chatbot.model.ChatRequest;
import com.pratish.chatbot.model.ChatResponse;
import com.pratish.chatbot.nlp.IntentLabel;
import com.pratish.chatbot.nlp.NaiveBayesClassifier;
import com.pratish.chatbot.nlp.TfIdfFaqMatcher;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private final Map<String, Map<String, Object>> sessions = new HashMap<>();
    private final NaiveBayesClassifier classifier = new NaiveBayesClassifier();
    private final TfIdfFaqMatcher faqMatcher = new TfIdfFaqMatcher();
    private final List<String> smalltalk = new ArrayList<>();

    // Rule patterns
    private static final Pattern HOURS = Pattern.compile("\b(hours?|open|opening|time)\b");
    private static final Pattern SHIPPING = Pattern.compile("\b(ship|shipping|delivery|deliver|arrive)\b");
    private static final Pattern REFUND = Pattern.compile("\b(refund|return|exchange|replacement)\b");
    private static final Pattern CONTACT = Pattern.compile("\b(contact|email|phone|support|helpdesk)\b");
    private static final Pattern HELLO = Pattern.compile("\b(hi|hello|hey|yo|good\s+(morning|evening|afternoon))\b");
    private static final Pattern BYE = Pattern.compile("\b(bye|goodbye|see\s*ya|cya|later)\b");
    private static final Pattern THANKS = Pattern.compile("\b(thanks|thank\s*you|thx|ty)\b");

    public ChatService() {
        // Load data
        faqMatcher.fit(DataLoader.loadFaqs());
        classifier.fit(DataLoader.loadIntents());
        smalltalk.addAll(DataLoader.loadSmalltalk());
    }

    public ChatResponse reply(ChatRequest req) {
        String msg = req.getMessage();
        String intent = IntentLabel.UNKNOWN.name();

        // 1) Rule-based first (hard intents)
        if (HELLO.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.GREETING.name();
            return new ChatResponse("Hey! How can I help you today?", "rule", 1.0, intent);
        }
        if (THANKS.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.THANKS.name();
            return new ChatResponse("Anytime! If there's more I can do, just say the word.", "rule", 1.0, intent);
        }
        if (BYE.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.GOODBYE.name();
            return new ChatResponse("Goodbye! Have a great day 👋", "rule", 1.0, intent);
        }
        if (HOURS.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.HOURS.name();
            return new ChatResponse("We’re open Mon–Fri, 9am–6pm (EST).", "rule", 1.0, intent);
        }
        if (SHIPPING.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.SHIPPING.name();
            return new ChatResponse("Standard shipping takes 3–5 business days in North America. You’ll get a tracking link by email.", "rule", 1.0, intent);
        }
        if (REFUND.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.REFUND.name();
            return new ChatResponse("We have a 30-day return window. Start a return from your order history, or reply here with your order #.", "rule", 1.0, intent);
        }
        if (CONTACT.matcher(msg.toLowerCase()).find()) {
            intent = IntentLabel.CONTACT.name();
            return new ChatResponse("You can reach support at support@example.com or +1 (555) 123‑4567, Mon–Fri.", "rule", 1.0, intent);
        }

        // 2) ML-enhanced FAQ retrieval
        TfIdfFaqMatcher.MatchResult mr = faqMatcher.bestMatch(msg);
        if (mr.item != null && mr.score >= 0.28) { // tuned threshold
            intent = IntentLabel.FAQ.name();
            return new ChatResponse(mr.item.answer, "faq", mr.score, intent);
        }

        // 3) Naive Bayes fallback for smalltalk/unknown
        String nb = classifier.predictLabel(msg);
        if (nb.equals(IntentLabel.SMALLTALK.name()) || nb.equals(IntentLabel.GREETING.name())) {
            intent = nb;
            String reply = smalltalk.get(new Random().nextInt(smalltalk.size()));
            return new ChatResponse(reply, "smalltalk", 0.6, intent);
        }

        // 4) Ultimate fallback
        return new ChatResponse("I’m not totally sure yet — could you rephrase that or ask about shipping, refunds, hours, or anything else?", "fallback", 0.2, IntentLabel.UNKNOWN.name());
    }
}
