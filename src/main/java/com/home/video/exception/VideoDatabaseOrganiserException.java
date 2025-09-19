package com.home.video.exception;

import com.home.common.exception.HomeException;

public class VideoDatabaseOrganiserException extends HomeException {

    public VideoDatabaseOrganiserException(String message) {
        super(message);
    }

    public VideoDatabaseOrganiserException(String message, Throwable cause) {
        super(message, cause);
    }
}
