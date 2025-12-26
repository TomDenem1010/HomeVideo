package com.home.video.scheduler;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.home.common.video.dto.VideoDto;
import com.home.video.exception.VideoDatabaseOrganiserException;
import com.home.video.service.EntityService;
import com.home.video.service.FileFinder;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "video.database.organiser.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j(topic = "VIDEO")
public class VideoDatabaseOrganiser {

    private final FileFinder fileFinder;
    private final EntityService entityService;
    private final String path;

    public VideoDatabaseOrganiser(
            final FileFinder fileFinder,
            final EntityService entityService,
            final @Value("${video.path}") String path) {
        this.fileFinder = fileFinder;
        this.entityService = entityService;
        this.path = path;
    }

    @Caching(evict = {
            @CacheEvict(value = "videos", allEntries = true),
            @CacheEvict(value = "folders", allEntries = true),
            @CacheEvict(value = "actors", allEntries = true),
            @CacheEvict(value = "videosByFolder", allEntries = true),
            @CacheEvict(value = "videosByActor", allEntries = true)
    })
    @Scheduled(fixedRateString = "${video.database.organiser.rate}")
    public void run() {
        try {
            log.info("VideoDatabaseOrganiser::run in");
            List<VideoDto> videos = fileFinder.findVideosByPath(path);
            if (Objects.nonNull(videos) && !videos.isEmpty()) {
                entityService.saveVideoDtos(videos);
            }
            log.info("VideoDatabaseOrganiser::run out: {}", videos.size());
        } catch (Exception exception) {
            log.error("VideoDatabaseOrganiser::run error: {}", exception.getMessage());
            throw new VideoDatabaseOrganiserException("Error occurred while organizing video database", exception);
        }
    }
}
