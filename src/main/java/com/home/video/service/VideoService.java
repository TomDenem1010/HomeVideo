package com.home.video.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.home.common.video.dto.ActorDto;
import com.home.common.video.dto.FolderDto;
import com.home.common.video.dto.VideoDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VideoService {

    private final EntityService entityService;
    private final VideoStreamService videoStreamService;

    @Cacheable("videos")
    public List<VideoDto> findVideos() {
        return entityService.findAllActiveVideos();
    }

    @Cacheable("folders")
    public List<FolderDto> findFolders() {
        return entityService.findAllActiveFolders();
    }

    @Cacheable("actors")
    public List<ActorDto> findActors() {
        return entityService.findAllActiveActors();
    }

    @Cacheable(value = "videosByFolder", key = "#folderPath")
    public List<VideoDto> findByFolder(final String folderPath) {
        return entityService.findAllActiveByFolder(folderPath);
    }

    @Cacheable(value = "videosByActor", key = "#actorName")
    public List<VideoDto> findByActor(final String actorName) {
        return entityService.findAllActiveByActor(actorName);
    }

    public ResponseEntity<ResourceRegion> streamVideo(final String rangeHeader, final HttpServletRequest request) {
        return videoStreamService.streamVideo(rangeHeader, request);
    }
}
