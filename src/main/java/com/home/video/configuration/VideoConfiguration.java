package com.home.video.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.home.common.video.mapper.ActorMapper;
import com.home.common.video.mapper.FolderMapper;
import com.home.common.video.mapper.VideoMapper;
import com.home.common.video.repository.ActorRepository;
import com.home.common.video.repository.FolderRepository;
import com.home.common.video.repository.VideoRepository;
import com.home.video.service.EntityService;

@Configuration
public class VideoConfiguration {

    @Bean
    FolderMapper folderMapper() {
        return new FolderMapper();
    }

    @Bean
    ActorMapper actorMapper() {
        return new ActorMapper();
    }

    @Bean
    VideoMapper videoMapper(
            final FolderRepository folderRepository,
            final ActorRepository actorRepository,
            final FolderMapper folderMapper,
            final ActorMapper actorMapper) {
        return new VideoMapper(folderRepository, actorRepository, folderMapper, actorMapper);
    }

    @Bean
    EntityService entityService(
            final FolderMapper folderMapper,
            final FolderRepository folderRepository,
            final ActorRepository actorRepository,
            final VideoRepository videoRepository,
            final ActorMapper actorMapper,
            final VideoMapper videoMapper) {
        return new EntityService(
                folderRepository,
                actorRepository,
                videoRepository,
                folderMapper,
                actorMapper,
                videoMapper);
    }
}
