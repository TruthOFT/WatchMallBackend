package com.watch.watch_mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "search")
public class SearchProperties {

    private String productIndex = "product_search";
}
