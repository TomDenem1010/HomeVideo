package com.home.video.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.home.common.video.dto.ActorDto;
import com.home.common.video.dto.FolderDto;
import com.home.common.video.dto.VideoDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VideoService {

    private final EntityService entityService;

    public List<VideoDto> findVideos() {
        return entityService.findAllActiveVideos();
    }

    public List<FolderDto> findFolders() {
        return entityService.findAllActiveFolders();
    }

    public List<ActorDto> findActors() {
        return entityService.findAllActiveActors();
    }

    public List<VideoDto> findByFolder(final String folderPath) {
        return entityService.findAllActiveByFolder(folderPath);
    }

    public List<VideoDto> findByActor(final String actorName) {
        return entityService.findAllActiveByActor(actorName);
    }
}
