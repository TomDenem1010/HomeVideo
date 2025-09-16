package com.home.video.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import com.home.common.video.Status;
import com.home.common.video.dto.ActorDto;
import com.home.common.video.dto.FolderDto;
import com.home.common.video.dto.VideoDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FileFinder {

    private VideoNameSplitter videoNameSplitter;

    public List<VideoDto> findVideosByPath(final String path) {
        List<VideoDto> response = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        try (Stream<Path> paths = Files.walk(Paths.get(path))) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(
                            actualPath -> {
                                Map<String, Object> splittedName = videoNameSplitter
                                        .split(actualPath.getFileName().toString());
                                response.add(
                                        new VideoDto(
                                                null,
                                                getFolderDto(actualPath, now),
                                                splittedName.get(VideoNameSplitter.NAME_KEY).toString(),
                                                getSize(actualPath),
                                                getActorDtos(
                                                        (List<String>) splittedName.get(VideoNameSplitter.ACTORS_KEY),
                                                        now),
                                                Status.ACTIVE,
                                                null,
                                                null));
                            });
        } catch (Exception ignored) {
        }

        return response;
    }

    public FileSystemResource findFileSystemResourceByPath(final String path) {
        FileSystemResource fileSystemResource = getFileSystemResource(path);
        if (!fileSystemResource.exists()) {
            throw new RuntimeException(path);
        }
        return fileSystemResource;
    }

    private FileSystemResource getFileSystemResource(final String path) {
        try {
            return new FileSystemResource(new File(path).getCanonicalFile());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private FolderDto getFolderDto(final Path path, final LocalDateTime now) {
        return new FolderDto(
                null,
                getParentName(path),
                Status.ACTIVE,
                now,
                now);
    }

    private List<ActorDto> getActorDtos(final List<String> actorNames, final LocalDateTime now) {
        List<ActorDto> actorDtos = new ArrayList<>();
        for (String actorName : actorNames) {
            actorDtos.add(new ActorDto(null, actorName, Status.ACTIVE, now, now));
        }
        return actorDtos;
    }

    private String getSize(final Path path) {
        String size = "";

        try {
            size = String.format("%.2f GB", Files.size(path) / (1024.0 * 1024.0 * 1024.0));
        } catch (Exception ignored) {
        }

        return size;
    }

    private String getParentName(final Path path) {
        return path.getParent().toString().replace("\\", "/");
    }
}
