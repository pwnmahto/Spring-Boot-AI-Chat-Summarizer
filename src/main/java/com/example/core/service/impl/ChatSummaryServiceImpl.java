package com.example.core.service.impl;

import com.example.core.model.ChatMessage;
import com.example.core.service.ChatSummaryService;
import lombok.Setter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Service
public class ChatSummaryServiceImpl implements ChatSummaryService {

    private ChatClient chatClient;

    public String summarize(List<ChatMessage> messages) {
       return null;
    }
}
