package io.github.adelemphii.objects;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class ClipPlayer implements LineListener {

    boolean isCompleted = false;

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type == LineEvent.Type.STOP) {
            isCompleted = true;
            System.out.println("ClipPlayer: Clip has stopped playing");
        } else if(type == LineEvent.Type.START) {
            System.out.println("ClipPlayer: Clip has started playing");
        }
    }

    public void start(InputStream inputStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(inputStream);
        Clip clip = AudioSystem.getClip();

        clip.addLineListener(this);
        clip.open(audioInputStream);
        clip.start();
        // TODO: this is a hack, remove when program doesn't exit before clip finishes playing
        Thread.sleep(getLengthInMillis(clip));

        clip.close();
        audioInputStream.close();
        inputStream.close();
    }

    private long getLengthInMillis(Clip clip) {
        long length = clip.getMicrosecondLength();
        return length / 1000;
    }
}
