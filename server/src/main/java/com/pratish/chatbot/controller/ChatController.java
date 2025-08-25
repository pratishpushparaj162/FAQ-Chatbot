package com.pratish.chatbot.controller;

import com.pratish.chatbot.model.ChatRequest;
import com.pratish.chatbot.model.ChatResponse;
import com.pratish.chatbot.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(path = "/message", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ChatResponse sendMessage(@Valid @RequestBody ChatRequest request) {
        return chatService.reply(request);
    }
}
