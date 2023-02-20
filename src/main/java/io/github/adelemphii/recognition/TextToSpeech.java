package io.github.adelemphii.recognition;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.*;

public class TextToSpeech {

    public static void speak(String text, String path) {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setName("en-AU-Wavenet-B")
                            .setLanguageCode("en-AU")
                            .setSsmlGender(SsmlVoiceGender.MALE)
                            .build();

            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream(path)) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file: " + path);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedInputStream getAudioInputStream(String path) {
        try {
            File file = new File(path);
            if(!file.exists()) {
                // TODO: make some default audio files
                throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
            }
            return new BufferedInputStream(new FileInputStream(path));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
