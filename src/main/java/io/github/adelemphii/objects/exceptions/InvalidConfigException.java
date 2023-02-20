package io.github.adelemphii.objects.exceptions;

import lombok.Getter;

import java.io.File;

public class InvalidConfigException extends Exception {

    @Getter
    private final File file;

    public InvalidConfigException(String message, File file) {
        super(message);
        this.file = file;
    }
}
