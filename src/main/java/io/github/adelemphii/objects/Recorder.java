package io.github.adelemphii.objects;

import lombok.Getter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Recorder {

    private final File file = new File("stt.wav");

    @Getter
    private long recordTimeMillis = 10000;  // 10 seconds
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private TargetDataLine line;

    public void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if(!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            System.out.println("Start capturing...");

            AudioInputStream audioInputStream = new AudioInputStream(line);
            System.out.println("Start recording...");

            AudioSystem.write(audioInputStream, fileType, file);
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void setRecordTimeInSeconds(long seconds) {
        recordTimeMillis = seconds * 1000;
    }

}
