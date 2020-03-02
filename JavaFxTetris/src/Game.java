import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class Game extends Application {
    GridPane gamePane;//Pane to hold the map
    Pane boardPane;//The main Pane in the game
    GridPane nextBlockPane;//Pane to hold next block
    Timer timer;//Timer to control the block drop automatically
    private Blocks block = new Blocks();
    Label lblScore;//The score label
    Label lblTime = new Label();//The time label
    int x, y;//block location
    int blockSize = 27;//The block size in the mao
    int score = 0;//Score
    int[][] stableMap = new int[25][14];//The map to save the dropped blocks
    int[][] map = new int[25][14];//The map to hold the dropping blocks
    int[][] cell = new int[4][4];//The normal block cell in the game
    boolean isPaused;//Whether the Button pause has been fired
    boolean hasStarted = true;//Whether the Button start has been fired
    long startTime = System.currentTimeMillis();//Start time = current time
    long tempTime = 0;//The game time before pausing
    long time = 0;//The game time
    String strTime;//The game time string x : x: x
    static ArrayList<Player> playerList = new ArrayList<>();//store rank
    static ArrayList<Player> playerListTime = new ArrayList<>();//store time
    private java.io.File file;//Store rank
    private java.io.File timeFile;//Store time
    static double bgmVolume = 50;//The volume of the background music
    static double gameSoundVolume = 50;//The volume of the game sound
    static boolean bgmIsSelected = true;//Whether the checkbox background music has been selected
    static boolean gameSoundIsSelected = true;//Whether the checkbox music sound has been selected
    static int speed;//The speed of dropping
    static int musicIndex = 0;//The music to play
    private int mode = 1;//The game mode
    int count = 0;//When to accelerate

    /**
     * PrimaryStage of the game
     * Initialize the settings bgm,gameSound,bgmVolume,gameSoundVolume,dropping speed
     * Create a new pane,set background
     * Crate a new scene
     * Add nodes LblTetris,btStart,btExit,btRank,btSettings,btHelp,set location
     * Put scene to Stage
     * Show Stage
     *
     * @param primaryStage PrimaryStage
     */
    public void start(Stage primaryStage) {
        {
            playerList = new ArrayList<>();
            playerListTime = new ArrayList<>();
            try {
                read();
                readTime();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Initialize settings
        speed = 1000;

        // Create a new pane
        Pane pane = new Pane();
        Image image = new Image("file:src/GameBackground.gif");
        double width = image.getWidth();
        double height = image.getHeight();
        Scene scene = new Scene(pane, width, height);

        // Place nodes in the pane
        Label LblTetris = new Label("Tetris Go !");
        LblTetris.setFont(Font.font("Arial Black", FontWeight.BOLD, 50));
        LblTetris.setStyle("-fx-border-color: white; -fx-border-width: 5");
        LblTetris.setTextFill(Color.WHITE);
        LblTetris.setLayoutX(150);
        LblTetris.setLayoutY(150);

        Button btStart = new Button("Start");
        setButton(btStart, (int) width / 2 - 200, 320, 3, 3);
        btStart.setStyle("-fx-border-color: black; -fx-background-color:white");
        btStart.setOnAction(event -> {
            hasStarted = true;
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            setModePane(primaryStage);
        });

        Button btExit = new Button("Exit");
        setButton(btExit, 280, 320, 3, 3);
        btExit.setStyle("-fx-border-color: black; -fx-background-color:white");
        btExit.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            System.exit(0);
        });

        Button btRank = new Button("Rank");
        setButton(btRank, (int) width / 2 + 155, 320, 3, 3);
        btRank.setStyle("-fx-border-color: black; -fx-background-color:white");
        btRank.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            new Rank();
        });

        Button btSettings = new Button("Settings");
        setButton(btSettings, 450, 20, 1, 1);
        btSettings.setStyle("-fx-border-color: black; -fx-background-color:white");
        btSettings.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            new Settings();
        });

        Button btHelp = new Button("Help");
        setButton(btHelp, 525, 20, 1, 1);
        btHelp.setStyle("-fx-border-color: black; -fx-background-color:white");
        btHelp.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            new Help();
        });

        //Add the nodes to the pane
        pane.getChildren().addAll(LblTetris, btStart, btExit, btRank, btSettings, btHelp);

        pane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.ROUND,
                BackgroundRepeat.ROUND, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        primaryStage.setTitle("Welcome to Zxx's Tetris World ahhhhhhhh");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        if (bgmIsSelected)
            Settings.setDisplay(musicIndex);
        primaryStage.show();
    }

    /**
     * Set the game panes
     * Clear the nodes in the panes
     * Set button btStart,btQuit,btContinue,btPause,add to boardPane
     * BtStart To start the game
     * BtQuit To quit the game
     * BtContinue To continue the game after pause
     * BtPause To pause the game
     * Show Score and Time if game is over
     *
     * @param stage The primary Stage
     */
    private void setGame(Stage stage) {

        //Create the panes
        setBoardPane();
        setGamePane();
        setNextBlockPane();
        setLblScore();

        //Move the child nodes in the pane
        //Refresh the maps
        nextBlockPane.getChildren().clear();
        gamePane.getChildren().clear();
        newGames();
        boardPane.getChildren().remove(lblScore);
        setLblScore();
        setLblTime();

        //Button btStart
        Button btStart = new Button("Start");
        setButton(btStart, 352, 510, 2, 2);
        btStart.setStyle("-fx-border-color: black; -fx-background-color:white");
        btStart.setOnAction(event -> {
            if (hasStarted) {
                if (gameSoundIsSelected)
                    GameSound.playMenuSound();
                hasStarted = false;
                tempTime = 0;
                copy(block.getRandomBlock());
                block.nextRandomBlock();
                drawWall();
                createBlock();
                bottomPlus();
                initGameView();
                startTime = System.currentTimeMillis();
                setStartTimerTask(stage);
            }
        });
        btStart.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case S:
                    bottom();
                    break;
                case A:
                    left();
                    break;
                case D:
                    right();
                    break;
                case W:
                    rotate();
                    break;
            }
        });

        //Button btPause
        Button btPause = new Button("Pause");
        setButton(btPause, 355, 435, 2, 2);
        btPause.setStyle("-fx-border-color: black; -fx-background-color:white");
        btPause.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            isPaused = true;
            tempTime = System.currentTimeMillis() - startTime + tempTime;
            try {
                timer.cancel();
            } catch (NullPointerException ignored) {
            }
        });

        //Button btContinue
        Button btContinue = new Button("Continue");
        setButton(btContinue, 480, 435, 2, 2);
        btContinue.setStyle("-fx-border-color: black; -fx-background-color:white");
        btContinue.setOnAction(event -> {
            if (isPaused) {
                if (gameSoundIsSelected)
                    GameSound.playMenuSound();
                isPaused = false;
                startTime = System.currentTimeMillis();
                setContinueTimerTask(stage);
            }
        });
        btContinue.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case S:
                    bottom();
                    break;
                case A:
                    left();
                    break;
                case D:
                    right();
                    break;
                case W:
                    rotate();
                    break;
            }
        });

        //Button btQuit
        Button btQuit = new Button("Quit");
        setButton(btQuit, 466, 510, 2, 2);
        btQuit.setStyle("-fx-border-color: black; -fx-background-color:white");
        btQuit.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            tempTime = System.currentTimeMillis() - startTime + tempTime;
            try {
                timer.cancel();
            } catch (NullPointerException ignored) {
            }
            Pane confirmPane = new Pane();
            confirmPane.setMinSize(455, 300);
            confirmPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
            Label label = new Label("Are you sure to quit ?????????");
            label.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
            label.setLayoutX(25);
            label.setLayoutY(60);

            Button btYes = new Button("Yes");
            btYes.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
            Game.setButton(btYes, 100, 200, 3, 3);
            btYes.setOnAction(e -> {
                if (gameSoundIsSelected)
                    GameSound.playMenuSound();
                leave(stage);
            });

            Button btNo = new Button("No");
            btNo.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
            Game.setButton(btNo, 300, 200, 3, 3);
            btNo.setOnAction(e -> {
                if (gameSoundIsSelected)
                    GameSound.playMenuSound();
                boardPane.getChildren().remove(confirmPane);
                if (!hasStarted) {
                    isPaused = true;
                    btContinue.fire();
                }
            });
            confirmPane.setLayoutX(65);
            confirmPane.setLayoutY(100);
            confirmPane.getChildren().addAll(label, btNo, btYes);
            boardPane.getChildren().add(confirmPane);
        });

        boardPane.getChildren().addAll(btPause, btQuit, btStart, btContinue);
        stage.setScene(new Scene(boardPane, 600, 600));
    }

    /**
     * Set the timer to control the block drop automatically
     * @param stage Primary Stage
     */
    void setStartTimerTask(Stage stage) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    setLblTime(System.currentTimeMillis() - startTime);
                    count++;
                    if (mode == 2) {
                        if (count % 15 == 0 && speed > 300) {
                            speed = speed - 100;
                            tempTime = System.currentTimeMillis() - startTime + tempTime;
                            try {
                                timer.cancel();
                            } catch (NullPointerException ignored) {
                            }
                            isPaused = true;
                            setContinueTimerTask(stage);
                        }
                    }
                    nextBlockView();
                    down();
                    if (feasibility() == 0) {
                        if (IsGameOver()) {
                            timer.cancel();
                            if (gameSoundIsSelected)
                                GameSound.playGameOverSound();
                            if (mode == 1)
                                whetherLeave(stage);
                            else afterGameOver(stage);
                        }
                    }
                });
            }
        }, 0, speed);
    }

    /**
     * Set the timer to control the block drop automatically
     * @param stage Primary Stage
     */
    void setContinueTimerTask(Stage stage) {
        startTime = System.currentTimeMillis();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    setLblTime(System.currentTimeMillis() - startTime + tempTime);
                    count++;
                    nextBlockView();
                    if (mode == 2) {
                        if (count % 15 == 0 && speed > 300) {
                            speed = speed - 100;
                            tempTime = System.currentTimeMillis() - startTime + tempTime;
                            try {
                                timer.cancel();
                            } catch (NullPointerException ignored) {
                            }
                            isPaused = true;
                            setContinueTimerTask(stage);
                        }
                    }
                    down();
                    if (feasibility() == 0) {
                        if (IsGameOver()) {
                            timer.cancel();
                            if (gameSoundIsSelected) {
                                GameSound.playGameOverSound();
                            }
                            if (mode == 1)
                                whetherLeave(stage);
                            else afterGameOver(stage);
                        }
                    }
                });
            }
        }, 0, speed);
    }

    /**
     * When Button Quit is pressed,set the pane to ask player whether to leave
     * @param stage Primary Stage
     */
    private void whetherLeave(Stage stage) {
        Pane pane = new Pane();
        pane.setMinSize(455, 300);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        Label label = new Label("Whether to reserve your score and time");
        label.setFont(Font.font("Arial Black", FontWeight.BOLD, 18));
        label.setLayoutX(25);
        label.setLayoutY(60);

        Button btYes = new Button("Yes");
        btYes.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        Game.setButton(btYes, 100, 200, 3, 3);
        btYes.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            inputName(stage);
        });

        Button btNo = new Button("No");
        btNo.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        Game.setButton(btNo, 300, 200, 3, 3);
        btNo.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            afterGameOver(stage);
        });

        pane.setLayoutX(65);
        pane.setLayoutY(100);
        pane.getChildren().addAll(label, btNo, btYes);
        boardPane.getChildren().clear();
        boardPane.getChildren().add(pane);
    }

    /**
     *  If player want to reserve the score and time to the rank,
     *  set the text field to ask them to enter the name and confirm
     *  Add the player to player list an sort the rank
     *  Write the new rank to the file
     * @param stage Primary stage
     */
    private void inputName(Stage stage) {
        Label label1 = new Label("Please enter your name:");
        TextField textField = new TextField();
        Button btOk = new Button("Ok");
        HBox hb = new HBox();
        hb.setScaleX(1.5);
        hb.setPadding(new Insets(10, 10, 10, 10));
        hb.setScaleY(1.5);
        hb.getChildren().addAll(label1, textField, btOk);
        hb.setBackground(new Background(new BackgroundFill(Color.ORANGE, null, null)));
        hb.setSpacing(20);
        hb.setLayoutX(105);
        hb.setLayoutY(250);
        boardPane.getChildren().clear();
        boardPane.getChildren().add(hb);
        btOk.setOnAction(event -> {
            if (gameSoundIsSelected)
                GameSound.playMenuSound();
            String name = textField.getText();
            Player player = new Player(name, score, time);
            playerList.add(player);
            playerListTime.add(player);
            playRankScore();
            playRankTime();
            java.io.PrintWriter output = null;
            try {
                output = new java.io.PrintWriter(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0, s = playerList.size(); i < s && i < 10; i++) {
                output.print(playerList.get(i).getName() + " ");
                output.print(playerList.get(i).getScore() + " ");
                output.print(playerList.get(i).getTime() + " ");
            }
            output.close();

            try {
                output = new java.io.PrintWriter(timeFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (int i = 0, s = playerListTime.size(); i < s && i < 10; i++) {
                output.print(playerListTime.get(i).getName() + " ");
                output.print(playerListTime.get(i).getScore() + " ");
                output.print(playerListTime.get(i).getTime() + " ");
            }
            output.close();

            afterGameOver(stage);
        });
    }

    /**
     * Set the mode choose pane
     * Add button Normal、Changing Speed、Random blocks、Shuttle Boundary
     * @param stage Primary Stage
     */
    private void setModePane(Stage stage) {
        Pane pane = new Pane();
        pane.setLayoutX(100);
        pane.setLayoutY(100);

        Button btNormal = new Button("Normal");
        setButton(btNormal, 20, 20, 2, 2);
        btNormal.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        btNormal.setOnAction(event -> {
            mode = 1;
            setGame(stage);
        });

        Button btSpeed = new Button("Changing Speed");
        setButton(btSpeed, 20, 100, 2, 2);
        btSpeed.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        btSpeed.setOnAction(event -> {
            mode = 2;
            setGame(stage);
        });

        Button btRandomBlocks = new Button("Random Blocks");
        setButton(btRandomBlocks, 20, 180, 2, 2);
        btRandomBlocks.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        btRandomBlocks.setOnAction(event -> {
            mode = 3;
            PlayRandomBlock playRandomBlock = new PlayRandomBlock();
            playRandomBlock.setGame(stage);
        });

        Button btShuttle = new Button("Shuttle Border");
        setButton(btShuttle, 20, 260, 2, 2);
        btShuttle.setStyle("-fx-border-color: Black; -fx-background-color:White; -fx-text-fill:Black");
        btShuttle.setOnAction(event -> {
            mode = 4;
            ShuttleBoundary shuttleBoundary = new ShuttleBoundary();
            shuttleBoundary.setGame(stage);

        });

        pane.getChildren().addAll(btNormal, btSpeed, btRandomBlocks, btShuttle);
        stage.setScene(new Scene(pane, 500, 500));
    }

    /**
     * Create new gamePane
     * Set gamePane size
     * Set gamePane background
     * Set gamePane location in the boardPane
     * Add gamePane to boardPane
     */
    void setGamePane() {
        int mapRows = 19;
        int mapCols = 10;
        gamePane = new GridPane();
        gamePane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        //Set size
        gamePane.setPrefSize(mapCols * blockSize, mapRows * blockSize);
        gamePane.setOpacity(0.5);
        gamePane.setLayoutX(40);
        gamePane.setLayoutY(40);
        boardPane.getChildren().add(gamePane);
    }

    /**
     * Create new boardPane
     * Set boardPane background
     */
    void setBoardPane() {
        boardPane = new Pane();//Create boardPane
        //Set game background
        Image image = new Image("file:src/PlayBackground.gif");
        boardPane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.ROUND,
                BackgroundRepeat.ROUND, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    /**
     * Create new nextBlockPane
     * Set nextBlockPane background
     * Set nextBlockPane size
     * Set nextBlockPane location in boardPane
     */
    void setNextBlockPane() {
        int row, col;
        if (mode == 3) {
            row = 5;//Cell row
            col = 5;//Cell col
        } else {
            row = 4;
            col = 4;
        }
        nextBlockPane = new GridPane();
        nextBlockPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        nextBlockPane.setOpacity(0.5);
        //Set size
        nextBlockPane.setPrefSize(col * blockSize * 1.5, row * blockSize * 1.5);
        nextBlockPane.setLayoutX(355);
        nextBlockPane.setLayoutY(40);
        boardPane.getChildren().add(nextBlockPane);
    }

    /**
     * Set the label Score
     */
    void setLblScore() {
        lblScore = new Label("Score: " + score);
        lblScore.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        lblScore.setStyle("-fx-border-color: white; -fx-border-width: 5");
        lblScore.setTextFill(Color.WHITE);
        lblScore.setLayoutX(330);
        lblScore.setLayoutY(240);
        boardPane.getChildren().add(lblScore);
    }

    /**
     * Set the label Time
     * @param time Play time
     */
    void setLblTime(long time) {
        int hour = (int) time / (60 * 60 * 1000);
        int minute = ((int) time / 60000) % 60;
        int sec = ((int) time / 1000) % 60;
        this.time = time;
        strTime = "Time: " + String.valueOf(hour) + ":" + String.valueOf(minute) + ":" + String.valueOf(sec);
        lblTime.setText(strTime);
        lblTime.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        lblTime.setStyle("-fx-border-color: white; -fx-border-width: 5");
        lblTime.setTextFill(Color.WHITE);
        lblTime.setLayoutX(330);
        lblTime.setLayoutY(330);
        boardPane.getChildren().remove(lblTime);
        boardPane.getChildren().add(lblTime);
    }

    /**
     * Set the first time label
     */
    void setLblTime() {
        lblTime.setText("Time: 0:0:0");
        lblTime.setFont(Font.font("Arial Black", FontWeight.BOLD, 30));
        lblTime.setStyle("-fx-border-color: white; -fx-border-width: 5");
        lblTime.setTextFill(Color.WHITE);
        lblTime.setLayoutX(330);
        lblTime.setLayoutY(330);
        boardPane.getChildren().add(lblTime);
    }

    /**
     * Set buttons
     *
     * @param button Button nodes
     * @param X      LocationX in pane
     * @param Y      LocationY in pane
     * @param x      Button size
     * @param y      Button size
     */
    static void setButton(Button button, int X, int Y, int x, int y) {
        button.setLayoutX(X);
        button.setLayoutY(Y);
        button.setScaleX(x);
        button.setScaleY(y);
    }

    /**
     * Leave game scene
     * Repaint first scene
     *
     * @param firstSceneStage PrimaryStage
     */
    void leave(Stage firstSceneStage) {
        start(firstSceneStage);
    }

    /**
     * Remove all child nodes in gaamePane
     * Add blocks to gamePane
     */
    void initGameView() {
        gamePane.getChildren().clear();
        for (int i = 4; i < map.length - 2; i++) {
            for (int j = 2; j < map[i].length - 2; j++) {
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                rectangle.setArcHeight(15);
                rectangle.setArcWidth(15);
                if (stableMap[i][j] != 9) {
                    rectangle.setFill(getFillColor(map[i][j]));
                } else rectangle.setFill(getFillColor(stableMap[i][j]));
                gamePane.add(rectangle, j - 2, i - 4);
            }
        }
    }

    /**
     * Remove all child nodes in nextBlockPane
     * Add blocks to nextBlockPane
     */
    void nextBlockView() {
        nextBlockPane.getChildren().clear();
        for (int i = 0; i < cell.length; i++) {
            for (int j = 0; j < cell[0].length; j++) {
                Rectangle rectangle = new Rectangle(blockSize * 1.5, blockSize * 1.5);
                rectangle.setArcHeight(10);
                rectangle.setArcWidth(10);
                if (block.getNextRandomBlock()[i * cell.length + j] == 1)
                    rectangle.setFill(Color.ORANGE);
                else rectangle.setFill(Color.GHOSTWHITE);
                nextBlockPane.add(rectangle, j, i);
            }
        }
    }

    /**
     * According to number of the maps,pick color to fill the rectangle
     *
     * @param i Number of the maps
     * @return Color
     */
    Paint getFillColor(int i) {
        Paint paint = Color.WHITE;
        switch (i) {
            case 0://No blocks
                paint = Color.TRANSPARENT;
                break;
            case 1://Blocks in map[][] Dropping blocks
                paint = Color.PURPLE;
                break;
            case 9://Blocks in stableMap[][] Dropped blocks
                paint = Color.ORANGE;
                break;
            case 5://Blocks in map[][] Where the blocks will drop
                paint = Color.DARKGRAY;
                break;
        }
        return paint;
    }

    /**
     * Change block[] into Cell[][]
     *
     * @param block Block[]
     */
    void copy(int[] block) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(block, i * 4, cell[i], 0, cell[0].length);
        }
    }

    /**
     * Draw the map
     * Game map size 19 * 10
     */
    void drawWall() {
        for (int i = 4; i <= 22; i++) {
            map[i][1] = 2;
            stableMap[i][1] = 2;
            map[i][12] = 3;
            stableMap[i][12] = 3;
        }
        for (int j = 2; j <= 11; j++) {
            map[23][j] = 4;
            stableMap[23][j] = 4;
        }
    }

    /**
     * The location of the block in the map
     */
    private void location() {
        for (int i = 0; i < cell.length; i++) {
            for (int j = 0; j < cell[0].length; j++) {
                if (cell[i][j] == 1)
                    map[x + i][y + j] = 1;
            }
        }
    }

    /**
     * Put the block in the map
     * Initialize the block location
     */
    private void createBlock() {
        x = 0;
        y = 5;
        location();
        while (map[4][5] == 0 && map[4][6] == 0
                && map[4][7] == 0 && map[4][8] == 0) {
            x = x + 1;
            location();
            for (int i = x - 1; i >= 0; i--) {
                for (int j = 5; j <= 8; j++)
                    map[i][j] = 0;
            }
        }
    }

    /**
     * Refresh the map
     */
    private void newMap() {
        for (int i = 0; i <= 22; i++) {
            for (int j = 2; j <= 11; j++) {
                map[i][j] = 0;
            }
        }
    }

    /**
     * refresh the game
     */
    private void newGames() {
        for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 13; j++) {
                map[i][j] = 0;
                stableMap[i][j] = 0;
            }
        }
        score = 0;
    }

    /**
     * Judge whether the game is over
     *
     * @return True over;False continue
     */
    public boolean IsGameOver() {
        boolean isGameOver = false;
        for (int j = 2; j < map[3].length - 2; j++) {
            if (stableMap[3][j] == 9) {
                isGameOver = true;
                break;
            }
        }
        return isGameOver;
    }

    void afterGameOver(Stage stage) {
        Pane gameOverPane = new Pane();
        Image image = new Image("file:src/GameOverImage.gif");
        ImageView imageView = new ImageView(image);
        imageView.setX(200);
        imageView.setY(200);
        gameOverPane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Label lblTitle = new Label("Game over!");
        lblTitle.setFont(Font.font("Arial Black", FontWeight.BOLD, 50));
        lblTitle.setLayoutX(50);
        lblTitle.setLayoutY(20);

        Label lblScore = new Label("Your Score: " + score);
        lblScore.setFont(Font.font("Arial Black", 30));
        lblScore.setLayoutX(50);
        lblScore.setLayoutY(150);

        Label lblTime = new Label(strTime);
        lblTime.setFont(Font.font("Arial Black", 30));
        lblTime.setLayoutX(50);
        lblTime.setLayoutY(250);

        Button btBack = new Button("Back");
        btBack.setStyle("-fx-border-color: white; -fx-background-color:grey; -fx-text-fill:white");
        Game.setButton(btBack, 100, 350, 3, 3);
        btBack.setOnAction(event -> leave(stage));

        gameOverPane.getChildren().addAll(imageView, lblTitle, lblScore, lblTime, btBack);
        stage.setScene(new Scene(gameOverPane, 500, 500));
    }

    /**
     * Whether the block is at bottom
     *
     * @return 1 dropping;0 bottom
     */
    private int feasibility() {
        if (countDown() == block.getNumOfBlock()) return 1;
        else return 0;
    }

    private void down() {
        if (feasibility() == 1) {
            x = x + 1;
            newMap();
            bottomPlus();
            location();
            initGameView();
        } else {
            corresponding();
            destroyLine();
            initGameView();
            //Create next block
            copy(block.applyNextRandomBlock());
            block.nextRandomBlock();
            createBlock();
            bottomPlus();
            initGameView();
        }
    }

    public void left() {
        if (feasibility() == 1) {
            if (countLeft() == block.getNumOfBlock()) {
                y = y - 1;
            }
            newMap();
            bottomPlus();
            location();
            if (gameSoundIsSelected)
                GameSound.playMusic();
            initGameView();
        } else {
            corresponding();
            destroyLine();
            initGameView();
        }
    }

    public void right() {
        if (feasibility() == 1) {
            if (countRight() == block.getNumOfBlock()) {
                y = y + 1;
            }
            newMap();
            bottomPlus();
            location();
            if (gameSoundIsSelected)
                GameSound.playMusic();
            initGameView();
        } else {
            corresponding();
            destroyLine();
            initGameView();
        }
    }

    private void bottom() {
        if (feasibility() == 1) {
            while (countDown() == block.getNumOfBlock()) {
                x = x + 1;
            }
            newMap();
            location();
            if (gameSoundIsSelected)
                GameSound.playMusic();
            initGameView();
        } else {
            corresponding();
            destroyLine();
            initGameView();
        }
    }

    /**
     * If the block is dropping,first judge whether the block can be rotated
     * Rotate,refresh the map,show '+',put the block into the map
     * If the block is at bottom,fix it to stableMap
     */
    private void rotate() {
        if (feasibility() == 1) {
            int m = 0;
            for (int a = 0; a < cell.length; a++) {
                for (int b = 0; b < cell[0].length; b++) {
                    if (block.getNextRotatedBlock()[a * 4 + b] == 1
                            && stableMap[x + a][y + b] == 0)
                        m = m + 1;
                }
            }
            if (m == block.getNumOfBlock()) {
                copy(block.rotateBlock());
            }
            newMap();
            bottomPlus();
            location();
            if (gameSoundIsSelected)
                GameSound.playMusic();
            initGameView();
        } else {
            corresponding();
            destroyLine();
            initGameView();
        }
    }

    /**
     * Hint the bottom position of the blocks
     */
    private void bottomPlus() {
        int temp = x;//record current block's location
        if (feasibility() == 1) {
            while (countDown() == block.getNumOfBlock()) {
                x = x + 1;
            }
            for (int i = 0; i < cell.length; i++) {
                for (int j = 0; j < cell[0].length; j++) {
                    if (cell[i][j] == 1)
                        map[x + i][y + j] = 5;
                }
            }
        }
        x = temp;//where the block is
    }

    /**
     * Compare the block with the blank of the map
     * If the location of the block is blank after moving,then move
     *
     * @return The number of the blanks
     */
    private int countDown() {
        int m = 0;
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (cell[a][b] == 1 && stableMap[x + a + 1][y + b] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    private int countLeft() {
        int m = 0;
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (cell[a][b] == 1 && stableMap[x + a][y + b - 1] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    private int countRight() {
        int m = 0;
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (cell[a][b] == 1 && stableMap[x + a][y + b + 1] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    /**
     * When the block is at bottom,fix the block to stableMap
     */
    private void corresponding() {
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (map[x + a][y + b] == 1)//the block's location
                    stableMap[x + a][y + b] = 9;//fix the block
            }
        }
        newMap();//refresh map to delete the block in map
    }

    /**
     * Destroy when a line is filled with blocks
     * Move the upper blocks
     * Compute the score
     */
    private void destroyLine() {
        double k = 0;
        for (int i = 4; i <= 22; i++) {
            int m = 0;
            for (int j = 2; j <= 11; j++) {
                if (stableMap[i][j] == 9) {
                    m = m + 1;
                    if (m == 10) {
                        for (j = 2; j <= 11; j++) {
                            stableMap[i][j] = 0;
                            for (int n = 0; n <= i - 4; n++) {
                                stableMap[i - n][j] = stableMap[i - (n + 1)][j];
                            }
                        }
                        k = k + 1;
                    }
                }
            }
        }
        score = score + (int) (k * 10 * Math.pow(2, (k - 1)));
        boardPane.getChildren().remove(lblScore);
        setLblScore();
    }

    /**
     * Use arrayList to record and sort the rank
     */
    private void playRankScore() {
        Comparator<Player> comparator = (o1, o2) -> Integer.compare(o2.getScore(), o1.getScore());
        playerList.sort(comparator);
        for (int i = 0, s = playerList.size(); i < s; i++) {
            if (i > 0 && playerList.get(i).getScore() == playerList.get(i - 1).getScore()) {
                playerList.get(i).setRank(playerList.get(i - 1).getRank());
            } else {
                playerList.get(i).setRank(i + 1);
            }
        }
    }

    private void playRankTime() {
        Comparator<Player> comparator = (o1, o2) -> Long.compare(o2.getTime(), o1.getTime());
        playerListTime.sort(comparator);
        for (int i = 0, s = playerListTime.size(); i < s; i++) {
            if (i > 0 && playerListTime.get(i).getTime() == playerListTime.get(i - 1).getTime()) {
                playerListTime.get(i).setRank(playerListTime.get(i - 1).getRank());
            } else {
                playerListTime.get(i).setRank(i + 1);
            }
        }
    }

    /**
     * Read the file which stored the rank
     *
     * @throws IOException fileNotFoundException
     */
    void read() throws IOException {
        File currentDict = new File("");
        System.out.println();
        System.out.println(currentDict.getAbsolutePath());
        file = new java.io.File("src/rank.txt");
        Scanner input = new Scanner(file);
        while (input.hasNext()) {
            Player temp = new Player(input.next(), Integer.parseInt(input.next()), Long.parseLong(input.next()));
            playerList.add(temp);
        }
        input.close();
    }

    void readTime() throws IOException {
        File currentDict = new File("");
        System.out.println();
        System.out.println(currentDict.getAbsolutePath());
        timeFile = new java.io.File("src/timeRank.txt");
        Scanner input = new Scanner(timeFile);
        while (input.hasNext()) {
            Player temp = new Player(input.next(), Integer.parseInt(input.next()), Long.parseLong(input.next()));
            playerListTime.add(temp);
        }
        input.close();
    }
}