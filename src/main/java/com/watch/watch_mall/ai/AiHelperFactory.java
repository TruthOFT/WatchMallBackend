package com.watch.watch_mall.ai;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiHelperFactory {

    @Resource
    ChatModel chatModel;

    @Resource
    ContentRetriever contentRetriever;

    @Resource
    StreamingChatModel streamingChatModel;

    @Bean
    public AiAssistService aiAssistService() {
        ChatMemory messageWindowChatMemory = MessageWindowChatMemory.withMaxMessages(30);
        return AiServices.builder(AiAssistService.class)
                .chatModel(chatModel)
                .chatMemory(messageWindowChatMemory)
                .contentRetriever(contentRetriever)
                .streamingChatModel(streamingChatModel) // 流式响应
                .chatMemoryProvider(memoryId -> messageWindowChatMemory)
                .build();
    }
}
