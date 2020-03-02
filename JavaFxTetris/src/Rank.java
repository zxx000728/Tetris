import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;

class Rank {
    Rank(){
        Stage stage=new Stage();

        Pane pane=new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));

        //The label 'Rank'
        Label lblRank = new Label("Rank");
        lblRank.setFont(Font.font("Arial Black", FontWeight.BOLD, 50));
        lblRank.setLayoutX(200);
        lblRank.setLayoutY(20);

        Label lblScoreRank = new Label("ScoreRank");
        lblScoreRank.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        lblScoreRank.setLayoutX(90);
        lblScoreRank.setLayoutY(100);

        Label lblTimeRank = new Label("TimeRank");
        lblTimeRank.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        lblTimeRank.setLayoutX(300);
        lblTimeRank.setLayoutY(100);

        //The button 'Back'
        Button btBack = new Button("Back");
        btBack.setStyle("-fx-border-color: white; -fx-background-color:grey; -fx-text-fill:white");
        Game.setButton(btBack,450,370,3,3);
        btBack.setOnAction(event -> stage.close());

        //The background
        Image image = new Image("file:src/HelpBackground.gif");
        ImageView imageView = new ImageView(image);
        imageView.setX(0);
        imageView.setY(70);
        imageView.setFitHeight(400);
        imageView.setFitHeight(400);

        pane.getChildren().addAll(imageView,lblRank,btBack,lblScoreRank,lblTimeRank);

        if(!Game.playerList.isEmpty()) {
            try {
                printRank(pane);
                printRankTime(pane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        stage.setResizable(false);
        stage.setTitle("Rank");
        stage.setScene(new Scene(pane,600,450));
        stage.show();
    }

    /**
     * Print the player rank:name + score
     */
    private void printRank(Pane pane) throws IOException {
        int num = 1;
        if (!Game.playerList.isEmpty()) {
            for (Player i : Game.playerList) {
                String strRank = "rank " + num++ + ": " + i.toStringScore();
                Label lblRank = new Label(strRank);
                lblRank.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                lblRank.setTextFill(Color.BLACK);
                lblRank.setLayoutX(100);
                lblRank.setLayoutY(150 + 20 * (num - 2));
                pane.getChildren().add(lblRank);
            }
        }
    }

    private void printRankTime(Pane pane) throws IOException {
        int num = 1;
        if (!Game.playerListTime.isEmpty()) {
            for (Player i : Game.playerListTime) {
                String strRank = "rank " + num++ + ": " + i.toStringTime();
                Label lblRank = new Label(strRank);
                lblRank.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                lblRank.setTextFill(Color.BLACK);
                lblRank.setLayoutX(300);
                lblRank.setLayoutY(150 + 20 * (num - 2));
                pane.getChildren().add(lblRank);
            }
        }
    }
}
