package com.pratish.chatbot.model;

public class ChatResponse {
    private String reply;
    private String source;
    private double confidence;
    private String intent;

    public ChatResponse() {}

    public ChatResponse(String reply, String source, double confidence, String intent) {
        this.reply = reply;
        this.source = source;
        this.confidence = confidence;
        this.intent = intent;
    }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getIntent() { return intent; }
    public void setIntent(String intent) { this.intent = intent; }
}
