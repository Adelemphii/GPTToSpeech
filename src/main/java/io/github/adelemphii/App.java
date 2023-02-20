package io.github.adelemphii;

import ai.picovoice.porcupine.PorcupineException;
import io.github.adelemphii.objects.ClipPlayer;
import io.github.adelemphii.objects.config.APIKeys;
import io.github.adelemphii.objects.config.BucketConfig;
import io.github.adelemphii.objects.config.Configuration;
import io.github.adelemphii.objects.enums.TTSVoices;
import io.github.adelemphii.objects.exceptions.GPTException;
import io.github.adelemphii.objects.exceptions.InvalidConfigException;
import io.github.adelemphii.recognition.SpeechToText;
import io.github.adelemphii.recognition.TextToSpeech;
import io.github.adelemphii.recognition.VoiceCommands;
import io.github.adelemphii.storage.ConfigLoader;
import io.github.adelemphii.utility.BucketUpload;
import io.github.adelemphii.utility.ChatGPT;
import lombok.Getter;
import org.apache.commons.cli.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Scanner;

public class App {

    private static Configuration configuration;
    @Getter
    private static ConfigLoader configLoader;

    public static void main(String[] args) {
        Options options = buildOptions();
        boolean debug = isDebug(options, args);

        if(!debug) {
            configLoader = new ConfigLoader();
            try {
                configuration = configLoader.loadConfig();
            } catch (InvalidConfigException e) {
                e.printStackTrace();
                return;
            }
            parseOptions(options, args);

            try {
                configuration.validate();
            } catch (InvalidConfigException e) {
                e.printStackTrace();
                return;
            }
        } else {
            configuration = buildIDEConfiguration();
        }

        printConfiguration();

        try {
            new VoiceCommands(configuration);
        } catch (PorcupineException | IOException e) {
            throw new RuntimeException(e);
        }

        new Scanner(System.in).nextLine();
    }

    public static void speak() {
        String response = getResponse();

        if(response != null && response.length() > 0) {
            System.out.println(response);
            TextToSpeech.speak(response, configuration.getOutputPath());

            ClipPlayer player = new ClipPlayer();
            try {
                player.start(TextToSpeech.getAudioInputStream(configuration.getOutputPath()));
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No response");
        }
    }

    private static String getResponse() {
        BucketConfig bucketConfig = configuration.getBucketConfig();

        String response = null;
        try {
            BucketUpload.uploadObject(bucketConfig.getProjectID(), bucketConfig.getBucketName(),
                    bucketConfig.getObjectName(), bucketConfig.getObjectPath());

            String transcript = SpeechToText.getTranscription("gs://" + bucketConfig.getBucketName() + "/"
                    + bucketConfig.getObjectName());

            int tokens = ChatGPT.calculateTokens(transcript);
            response = ChatGPT.getCompletionFromTranscription(transcript, tokens * 5);
        } catch (IOException | GPTException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static boolean isDebug(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("gpttospeech", options);
            System.exit(1);
            return false;
        }

        return cmd.hasOption("debug");
    }

    private static Configuration buildIDEConfiguration() {
        configuration = new Configuration();
        configuration.setConfigFile(null);

        APIKeys apiKeys = new APIKeys();
        apiKeys.setPicoKey(System.getenv("PICOVOICE_KEY"));
        apiKeys.setGoogleCloudPlatformKey(System.getenv("GOOGLE_TTS_TOKEN"));
        apiKeys.setGoogleCloudStorageKey(System.getenv("GOOGLE_TTS_TOKEN"));
        apiKeys.setOpenAIKey(System.getenv("CHATGPT_API_TOKEN"));
        configuration.setApiKeys(apiKeys);

        configuration.setOutputPath("./project/audio/output.wav");
        configuration.setInputPath("./project/audio/input.wav");
        configuration.setPicoKeywordPath("./project/pico-keyword/listening.ppn");

        configuration.setTtsVoice(TTSVoices.ENGLISH_AU_B);

        BucketConfig bucketConfig = new BucketConfig(
                "gpttospeech",
                "gpt_to_speech_bucket",
                configuration.getInputName(),
                configuration.getInputPath()
        );

        configuration.setBucketConfig(bucketConfig);

        return configuration;
    }

    private static void printConfiguration() {
        System.out.println("Configuration:");
        System.out.println("  PicoVoice Key: " + configuration.getApiKeys().getPicoKey());
        System.out.println("  Google Cloud Platform Key: " + configuration.getApiKeys().getGoogleCloudPlatformKey());
        System.out.println("  Google Cloud Storage Key: " + configuration.getApiKeys().getGoogleCloudStorageKey());
        System.out.println("  OpenAI Key: " + configuration.getApiKeys().getOpenAIKey());
        System.out.println("  Output Path: " + configuration.getOutputPath());
        System.out.println("  Input Path: " + configuration.getInputPath());
        System.out.println("  Pico Keyword Path: " + configuration.getPicoKeywordPath());
        System.out.println("  TTS Voice: " + configuration.getTtsVoice().getName());
        System.out.println("  Bucket Config:");
        System.out.println("    Project ID: " + configuration.getBucketConfig().getProjectID());
        System.out.println("    Bucket Name: " + configuration.getBucketConfig().getBucketName());
        System.out.println("    Object Name: " + configuration.getBucketConfig().getObjectName());
        System.out.println("    Object Path: " + configuration.getBucketConfig().getObjectPath());
    }

    private static void parseOptions(Options options, String[] args) {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("gpttospeech", options);
            System.exit(1);
            return;
        }

        if(cmd.hasOption("help")) {
            formatter.printHelp("gpttospeech", options);
            System.exit(1);
        }

        String picoKey = cmd.getOptionValue("access-key-pico");
        String openAIKey = cmd.getOptionValue("access-key-gpt");
        String googleCloudStorageKey = cmd.getOptionValue("access-key-gcs");
        String googleCloudPlatformKey = cmd.getOptionValue("access-key-gcp");

        APIKeys apiKeys = new APIKeys();

        if(picoKey != null && picoKey.length() != 0) {
            apiKeys.setPicoKey(picoKey);
        }

        if(openAIKey != null && openAIKey.length() != 0) {
            apiKeys.setOpenAIKey(openAIKey);
        }

        if(googleCloudStorageKey != null && googleCloudStorageKey.length() != 0) {
            apiKeys.setGoogleCloudStorageKey(googleCloudStorageKey);
        }

        if(googleCloudPlatformKey != null && googleCloudPlatformKey.length() != 0) {
            apiKeys.setGoogleCloudPlatformKey(googleCloudPlatformKey);
        }
        configuration.setApiKeys(apiKeys);
    }

    private static Options buildOptions() {
        Options options = new Options();

        options.addOption(Option.builder("db")
                .longOpt("debug")
                .hasArg(false)
                .desc("Enable debug mode")
                .build());
        options.addOption(Option.builder("akp")
                .longOpt("access-key-pico")
                .hasArg(true)
                .desc("Access key for Picovoice")
                .build());

        options.addOption(Option.builder("akgpt")
                .longOpt("access-key-gpt")
                .hasArg(true)
                .desc("Access key for OpenAI")
                .build());

        options.addOption(Option.builder("akb")
                .longOpt("access-key-gcs")
                .hasArg(true)
                .desc("Access key for Google Cloud Storage")
                .build());

        options.addOption(Option.builder("akg")
                .longOpt("access-key-gcp")
                .hasArg(true)
                .desc("Access key for Google Cloud Platform")
                .build());

        options.addOption("h", "help", false, "Show help");

        return options;
    }
}
