package com.home.video.exception;

import com.home.common.exception.HomeException;

public class FileNotFoundException extends HomeException {

    private static final String INVALID_VIDEO_PATH = "Invalid video path: ";

    public FileNotFoundException(String path) {
        super(INVALID_VIDEO_PATH + path);
    }

    public FileNotFoundException(String path, Throwable cause) {
        super(INVALID_VIDEO_PATH + path, cause);
    }

}
