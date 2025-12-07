package com.home.video.service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.home.video.exception.FileNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "VIDEO")
public class VideoStreamService {

    private final FileFinder fileFinder;
    private final String basePath;

    public VideoStreamService(
            final FileFinder fileFinder,
            final @Value("${video.path}") String basePath) {
        this.fileFinder = fileFinder;
        this.basePath = basePath;
    }

    public ResponseEntity<ResourceRegion> streamVideo(final String rangeHeader, final HttpServletRequest request) {
        String path = getPathToStream(request);
        FileSystemResource videoResource = fileFinder.findFileSystemResourceByPath(path);
        long contentLength = getContentLength(videoResource);
        List<HttpRange> httpRanges = rangeHeader != null ? HttpRange.parseRanges(rangeHeader) : List.of();
        ResourceRegion region;

        if (!httpRanges.isEmpty()) {
            HttpRange range = httpRanges.get(0);
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1);
            region = new ResourceRegion(videoResource, start, rangeLength);
        } else {
            long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
            region = new ResourceRegion(videoResource, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(videoResource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    private long getContentLength(FileSystemResource videoResource) {
        try {
            return videoResource.contentLength();
        } catch (IOException e) {
            log.error("VideoStreamService::getContentLength, exception: ", e);
            return 0;
        }
    }

    private String getPathToStream(final HttpServletRequest request) {
        String path = URLDecoder.decode(URLDecoder.decode(
                request
                        .getRequestURI()
                        .substring(request.getRequestURI().indexOf("/stream/") + "/stream/".length()),
                StandardCharsets.UTF_8),
                StandardCharsets.UTF_8).replace("%20", " ");
        checkBasePath(path);
        return path;
    }

    private void checkBasePath(final String path) {
        if (!path.startsWith(basePath)) {
            log.error("VideoStreamService::checkBasePath, invalid path: {}", path);
            throw new FileNotFoundException(path);
        }
    }
}
