import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

class Settings {
    static String[] music = {"src/Bgm.mp3", "src/infinity.mp3", "src/Bgm3.mp3"};
    static String[] list = {"Music 1", "Music 2", "Music 3"};
    static ObservableList<String> items = FXCollections.observableArrayList(list);
    static ComboBox<String> cbo = new ComboBox<>();

    Settings() {
        Stage stage = new Stage();

        Pane pane = new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        //The background
        Image image = new Image("file:src/HelpBackground.gif");
        ImageView imageView = new ImageView(image);
        imageView.setX(0);
        imageView.setY(70);
        imageView.setFitHeight(400);
        imageView.setFitHeight(400);

        pane.getChildren().add(imageView);
        //The label 'Settings'
        Label lblSettings = new Label("Settings");
        lblSettings.setFont(Font.font("Arial Black", FontWeight.BOLD, 50));
        lblSettings.setLayoutX(200);
        lblSettings.setLayoutY(20);


        //The background music label 'Volume'
        Label lblBgmVolume = new Label("Volume");
        lblBgmVolume.setLayoutX(40);
        lblBgmVolume.setLayoutY(145);
        lblBgmVolume.setFont(Font.font("Arial", 20));
        lblBgmVolume.setVisible(Game.bgmIsSelected);

        //The game sound slider
        Slider scGameSound = new Slider();
        scGameSound.setMin(0);
        scGameSound.setMax(100);
        scGameSound.setValue(Game.gameSoundVolume);
        scGameSound.valueProperty().addListener(ov -> {
            if (scGameSound.isValueChanging()) {
                GameSound.acGameSound.stop();
                GameSound.acGameSound.setVolume(scGameSound.getValue() / 100.0);
                Game.gameSoundVolume = scGameSound.getValue();
                GameSound.acGameSound.play();
            }
        });
        scGameSound.setLayoutX(150);
        scGameSound.setLayoutY(250);
        scGameSound.setVisible(Game.gameSoundIsSelected);

        //The game sound label 'Volume'
        Label lblGameSoundVolume = new Label("Volume");
        lblGameSoundVolume.setLayoutX(40);
        lblGameSoundVolume.setLayoutY(245);
        lblGameSoundVolume.setFont(Font.font("Arial", 20));
        lblGameSoundVolume.setVisible(Game.gameSoundIsSelected);

        //The game sound checkbox
        CheckBox chkGameSound = new CheckBox("Game Sound");
        chkGameSound.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        chkGameSound.setSelected(Game.gameSoundIsSelected);
        chkGameSound.setOnAction(event -> {
            Game.gameSoundIsSelected = chkGameSound.isSelected();
            scGameSound.setVisible(Game.gameSoundIsSelected);
            lblGameSoundVolume.setVisible(Game.gameSoundIsSelected);
        });
        chkGameSound.setLayoutX(40);
        chkGameSound.setLayoutY(200);

        //The button 'Back'
        Button btBack = new Button("Back");
        btBack.setStyle("-fx-border-color: white; -fx-background-color:grey; -fx-text-fill:white");
        Game.setButton(btBack, 450, 370, 3, 3);
        btBack.setOnAction(event -> stage.close());

        try {
            cbo.getItems().removeAll(items);
        } catch (Exception ignored) {
        }
        cbo.getItems().addAll(items);
        cbo.setStyle("-fx-color: White");
        cbo.setOnAction(e -> setDisplay(items.indexOf(cbo.getValue())));
        cbo.setVisible(Game.bgmIsSelected);
        cbo.setLayoutX(350);
        cbo.setLayoutY(110);
        if (Game.bgmIsSelected) {
            cbo.setValue(list[Game.musicIndex]);
        }

        //The background music slider
        Slider scBgm = new Slider();
        scBgm.setMin(0);
        scBgm.setMax(100);
        scBgm.setValue(Game.bgmVolume);
        scBgm.valueProperty().addListener(ov -> {
            if (scBgm.isValueChanging()) {
                BGM.ac.stop();
                BGM.ac.setVolume(scBgm.getValue() / 100.0);
                Game.bgmVolume = scBgm.getValue();
                BGM.ac.play();
            }
        });
        scBgm.setLayoutX(150);
        scBgm.setLayoutY(150);
        scBgm.setVisible(Game.bgmIsSelected);

        //The background music checkbox
        CheckBox chkBackgroundMusic = new CheckBox("Background Music");
        chkBackgroundMusic.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        chkBackgroundMusic.setSelected(Game.bgmIsSelected);
        chkBackgroundMusic.setOnAction(event -> {
            Game.bgmIsSelected = chkBackgroundMusic.isSelected();
            scBgm.setVisible(Game.bgmIsSelected);
            lblBgmVolume.setVisible(Game.bgmIsSelected);
            cbo.setVisible(Game.bgmIsSelected);
            if (!Game.bgmIsSelected)
                BGM.ac.stop();
            else {
                BGM.musicCount = 0;
                setDisplay(Game.musicIndex);
            }
        });
        chkBackgroundMusic.setLayoutX(40);
        chkBackgroundMusic.setLayoutY(100);


        pane.getChildren().addAll(lblSettings, chkBackgroundMusic, chkGameSound, lblGameSoundVolume, scGameSound,
                lblBgmVolume, scBgm, btBack, cbo);

        stage.setResizable(false);
        stage.setTitle("Settings");
        stage.setScene(new Scene(pane, 600, 450));
        stage.show();
    }

    static void setDisplay(int index) {
        switch (index) {
            case 0: {
                try {
                    BGM.ac.stop();
                } catch (Exception ignored) {
                }
                Game.musicIndex = 0;
                BGM.musicCount = 0;
                BGM.playMusic(music[0]);
            }
            break;
            case 1: {
                try {
                    BGM.ac.stop();
                } catch (Exception ignored) {
                }
                Game.musicIndex = 1;
                BGM.musicCount = 0;
                BGM.playMusic(music[1]);
            }
            break;
            case 2: {
                try {
                    BGM.ac.stop();
                } catch (Exception ignored) {
                }
                Game.musicIndex = 2;
                BGM.musicCount = 0;
                BGM.playMusic(music[2]);
            }
            break;
        }
    }
}
