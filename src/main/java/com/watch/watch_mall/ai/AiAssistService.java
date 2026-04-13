package com.watch.watch_mall.ai;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiAssistService {

    @SystemMessage(fromResource = "prompt.txt")
    String chat(String message);

    @SystemMessage(fromResource = "prompt.txt")
    Report chatWithFormat(String message);

    @SystemMessage(fromResource = "prompt.txt")
    Result<String> chatWithRag(String message);

    record Report(String name, List<String> messages) {
    };

    @SystemMessage(fromResource = "prompt.txt")
    Flux<String> chatWithFlux(@MemoryId int memoryId, @UserMessage String userMsg);

}
