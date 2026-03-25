package com.watch.watch_mall.config;

import io.swagger.v3.oas.models.media.StringSchema;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    static {
        SpringDocUtils.getConfig()
                .replaceWithSchema(Long.class, new StringSchema())
                .replaceWithSchema(Long.TYPE, new StringSchema());
    }
}
