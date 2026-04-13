package com.watch.watch_mall.ai;


import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiAssist {

    @Resource
    private ChatModel chatModel;

    public String chat(String message) {
        UserMessage userMessage = UserMessage.from(message);
        ChatResponse chat = chatModel.chat(userMessage);
        AiMessage aiMessage = chat.aiMessage();
        return aiMessage.text();
    }
}
