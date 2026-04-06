package com.watch.watch_mall.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.net.ssl.SSLContext;

@Slf4j
@Configuration
@EnableConfigurationProperties(SearchProperties.class)
public class ElasticsearchConfig {

    private RestClient restClient;

    @Bean
    public RestClient restClient(Environment environment) {
        String uri = environment.getProperty("spring.elasticsearch.uris", "https://localhost:9200");
        String username = environment.getProperty("spring.elasticsearch.username");
        String password = environment.getProperty("spring.elasticsearch.password");

        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));

        SSLContext sslContext;
        try {
            sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((certificate, authType) -> true)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("初始化 Elasticsearch SSL 配置失败", e);
        }

        RestClientBuilder builder = RestClient.builder(HttpHost.create(uri))
                .setHttpClientConfigCallback(httpClientBuilder -> configureHttpClient(httpClientBuilder, credentialsProvider, sslContext));
        this.restClient = builder.build();
        return this.restClient;
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        return new RestClientTransport(restClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    @PreDestroy
    public void closeRestClient() {
        if (restClient == null) {
            return;
        }
        try {
            restClient.close();
        } catch (Exception e) {
            log.warn("关闭 Elasticsearch RestClient 失败", e);
        }
    }

    private HttpAsyncClientBuilder configureHttpClient(HttpAsyncClientBuilder httpClientBuilder,
                                                       BasicCredentialsProvider credentialsProvider,
                                                       SSLContext sslContext) {
        return httpClientBuilder
                .setDefaultCredentialsProvider(credentialsProvider)
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
    }
}
