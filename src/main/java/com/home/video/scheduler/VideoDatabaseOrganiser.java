package com.home.video.scheduler;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.home.common.video.dto.VideoDto;
import com.home.video.service.EntityService;
import com.home.video.service.FileFinder;

@Component
@ConditionalOnProperty(name = "video.database.organiser.enabled", havingValue = "true", matchIfMissing = false)
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

    @Scheduled(fixedRateString = "${video.database.organiser.rate}")
    public void run() {
        try {
            List<VideoDto> videos = fileFinder.findVideosByPath(path);
            entityService.saveVideoDtos(videos);
            System.out.println(videos.size());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
