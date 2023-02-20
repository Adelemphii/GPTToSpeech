package io.github.adelemphii.objects.config;

import io.github.adelemphii.objects.enums.TTSVoices;
import io.github.adelemphii.objects.exceptions.InvalidConfigException;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

public class Configuration {

    @Getter @Setter
    private File configFile;

    // API Keys
    @Getter @Setter
    private APIKeys apiKeys;

    // Paths
    @Getter @Setter
    private String picoKeywordPath;
    @Getter @Setter
    private String inputPath;
    @Getter @Setter
    private String outputPath;

    // GPT Configuration
    @Getter @Setter
    private GPTConfig gptConfig;

    // Text to Speech Configuration
    @Getter @Setter
    private TTSVoices ttsVoice;

    // Google Cloud Storage Configuration
    @Getter @Setter
    private BucketConfig bucketConfig;

    @Getter @Setter
    private boolean debug;

    public Configuration() {
        this.apiKeys = null;

        this.picoKeywordPath = "";
        this.inputPath = "";
        this.outputPath = "";

        this.gptConfig = new GPTConfig();
        this.ttsVoice = TTSVoices.ENGLISH_AU_B;

        this.bucketConfig = null;
        this.debug = false;
    }

    public void validate() throws InvalidConfigException {
        apiKeys.validate(configFile);
        bucketConfig.validate(configFile);
    }

    public String getInputName() {
        // get the file name from the input path
        String[] split = inputPath.split("/");
        return split[split.length - 1];
    }
}
