package io.github.adelemphii.objects;

import lombok.Getter;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Recorder {

    private final File recordingFile;

    @Getter
    private long recordTimeMillis = 10000;  // 10 seconds
    private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    @Getter
    private TargetDataLine line;

    @Getter
    private boolean running;

    public Recorder(File recordingFile) {
        this.recordingFile = recordingFile;
    }

    public void start(boolean save) {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if(!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(1);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            if(save) {
                AudioInputStream audioInputStream = new AudioInputStream(line);
                AudioSystem.write(audioInputStream, fileType, recordingFile);
            }
        } catch (LineUnavailableException | IOException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }
        running = true;
    }

    public void stop() {
        line.stop();
        line.close();
        System.out.println("Finished");
        running = false;
    }

    public AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public void setRecordTimeInSeconds(long seconds) {
        recordTimeMillis = seconds * 1000;
    }

}
