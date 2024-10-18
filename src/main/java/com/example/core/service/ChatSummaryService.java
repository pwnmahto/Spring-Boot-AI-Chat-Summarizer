package com.example.core.service;

import com.example.core.model.ChatMessage;

import java.util.List;

public interface ChatSummaryService {

    String summarize(List<ChatMessage> messages);
}