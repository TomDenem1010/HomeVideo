package com.home.video.controller;

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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/video")
@AllArgsConstructor
@Slf4j(topic = "VIDEO")
public class VideoController {

        private final VideoService videoService;

        @PostMapping(value = "/findVideos")
        public HomeResponse<FindVideosResponse> findVideos(
                        final @RequestBody FindVideosRequest homeRequest) {
                log.debug("VideoController::findVideos in: {}", homeRequest);
                var response = new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindVideosResponse(videoService.findVideos()));
                log.debug("VideoController::findVideos out: {}", response);
                return response;
        }

        @PostMapping(value = "/findFolders")
        public HomeResponse<FindFoldersResponse> findFolders(
                        final @RequestBody FindFoldersRequest homeRequest) {
                log.debug("VideoController::findFolders in: {}", homeRequest);
                var response = new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindFoldersResponse(videoService.findFolders()));
                log.debug("VideoController::findFolders out: {}", response);
                return response;
        }

        @PostMapping(value = "/findActors")
        public HomeResponse<FindActorsResponse> findActors(
                        final @RequestBody FindActorsRequest homeRequest) {
                log.debug("VideoController::findActors in: {}", homeRequest);
                var response = new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindActorsResponse(videoService.findActors()));
                log.debug("VideoController::findActors out: {}", response);
                return response;
        }

        @PostMapping(value = "/findByFolder")
        public HomeResponse<FindByFolderResponse> findByFolder(
                        final @RequestBody FindByFolderRequestWrapper homeRequest) {
                log.debug("VideoController::findByFolder in: {}", homeRequest);
                var response = new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindByFolderResponse(
                                                videoService.findByFolder(homeRequest.getRequest().getFolder())));
                log.debug("VideoController::findByFolder out: {}", response);
                return response;
        }

        @PostMapping(value = "/findByActor")
        public HomeResponse<FindByActorResponse> findByActor(
                        final @RequestBody FindByActorRequestWrapper homeRequest) {
                log.debug("VideoController::findByActor in: {}", homeRequest);
                var response = new HomeResponse<>(
                                homeRequest.getHeader(),
                                new FindByActorResponse(videoService.findByActor(homeRequest.getRequest().getName())));
                log.debug("VideoController::findByActor out: {}", response);
                return response;
        }

        @GetMapping(value = "/stream/**")
        public ResponseEntity<ResourceRegion> streamVideo(
                        HttpServletRequest request,
                        @RequestHeader(value = "Range", required = true) String rangeHeader) {
                log.debug("VideoController::streamVideo in: {}", rangeHeader);
                var videoStream = videoService.streamVideo(rangeHeader, request);
                log.debug("VideoController::streamVideo out: {}", videoStream);
                return videoStream;
        }
}
