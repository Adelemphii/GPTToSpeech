package io.github.adelemphii.recognition;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import io.github.adelemphii.App;
import io.github.adelemphii.objects.Recorder;
import io.github.adelemphii.objects.config.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VoiceCommands {

    @Getter @Setter
    private Configuration configuration;


    private final Porcupine porcupine;

    private final Recorder recorder;

    public VoiceCommands(Configuration configuration) throws PorcupineException, IOException {
        this.configuration = configuration;

        porcupine = new Porcupine.Builder()
                .setKeywordPath(configuration.getPicoKeywordPath())
                .setAccessKey(configuration.getApiKeys().getPicoKey())
                .build();

        this.recorder = new Recorder(new File(configuration.getInputPath()));
        recorder.start(false);
        listen();
    }

    private void listen() throws PorcupineException, IOException {
        System.out.println("Listening for voice commands...");
        int secondsToRecord = 5;

        int frameLength = porcupine.getFrameLength();
        ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
        captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
        short[] porcupineBuffer = new short[frameLength];

        int numBytesRead;
        while(System.in.available() == 0) {
            // read a buffer of audio
            numBytesRead = recorder.getLine().read(captureBuffer.array(), 0, captureBuffer.capacity());

            // don't pass to porcupine if we don't have a full buffer
            if (numBytesRead != frameLength * 2) {
                continue;
            }

            // copy into 16-bit buffer
            captureBuffer.asShortBuffer().get(porcupineBuffer);

            // process with porcupine
            int result = porcupine.process(porcupineBuffer);
            if (result >= 0) {
                recorder.stop();

                System.out.println("Detected keyword");
                SpeechToText.record(5, new File(configuration.getInputPath()));
                App.speak();

                try {
                    // TODO: growling noises
                    Thread.sleep((secondsToRecord + 1) * 1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(!recorder.isRunning()) {
                System.out.println("Restarting recorder...");
                recorder.start(false);
            }
        }
    }
}
