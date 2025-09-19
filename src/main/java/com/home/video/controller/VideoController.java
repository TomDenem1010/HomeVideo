package com.home.video.controller;

import java.io.IOException;

import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.home.common.dto.HomeResponse;
import com.home.common.video.dto.findActors.FindActorsRequest;
import com.home.common.video.dto.findActors.FindActorsResponse;
import com.home.common.video.dto.findByActor.FindByActorRequestWrapper;
import com.home.common.video.dto.findByActor.FindByActorResponse;
import com.home.common.video.dto.findByFolder.FindByFolderRequestWrapper;
import com.home.common.video.dto.findByFolder.FindByFolderResponse;
import com.home.common.video.dto.findFolders.FindFoldersRequest;
import com.home.common.video.dto.findFolders.FindFoldersResponse;
import com.home.common.video.dto.findVideos.FindVideosRequest;
import com.home.common.video.dto.findVideos.FindVideosResponse;
import com.home.video.service.VideoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/video")
@AllArgsConstructor
public class VideoController {

        private final VideoService videoService;

        @PostMapping(value = "/findVideos")
        public HomeResponse<FindVideosResponse> findVideos(
                        final @RequestBody FindVideosRequest homeRequest) {
                return new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindVideosResponse(videoService.findVideos()));
        }

        @PostMapping(value = "/findFolders")
        public HomeResponse<FindFoldersResponse> findFolders(
                        final @RequestBody FindFoldersRequest homeRequest) {
                return new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindFoldersResponse(videoService.findFolders()));
        }

        @PostMapping(value = "/findActors")
        public HomeResponse<FindActorsResponse> findActors(
                        final @RequestBody FindActorsRequest homeRequest) {
                return new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindActorsResponse(videoService.findActors()));
        }

        @PostMapping(value = "/findByFolder")
        public HomeResponse<FindByFolderResponse> findByFolder(
                        final @RequestBody FindByFolderRequestWrapper homeRequest) {
                return new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindByFolderResponse(
                                                videoService.findByFolder(homeRequest.getRequest().getFolder())));
        }

        @PostMapping(value = "/findByActor")
        public HomeResponse<FindByActorResponse> findByActor(
                        final @RequestBody FindByActorRequestWrapper homeRequest) {
                return new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindByActorResponse(videoService.findByActor(homeRequest.getRequest().getName())));
        }

        @GetMapping(value = "/stream/**")
        public ResponseEntity<ResourceRegion> streamVideo(
                        HttpServletRequest request,
                        @RequestHeader(value = "Range", required = true) String rangeHeader)
                        throws IOException {
                return videoService.streamVideo(rangeHeader, request);
        }
}
