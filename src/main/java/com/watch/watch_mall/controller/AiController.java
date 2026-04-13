package com.watch.watch_mall.controller;

import com.watch.watch_mall.service.SupportAiService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Resource
    private SupportAiService supportAiService;

    @GetMapping("/chat")
    public Flux<ServerSentEvent<String>> chat(@RequestParam("chatMemoryId") Integer chatMemoryId,
                                              @RequestParam("userMsg") String userMsg) {
        Flux<String> aiFlux = supportAiService.chatWithFlux(chatMemoryId, userMsg);
        return aiFlux.map(chunk -> ServerSentEvent.builder(chunk).data(chunk).build());
    }
}
