package io.github.adelemphii.recognition;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import io.github.adelemphii.App;
import io.github.adelemphii.objects.Recorder;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VoiceCommands {

    private static final String API_KEY = System.getenv("PICOVOICE_KEY");
    private final File customKeyword = new File("listening_en_windows_v2_1_0.ppn");

    private final Porcupine porcupine;

    private final Recorder recorder;

    public VoiceCommands() throws PorcupineException, IOException {

        porcupine = new Porcupine.Builder()
                .setKeywordPath(customKeyword.getAbsolutePath())
                .setAccessKey(API_KEY)
                .build();

        this.recorder = new Recorder();
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
                SpeechToText.record(5);
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
