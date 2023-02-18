package io.github.adelemphii.utility;

import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;

import java.io.*;

public class TextToSpeech {

    public static void speak(String text) {
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice =
                    VoiceSelectionParams.newBuilder()
                            .setLanguageCode("en-AU")
                            .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                            .build();

            AudioConfig audioConfig =
                    AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

            SynthesizeSpeechResponse response =
                    textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            try (OutputStream out = new FileOutputStream("output.wav")) {
                out.write(audioContents.toByteArray());
                System.out.println("Audio content written to file \"output.mp3\"");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedInputStream getAudioInputStream() {
        try {
            File file = new File("output.wav");
            if(!file.exists()) {
                // TODO: make some default audio files
                throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
            }
            return new BufferedInputStream(new FileInputStream("output.wav"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
