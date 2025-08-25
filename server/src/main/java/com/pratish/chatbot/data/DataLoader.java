package com.pratish.chatbot.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.*;

public class DataLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<Map<String, String>> loadFaqs() {
        try (InputStream is = DataLoader.class.getResourceAsStream("/data/faqs.json")) {
            return MAPPER.readValue(is, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load faqs.json", e);
        }
    }

    public static Map<String, List<String>> loadIntents() {
        try (InputStream is = DataLoader.class.getResourceAsStream("/data/intents.json")) {
            return MAPPER.readValue(is, new TypeReference<Map<String, List<String>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load intents.json", e);
        }
    }

    public static List<String> loadSmalltalk() {
        try (InputStream is = DataLoader.class.getResourceAsStream("/data/smalltalk.json")) {
            return MAPPER.readValue(is, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to load smalltalk.json", e);
        }
    }
}
