package com.home.video.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.home.common.video.Status;
import com.home.common.video.dao.ActorDao;
import com.home.common.video.dao.FolderDao;
import com.home.common.video.dto.ActorDto;
import com.home.common.video.dto.FolderDto;
import com.home.common.video.dto.VideoDto;
import com.home.common.video.mapper.ActorMapper;
import com.home.common.video.mapper.FolderMapper;
import com.home.common.video.repository.ActorRepository;
import com.home.common.video.repository.FolderRepository;
import com.home.common.video.repository.VideoRepository;

import jakarta.transaction.Transactional;

public class EntityService {

    private final FolderRepository folderRepository;
    private final ActorRepository actorRepository;
    private final VideoRepository videoRepository;
    private final FolderMapper folderMapper;
    private final ActorMapper actorMapper;

    public EntityService(
            final FolderRepository folderRepository,
            final ActorRepository actorRepository,
            final VideoRepository videoRepository,
            final FolderMapper folderMapper,
            final ActorMapper actorMapper) {
        this.folderRepository = folderRepository;
        this.actorRepository = actorRepository;
        this.videoRepository = videoRepository;
        this.folderMapper = folderMapper;
        this.actorMapper = actorMapper;
    }

    @Transactional
    public void saveVideoDtos(final List<VideoDto> videoDtos) {
        LocalDateTime now = LocalDateTime.now();
        saveFolderDtos(
                videoDtos.stream()
                        .map(VideoDto::folder)
                        .distinct()
                        .toList(),
                now);
        saveActorDtos(
                videoDtos.stream()
                        .flatMap(videoDto -> videoDto.actors().stream())
                        .distinct()
                        .toList(),
                now);
    }

    private void saveFolderDtos(final List<FolderDto> folderDtos, final LocalDateTime now) {
        folderRepository.updateAllFolderStatusByStatus(Status.READING.toString(), Status.ACTIVE.toString());
        List<FolderDao> folderDaos = folderDtos.stream()
                .map(folderMapper::toEntity)
                .toList();

        folderDaos.forEach(folderDao -> {
            Optional<FolderDao> existingFolderDao = folderRepository.findByPath(folderDao.getPath());
            if (existingFolderDao.isPresent() && existingFolderDao.get().getStatus() == Status.READING) {
                folderRepository.updateFolderStatus(
                        Status.ACTIVE.toString(),
                        existingFolderDao.get().getId());
            } else if (existingFolderDao.isPresent() && existingFolderDao.get().getStatus() == Status.INACTIVE) {
                folderRepository.updateFolderStatusAndUpdatedAt(
                        Status.ACTIVE.toString(),
                        now,
                        existingFolderDao.get().getId());
            } else {
                folderRepository.save(folderDao);
            }
        });

        folderRepository.updateAllFolderStatusAndUpdatedAtByStatus(
                Status.INACTIVE.toString(),
                now,
                Status.READING.toString());
    }

    private void saveActorDtos(final List<ActorDto> actorDtos, final LocalDateTime now) {
        actorRepository.updateAllActorStatusByStatus(Status.READING.toString(), Status.ACTIVE.toString());
        List<ActorDao> actorDaos = actorDtos.stream()
                .map(actorMapper::toEntity)
                .toList();

        actorDaos.forEach(actorDao -> {
            Optional<ActorDao> existingActorDao = actorRepository.findByName(actorDao.getName());
            if (existingActorDao.isPresent() && existingActorDao.get().getStatus() == Status.READING) {
                actorRepository.updateActorStatus(
                        Status.ACTIVE.toString(),
                        existingActorDao.get().getId());
            } else if (existingActorDao.isPresent() && existingActorDao.get().getStatus() == Status.INACTIVE) {
                actorRepository.updateActorStatusAndUpdatedAt(
                        Status.ACTIVE.toString(),
                        now,
                        existingActorDao.get().getId());
            } else {
                actorRepository.save(actorDao);
            }
        });

        actorRepository.updateAllActorStatusAndUpdatedAtByStatus(
                Status.INACTIVE.toString(),
                now,
                Status.READING.toString());
    }
}
