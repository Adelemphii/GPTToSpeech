package io.github.adelemphii.storage;

import io.github.adelemphii.objects.config.APIKeys;
import io.github.adelemphii.objects.config.BucketConfig;
import io.github.adelemphii.objects.config.Configuration;
import io.github.adelemphii.objects.config.GPTConfig;
import io.github.adelemphii.objects.enums.TTSVoices;
import io.github.adelemphii.objects.exceptions.InvalidConfigException;
import io.github.adelemphii.utility.FileUtility;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class ConfigLoader {

    private final File configFile = new File("./config.yml");

    public ConfigLoader() {
    }

    public Configuration loadConfig() throws InvalidConfigException {
        if(!configFile.exists()) {
            FileUtility.copy(getClass().getResourceAsStream("/config.yml"), "config.yml");
            throw new InvalidConfigException("Config file does not exist", configFile);
        }

        Configuration configuration = new Configuration();
        configuration.setConfigFile(configFile);
        Yaml yaml = new Yaml();

        HashMap<String, String> options;
        try {
            // use configFile
            options = yaml.load(new FileInputStream(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new InvalidConfigException("Invalid config file", configFile);
        }

        APIKeys apiKeys = new APIKeys(
                options.get("picoKey"),
                options.get("openAIKey"),
                options.get("googleCloudStorageKey"),
                options.get("googleCloudPlatformKey")
        );
        configuration.setApiKeys(apiKeys);

        // paths
        configuration.setPicoKeywordPath(options.getOrDefault("picoKeywordPath", "./pico-keyword/listening.ppn"));
        configuration.setInputPath(options.getOrDefault("inputPath", "./audio/input.wav"));
        configuration.setOutputPath(options.getOrDefault("outputPath", "./audio/output.wav"));

        GPTConfig gptConfig = new GPTConfig();
        gptConfig.setGptLanguage(options.get("gptLanguage"));
        try {
            gptConfig.setGptTemperature(Double.parseDouble(options.getOrDefault("gptTemperature", "0.0")));
            gptConfig.setGptMaxTokens(Integer.parseInt(options.getOrDefault("gptMaxTokens", "100")));
        } catch (NumberFormatException e) {
            throw new InvalidConfigException("Invalid GPTConfig number format", configFile);
        }
        configuration.setGptConfig(gptConfig);

        configuration.setTtsVoice(TTSVoices.getVoiceFromName(options.getOrDefault("ttsName", "en-AU-Wavenet-B")));

        BucketConfig bucketConfig = new BucketConfig(
                options.get("projectID"),
                options.get("bucketName"),
                options.get("objectName"),
                options.get("objectPath")
        );
        configuration.setBucketConfig(bucketConfig);

        return configuration;
    }
}
