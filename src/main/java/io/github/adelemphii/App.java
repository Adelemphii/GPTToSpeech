package io.github.adelemphii;

import ai.picovoice.porcupine.PorcupineException;
import io.github.adelemphii.objects.ClipPlayer;
import io.github.adelemphii.objects.Configuration;
import io.github.adelemphii.objects.exceptions.GPTException;
import io.github.adelemphii.recognition.SpeechToText;
import io.github.adelemphii.recognition.TextToSpeech;
import io.github.adelemphii.recognition.VoiceCommands;
import io.github.adelemphii.utility.BucketUpload;
import io.github.adelemphii.utility.ChatGPT;
import org.apache.commons.cli.*;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class App {

    private static Configuration configuration;

    public static void main(String[] args) {
        Options options = buildOptions();
        // TODO: load from config file
        configuration = new Configuration(null, null, null, null);
        parseOptions(options, args);

        try {
            VoiceCommands voiceCommands = new VoiceCommands();
        } catch (PorcupineException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void speak() {
        String response = getResponse();

        if(response != null && response.length() > 0) {
            System.out.println(response);
            TextToSpeech.speak(response);

            ClipPlayer player = new ClipPlayer();
            try {
                player.start(TextToSpeech.getAudioInputStream());
            } catch (IOException | LineUnavailableException | UnsupportedAudioFileException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No response");
        }
    }

    private static String getResponse() {
        String response = null;
        try {
            BucketUpload.uploadObject("gpttospeech", "gpt_to_speech_bucket",
                    "stt.wav", "stt.wav");

            String transcript = SpeechToText.getTranscription("gs://gpt_to_speech_bucket/stt.wav");

            int tokens = ChatGPT.calculateTokens(transcript);
            response = ChatGPT.getCompletionFromTranscription(transcript, tokens * 5);
        } catch (IOException | GPTException e) {
            e.printStackTrace();
        }
        return response;
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

        if(picoKey != null && picoKey.length() != 0) {
            configuration.setPicoKey(picoKey);
        }

        if(openAIKey != null && openAIKey.length() != 0) {
            configuration.setOpenAIKey(openAIKey);
        }

        if(googleCloudStorageKey != null && googleCloudStorageKey.length() != 0) {
            configuration.setGoogleCloudStorageKey(googleCloudStorageKey);
        }

        if(googleCloudPlatformKey != null && googleCloudPlatformKey.length() != 0) {
            configuration.setGoogleCloudPlatformKey(googleCloudPlatformKey);
        }
    }

    private static Options buildOptions() {
        Options options = new Options();

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
