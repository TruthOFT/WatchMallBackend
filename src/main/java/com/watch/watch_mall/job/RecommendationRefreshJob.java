package com.watch.watch_mall.job;

import com.watch.watch_mall.service.RecommendationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RecommendationRefreshJob {

    @Resource
    private RecommendationService recommendationService;

    @Scheduled(cron = "*/10 * * * * *", zone = "Asia/Shanghai")
    public void refreshRecommendationCache() {
        log.info("start refresh recommendation cache");
        recommendationService.refreshAll();
        log.info("finish refresh recommendation cache");
    }
}
