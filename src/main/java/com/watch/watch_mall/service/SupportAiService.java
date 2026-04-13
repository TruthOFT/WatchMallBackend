package com.watch.watch_mall.service;

import reactor.core.publisher.Flux;

public interface SupportAiService {

    String chat(Long ticketId, String message);

    Flux<String> chatWithFlux(Integer chatMemoryId, String message);
}
