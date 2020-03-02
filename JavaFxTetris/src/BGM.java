import javafx.animation.Timeline;
import javafx.scene.media.AudioClip;

import java.io.File;

public class BGM {
    public static AudioClip ac;
    static int musicCount = 0;

    static void playMusic(String pathname) {
        if(musicCount == 0) {
            musicCount ++;
            ac = new AudioClip(new File(pathname).toURI().toString());
            ac.setVolume(Game.bgmVolume);
            ac.play();
            ac.setCycleCount(Timeline.INDEFINITE);

        }
    }
}
