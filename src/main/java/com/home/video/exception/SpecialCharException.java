package com.home.video.exception;

import com.home.common.exception.HomeException;

public class SpecialCharException extends HomeException {

    public SpecialCharException(String message) {
        super(message);
    }

    public SpecialCharException(String message, Throwable cause) {
        super(message, cause);
    }

}
