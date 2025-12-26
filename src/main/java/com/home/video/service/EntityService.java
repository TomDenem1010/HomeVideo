package com.home.video.service;

import com.home.common.video.Status;
import com.home.common.video.dao.ActorDao;
import com.home.common.video.dao.FolderDao;
import com.home.common.video.dao.VideoDao;
import com.home.common.video.dto.ActorDto;
import com.home.common.video.dto.FolderDto;
import com.home.common.video.dto.VideoDto;
import com.home.common.video.mapper.ActorMapper;
import com.home.common.video.mapper.FolderMapper;
import com.home.common.video.mapper.VideoMapper;
import com.home.common.video.repository.ActorRepository;
import com.home.common.video.repository.FolderRepository;
import com.home.common.video.repository.VideoRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "VIDEO")
public class EntityService {

  private final FolderRepository folderRepository;
  private final ActorRepository actorRepository;
  private final VideoRepository videoRepository;
  private final FolderMapper folderMapper;
  private final ActorMapper actorMapper;
  private final VideoMapper videoMapper;

  public EntityService(
      final FolderRepository folderRepository,
      final ActorRepository actorRepository,
      final VideoRepository videoRepository,
      final FolderMapper folderMapper,
      final ActorMapper actorMapper,
      final VideoMapper videoMapper) {
    this.folderRepository = folderRepository;
    this.actorRepository = actorRepository;
    this.videoRepository = videoRepository;
    this.folderMapper = folderMapper;
    this.actorMapper = actorMapper;
    this.videoMapper = videoMapper;
  }

  @Transactional
  public void saveVideoDtos(final List<VideoDto> videoDtos) {
    log.debug("EntityService::saveVideoDtos, videoDtos: {}", videoDtos);
    LocalDateTime now = LocalDateTime.now();
    log.debug("EntityService::saveVideoDtos, now: {}", now);
    saveFolderDtos(videoDtos.stream().map(VideoDto::folder).distinct().toList(), now);
    log.debug("EntityService::saveVideoDtos, folders saved at: {}", now);
    saveActorDtos(
        videoDtos.stream().flatMap(videoDto -> videoDto.actors().stream()).distinct().toList(),
        now);
    log.debug("EntityService::saveVideoDtos, actors saved at: {}", now);
    saveVideoDtos(videoDtos, now);
    log.debug("EntityService::saveVideoDtos, videos saved at: {}", now);
  }

  public List<VideoDto> findAllActiveVideos() {
    log.debug("EntityService::findAllActiveVideos in");
    var response =
        videoRepository.findAllByStatus(Status.ACTIVE.toString()).stream()
            .map(videoMapper::toDto)
            .toList();
    log.debug("EntityService::findAllActiveVideos out size: {}", response.size());
    return response;
  }

  public List<FolderDto> findAllActiveFolders() {
    log.debug("EntityService::findAllActiveFolders in");
    var response =
        folderRepository.findAllByStatus(Status.ACTIVE.toString()).stream()
            .map(folderMapper::toDto)
            .toList();
    log.debug("EntityService::findAllActiveFolders out size: {}", response.size());
    return response;
  }

  public List<ActorDto> findAllActiveActors() {
    log.debug("EntityService::findAllActiveActors in");
    var response =
        actorRepository.findAllByStatus(Status.ACTIVE.toString()).stream()
            .map(actorMapper::toDto)
            .toList();
    log.debug("EntityService::findAllActiveActors out size: {}", response.size());
    return response;
  }

  public List<VideoDto> findAllActiveByFolder(final String folderPath) {
    log.debug("EntityService::findAllActiveByFolder in");
    var response =
        videoRepository
            .findAllByStatusAndFolderId(
                Status.ACTIVE.toString(), folderRepository.findByPath(folderPath).get().getId())
            .stream()
            .map(videoMapper::toDto)
            .toList();
    log.debug("EntityService::findAllActiveByFolder out size: {}", response.size());
    return response;
  }

  public List<VideoDto> findAllActiveByActor(final String actorName) {
    log.debug("EntityService::findAllActiveByActor in");
    var response =
        videoRepository
            .findAllByStatusAndActorId(
                Status.ACTIVE.toString(), actorRepository.findByName(actorName).get().getId())
            .stream()
            .map(videoMapper::toDto)
            .toList();
    log.debug("EntityService::findAllActiveByActor out size: {}", response.size());
    return response;
  }

