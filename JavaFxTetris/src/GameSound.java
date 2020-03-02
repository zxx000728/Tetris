import javafx.scene.media.AudioClip;

import java.io.File;

class GameSound {
    static AudioClip acGameSound = new AudioClip(new File("src/GameSound.mp3").toURI().toString());
    private static AudioClip acDestroySound = new AudioClip(new File("src/GameSound2.mp3").toURI().toString());
    private static AudioClip acGameOverSound = new AudioClip(new File("src/GameOverSound.mp3").toURI().toString());

    static void playMusic() {
        GameSound.acGameSound.setVolume(Game.gameSoundVolume / 100.0);
            acGameSound.play();
            acGameSound.setCycleCount(1);
    }

    static void playMenuSound(){
        GameSound.acDestroySound.setVolume(Game.gameSoundVolume / 100.0);
        acDestroySound.play();
        acDestroySound.setCycleCount(1);
    }

    static void playGameOverSound(){
        GameSound.acGameOverSound.setVolume(Game.gameSoundVolume / 100.0);
        acGameOverSound.play();
        acGameOverSound.setCycleCount(1);
    }

}
