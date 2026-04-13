package com.watch.watch_mall.service.impl;

import com.watch.watch_mall.ai.AiAssistService;
import com.watch.watch_mall.service.SupportAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class SupportAiServiceImpl implements SupportAiService {

    private final ObjectProvider<AiAssistService> aiAssistServiceProvider;

    public SupportAiServiceImpl(ObjectProvider<AiAssistService> aiAssistServiceProvider) {
        this.aiAssistServiceProvider = aiAssistServiceProvider;
    }

    @Override
    public String chat(Long ticketId, String message) {
        AiAssistService aiAssistService = aiAssistServiceProvider.getIfAvailable();
        if (aiAssistService == null) {
            return "AI 客服当前未完成模型配置，已为您记录问题，请先通过人工客服继续沟通。";
        }
        try {
            return aiAssistService.chat(message);
        } catch (Exception e) {
            log.error("support ai chat failed, ticketId={}", ticketId, e);
            return "AI 客服暂时不可用，已建议您转人工客服继续处理。";
        }
    }

    @Override
    public Flux<String> chatWithFlux(Integer chatMemoryId, String message) {
        AiAssistService aiAssistService = aiAssistServiceProvider.getIfAvailable();
        if (aiAssistService == null) {
            return Flux.just("AI 客服当前未完成模型配置，请稍后再试。");
        }
        try {
            return aiAssistService.chatWithFlux(chatMemoryId == null ? 0 : chatMemoryId, message);
        } catch (Exception e) {
            log.error("support ai stream chat failed, chatMemoryId={}", chatMemoryId, e);
            return Flux.just("AI 客服暂时不可用，请稍后再试。");
        }
    }
}
