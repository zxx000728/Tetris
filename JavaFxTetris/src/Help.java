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
import javafx.stage.StageStyle;

class Help {
    Help(){
        Stage stage=new Stage();

        Pane pane=new Pane();
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE,null,null)));

        //The label 'How to play'
        Label lblTitle = new Label("How to play");
        lblTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 50));
        lblTitle.setLayoutX(150);
        lblTitle.setLayoutY(20);

        //The label 'Hey! Welcome to Tetris World!'
        Label lblWelcome = new Label();
        lblWelcome.setText("Hey! Welcome to Tetris World!");
        lblWelcome.setFont(Font.font("Arial Black",20));
        lblWelcome.setLayoutX(40);
        lblWelcome.setLayoutY(110);

        //The label command
        Label lblHowToPlay = new Label();
        lblHowToPlay.setText(
                "'a' : Left\n\n" +
                "'d' : Right\n\n" +
                "'s' : Drop\n\n" +
                "'w' : Rotate\n\n"
        );
        lblHowToPlay.setFont(Font.font("Arial",15));
        lblHowToPlay.setLayoutX(40);
        lblHowToPlay.setLayoutY(150);

        //The button 'Back',press to close the stage
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

        //Add all the nodes to the pane
        pane.getChildren().addAll(imageView,lblTitle,lblWelcome,lblHowToPlay,btBack);

        stage.setResizable(false);
        stage.setTitle("Help");
        stage.setScene(new Scene(pane,600,450));
        stage.show();
    }
}
