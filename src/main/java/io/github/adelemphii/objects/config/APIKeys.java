package io.github.adelemphii.objects.config;

import io.github.adelemphii.objects.exceptions.InvalidConfigException;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class APIKeys {

    // API Keys
    @Getter @Setter
    private String picoKey;
    @Getter @Setter
    private String openAIKey;
    @Getter @Setter
    private String googleCloudStorageKey;
    @Getter @Setter
    private String googleCloudPlatformKey;

    public APIKeys(String picoKey, String openAIKey, String googleCloudStorageKey, String googleCloudPlatformKey) {
        this.picoKey = picoKey;
        this.openAIKey = openAIKey;
        this.googleCloudStorageKey = googleCloudStorageKey;
        this.googleCloudPlatformKey = googleCloudPlatformKey;
    }

    public APIKeys() {

    }

    public void validate(File configFile) throws InvalidConfigException {
        if(picoKey == null || picoKey.isEmpty()) {
            throw new InvalidConfigException("Pico Key is not set", configFile);
        }
        if(openAIKey == null || openAIKey.isEmpty()) {
            throw new InvalidConfigException("OpenAI Key is not set", configFile);
        }
        if(googleCloudStorageKey == null || googleCloudStorageKey.isEmpty()) {
            throw new InvalidConfigException("Google Cloud Storage Key is not set", configFile);
        }
        if(googleCloudPlatformKey == null || googleCloudPlatformKey.isEmpty()) {
            throw new InvalidConfigException("Google Cloud Platform Key is not set", configFile);
        }
    }
}
