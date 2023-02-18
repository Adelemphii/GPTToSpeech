package io.github.adelemphii;

import io.github.adelemphii.objects.ClipPlayer;
import io.github.adelemphii.objects.exceptions.GPTException;
import io.github.adelemphii.utility.BucketUpload;
import io.github.adelemphii.utility.ChatGPT;
import io.github.adelemphii.utility.SpeechToText;
import io.github.adelemphii.utility.TextToSpeech;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class App {
    public static void main(String[] args) {

        SpeechToText.record(5);

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
}