  private void saveFolderDtos(final List<FolderDto> folderDtos, final LocalDateTime now) {
    log.debug("EntityService::saveFolderDtos, now: {}", now);
    folderRepository.updateAllFolderStatusByStatus(
        Status.READING.toString(), Status.ACTIVE.toString());
    List<FolderDao> folderDaos = folderDtos.stream().map(folderMapper::toEntity).toList();

    folderDaos.forEach(
        folderDao -> {
          Optional<FolderDao> existingFolderDao = folderRepository.findByPath(folderDao.getPath());
          if (existingFolderDao.isPresent()
              && existingFolderDao.get().getStatus() == Status.READING) {
            log.debug(
                "EntityService::saveFolderDtos, READING -> ACTIVE, id: {}",
                existingFolderDao.get().getId());
            folderRepository.updateFolderStatus(
                Status.ACTIVE.toString(), existingFolderDao.get().getId());
          } else if (existingFolderDao.isPresent()
              && existingFolderDao.get().getStatus() == Status.INACTIVE) {
            log.debug(
                "EntityService::saveFolderDtos, INACTIVE -> ACTIVE, id: {}",
                existingFolderDao.get().getId());
            folderRepository.updateFolderStatusAndUpdatedAt(
                Status.ACTIVE.toString(), now, existingFolderDao.get().getId());
          } else {
            log.debug("EntityService::saveFolderDtos, new folder, path: {}", folderDao.getPath());
            folderRepository.save(folderDao);
          }
        });

    log.debug("EntityService::saveFolderDtos, READING -> INACTIVE, now: {}", now);
    folderRepository.updateAllFolderStatusAndUpdatedAtByStatus(
        Status.INACTIVE.toString(), now, Status.READING.toString());
  }

  private void saveActorDtos(final List<ActorDto> actorDtos, final LocalDateTime now) {
    log.debug("EntityService::saveActorDtos, now: {}", now);
    actorRepository.updateAllActorStatusByStatus(
        Status.READING.toString(), Status.ACTIVE.toString());
    List<ActorDao> actorDaos = actorDtos.stream().map(actorMapper::toEntity).toList();

    actorDaos.forEach(
        actorDao -> {
          Optional<ActorDao> existingActorDao = actorRepository.findByName(actorDao.getName());
          if (existingActorDao.isPresent()
              && existingActorDao.get().getStatus() == Status.READING) {
            log.debug(
                "EntityService::saveActorDtos, READING -> ACTIVE, id: {}",
                existingActorDao.get().getId());
            actorRepository.updateActorStatus(
                Status.ACTIVE.toString(), existingActorDao.get().getId());
          } else if (existingActorDao.isPresent()
              && existingActorDao.get().getStatus() == Status.INACTIVE) {
            log.debug(
                "EntityService::saveActorDtos, INACTIVE -> ACTIVE, id: {}",
                existingActorDao.get().getId());
            actorRepository.updateActorStatusAndUpdatedAt(
                Status.ACTIVE.toString(), now, existingActorDao.get().getId());
          } else {
            log.debug("EntityService::saveActorDtos, new actor, name: {}", actorDao.getName());
            actorRepository.save(actorDao);
          }
        });

    log.debug("EntityService::saveActorDtos, READING -> INACTIVE, now: {}", now);
    actorRepository.updateAllActorStatusAndUpdatedAtByStatus(
        Status.INACTIVE.toString(), now, Status.READING.toString());
  }

  private void saveVideoDtos(final List<VideoDto> videoDtos, final LocalDateTime now) {
    log.debug("EntityService::saveVideoDtos, now: {}", now);
    videoRepository.updateAllVideoStatusByStatus(
        Status.READING.toString(), Status.ACTIVE.toString());
    List<VideoDao> videoDaos = videoDtos.stream().map(videoMapper::toEntity).toList();

    videoDaos.forEach(
        videoDao -> {
          Optional<VideoDao> existingVideoDao =
              videoRepository.findByFolderIdAndName(videoDao.getFolderId(), videoDao.getName());
          if (existingVideoDao.isPresent()
              && existingVideoDao.get().getStatus() == Status.READING) {
            log.debug(
                "EntityService::saveVideoDtos, READING -> ACTIVE, id: {}",
                existingVideoDao.get().getId());
            videoRepository.updateVideoStatus(
                Status.ACTIVE.toString(), existingVideoDao.get().getId());
          } else if (existingVideoDao.isPresent()
              && existingVideoDao.get().getStatus() == Status.INACTIVE) {
            log.debug(
                "EntityService::saveVideoDtos, INACTIVE -> ACTIVE, id: {}",
                existingVideoDao.get().getId());
            videoRepository.updateVideoStatusAndUpdatedAt(
                Status.ACTIVE.toString(), now, existingVideoDao.get().getId());
          } else {
            log.debug("EntityService::saveVideoDtos, new video, name: {}", videoDao.getName());
            videoRepository.save(videoDao);
          }
        });

    log.debug("EntityService::saveVideoDtos, READING -> INACTIVE, now: {}", now);
    videoRepository.updateAllVideoStatusAndUpdatedAtByStatus(
        Status.INACTIVE.toString(), now, Status.READING.toString());
  }
}
