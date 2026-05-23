package com.watch.watch_mall.ai;

import dev.langchain4j.http.client.spring.restclient.SpringRestClient;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class SiliconFlowEmbeddingConfig {

    @Bean
    public EmbeddingModel embeddingModel(
            @Value("${siliconflow.api-key}") String apiKey,
            @Value("${siliconflow.embedding-model}") String modelName,
            @Value("${siliconflow.base-url}") String baseUrl
    ) {
        System.out.println("当前使用的 embedding model = " + modelName);

        return OpenAiEmbeddingModel.builder()
                .httpClientBuilder(
                        SpringRestClient.builder()
                                .restClientBuilder(RestClient.builder())
                )
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(60))
                .logRequests(true)
                .logResponses(true)
                .build();
    }
}