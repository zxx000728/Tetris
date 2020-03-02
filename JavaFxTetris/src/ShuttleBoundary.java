import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class ShuttleBoundary extends Game {
    private int[][] map2 = new int[24][10];
    private int[][] stableMap = new int[24][10];
    private int x, y;//block location
    private Blocks block = new Blocks();

    void setGame(Stage stage) {

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

    void setStartTimerTask(Stage stage) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    nextBlockView();
                    setLblTime(System.currentTimeMillis() - startTime);
                    count++;

                    down();
                    if (feasibility() == 0) {
                        if (IsGameOver()) {
                            timer.cancel();
                            if (gameSoundIsSelected)
                                GameSound.playGameOverSound();
                            afterGameOver(stage);
                        }
                    }
                });
            }
        }, 0, speed);
    }

    public boolean IsGameOver() {
        boolean isGameOver = false;
        for (int j = 0; j < map2[0].length; j++) {
            if (stableMap[3][j] == 9) {
                isGameOver = true;
                break;
            }
        }
        return isGameOver;
    }

    void setContinueTimerTask(Stage stage) {
        startTime = System.currentTimeMillis();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    nextBlockView();
                    setLblTime(System.currentTimeMillis() - startTime + tempTime);
                    count++;

                    down();
                    if (feasibility() == 0) {
                        if (IsGameOver()) {
                            timer.cancel();
                            if (gameSoundIsSelected) {
                                GameSound.playGameOverSound();
                            }
                            afterGameOver(stage);
                        }
                    }
                });
            }
        }, 0, speed);
    }

    void initGameView() {
        gamePane.getChildren().clear();
        for (int i = 4; i < map2.length - 1; i++) {
            for (int j = 0; j < map2[0].length; j++) {
                Rectangle rectangle = new Rectangle(blockSize, blockSize);
                rectangle.setArcHeight(15);
                rectangle.setArcWidth(15);
                if (stableMap[i][j] != 9) {
                    rectangle.setFill(getFillColor(map2[i][j]));
                } else rectangle.setFill(getFillColor(stableMap[i][j]));
                gamePane.add(rectangle, j, i - 4);
            }
        }
    }

    public void drawWall() {
        for (int j = 0; j <= 9; j++) {
            stableMap[23][j] = 4;
            map2[23][j] = 4;
        }
    }

    private void createBlock() {
        x = 0;
        y = 3;
        location();
        while (map2[4][3] == 0 && map2[4][4] == 0 && map2[4][5] == 0 && map2[4][6] == 0) {
            x = x + 1;
            location();
            for (int i = x - 1; i >= 0; i--) {
                for (int j = 3; j <= 6; j++)
                    map2[i][j] = 0;
            }
        }
    }

    private void location() {
        for (int i = 0; i <= 3; i++) {
            for (int j = 0; j <= 3; j++) {
                if (cell[i][j] == 1) {
                    map2[x + i][Math.floorMod(y + j, 10)] = 1;
                }
            }
        }
    }

    private void newMap() {
        for (int i = 0; i <= 22; i++) {
            for (int j = 0; j <= 9; j++) {
                map2[i][j] = 0;
            }
        }
    }

    private void corresponding() {
        if (feasibility() == 0) {
            for (int a = 0; a <= 3; a++) {
                for (int b = 0; b <= 3; b++) {
                    if (map2[x + a][Math.floorMod(y + b, 10)] == 1)
                        stableMap[x + a][Math.floorMod(y + b, 10)] = 9;
                }
            }
            newMap();
        }
    }

    private int countDown() {
        int m = 0;
        for (int a = 0; a <= 3; a++) {
            for (int b = 0; b <= 3; b++) {
                if (cell[a][b] == 1
                        && stableMap[x + a + 1][Math.floorMod(y + b, 10)] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    private int feasibility() {
        if (countDown() == 4) return 1;
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

    private void bottomPlus() {
        int a = x;
        if (feasibility() == 1) {
            while (countDown() == block.getNumOfBlock()) {
                x = x + 1;
            }
            for (int i = 0; i <= 3; i++) {
                for (int j = 0; j <= 3; j++) {
                    if (cell[i][j] == 1)
                        map2[x + i][Math.floorMod(y + j, 10)] = 5;
                }
            }
        }
        x = a;
    }

    private int countRight() {
        int m = 0;
        for (int a = 0; a <= 3; a++) {
            for (int b = 0; b <= 3; b++) {
                if (cell[a][b] == 1
                        && stableMap[x + a][Math.floorMod(y + b + 1, 10)] == 0)
                    m = m + 1;
            }
        }
        return m;
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

    private int countLeft() {
        int m = 0;
        for (int a = 0; a <= 3; a++) {
            for (int b = 0; b <= 3; b++) {
                if (cell[a][b] == 1
                        && stableMap[x + a][Math.floorMod(y + b - 1, 10)] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    public void left() {
        if (feasibility() == 1) {
            if (countLeft() == 4) {
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

    private void rotate() {
        if (feasibility() == 1) {
            int m = 0;
            for (int a = 0; a <= 3; a++) {
                for (int b = 0; b <= 3; b++) {
                    if (block.getNextRotatedBlock()[a * 4 + b] == 1
                            && stableMap[x + a + 1][Math.floorMod(y + b, 10)] == 0)
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

    private void destroyLine() {
        double k = 0;
        for (int i = 4; i <= 22; i++) {
            int m = 0;
            for (int j = 0; j <= 9; j++) {
                if (stableMap[i][j] == 9) {
                    m = m + 1;
                    if (m == 10) {
                        for (j = 0; j <= 9; j++) {
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

    private void newGames() {
        for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 9; j++) {
                map2[i][j] = 0;
                stableMap[i][j] = 0;
            }
        }
        score = 0;
    }
}
