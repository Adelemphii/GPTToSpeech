package io.github.adelemphii.utility;

import com.google.cloud.speech.v1.*;
import io.github.adelemphii.objects.Recorder;

import java.io.IOException;
import java.util.List;

public class SpeechToText {
    public static String getTranscription(String gcsURI) throws IOException {
        StringBuilder transcription = new StringBuilder();

        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode("en-US")
                    .setSampleRateHertz(16000)
                    .build();

            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setUri(gcsURI)
                    .build();

            RecognizeResponse response = speechClient.recognize(config, audio);
            List<SpeechRecognitionResult> results = response.getResultsList();

            for(SpeechRecognitionResult result : results) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                System.out.printf("Transcription: %s%n", alternative.getTranscript());
                transcription.append(alternative.getTranscript());
            }
        }

        return transcription.toString();
    }

    public static void record(long recordTime) {
        final Recorder recorder = new Recorder();
        recorder.setRecordTimeInSeconds(recordTime);

        Thread stopper = new Thread(() -> {
            try {
                Thread.sleep(recorder.getRecordTimeMillis());
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            recorder.stop();
        });

        stopper.start();
        recorder.start();
    }
}
