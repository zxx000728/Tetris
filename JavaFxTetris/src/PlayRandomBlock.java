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

public class PlayRandomBlock extends Game {
    private RandomBlocks randomBlocks = new RandomBlocks();
    private int[][] cell = new int[5][5];
    private int[][][] showCell = new int[7][5][5];//to store the next 7 blocks

    private void createRandomBlock() {
        for (int i = 0; i <= 6; i++) {
            showCell[i] = randomBlocks.setCell();
        }
    }

    private void createNextRandomBlock() {
        System.arraycopy(showCell, 1, showCell, 0, 6);
        showCell[6] = randomBlocks.setCell();
    }

    void setStartTimerTask(Stage stage) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    setLblTime(System.currentTimeMillis() - startTime);
                    count++;
                    nextBlockView();
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
//                    cell=showCell[0];
//                    createNextRandomBlock();
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
                createRandomBlock();
                nextBlockView();
                cell = showCell[0];
                createNextRandomBlock();
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

    void nextBlockView() {
        nextBlockPane.getChildren().clear();
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                Rectangle rectangle = new Rectangle(blockSize * 1.5, blockSize * 1.5);
                rectangle.setArcHeight(10);
                rectangle.setArcWidth(10);
                if (showCell[0][i][j] == 6) {
                    rectangle.setFill(Color.ORANGE);
                } else rectangle.setFill(Color.GHOSTWHITE);
                nextBlockPane.add(rectangle, j, i);
            }
        }
    }

    public void drawWall() {
        for (int i = 4; i <= 22; i++) {
            stableMap[i][1] = 2;
            stableMap[i][12] = 3;
            map[i][1] = 2;
            map[i][12] = 3;
        }
        for (int j = 2; j <= 11; j++) {
            map[23][j] = 4;
            stableMap[23][j] = 4;
        }
    }

    private int numOfBlock() {
        int num = 0;
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                if (cell[i][j] == 6)
                    num++;
            }
        }
        return num;
    }

    private void createBlock() {
        x = 0;
        y = 3;
        location();
        while (map[5][3] == 0 && map[5][4] == 0 && map[5][5] == 0
                && map[5][6] == 0 && map[5][7] == 0) {
            x = x + 1;
            location();
            for (int i = x - 1; i >= 0; i--) {
                for (int j = 3; j < 3 + cell.length; j++) {
                    map[i][j] = 0;
                }
            }
        }
    }

    private void location() {
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                if (cell[i][j] == 6)
                    map[x + i][y + j] = 1;
            }
        }
    }

    private int feasibility() {
        if (countDown() == numOfBlock()) return 1;
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
            cell = showCell[0];
            createNextRandomBlock();
            createBlock();
            bottomPlus();
            initGameView();
        }
    }

    public void left() {
        if (feasibility() == 1) {
            if (countLeft() == numOfBlock()) {
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
            if (countRight() == numOfBlock()) {
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
            while (countDown() == numOfBlock()) {
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


    private int[][] nextRotatedBlock() {
        int[][] block1 = new int[5][5];
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j <= 4; j++) {
                block1[4 - j][4 - i] = cell[i][4 - j];
            }
        }
        return block1;
    }

    private void rotate() {
        if (feasibility() == 1) {
            int m = 0;
            for (int a = 0; a <= 4; a++) {
                for (int b = 0; b <= 4; b++) {
                    if (nextRotatedBlock()[a][b] == 6 && stableMap[x + a + 1][y + b] == 0)
                        m = m + 1;
                }
            }
            if (m == numOfBlock()) {
                cell = nextRotatedBlock();
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

    private void bottomPlus() {
        int temp = x;//record current block's location
        if (feasibility() == 1) {
            while (countDown() == numOfBlock()) {
                x = x + 1;
            }
            for (int i = 0; i < cell.length; i++) {
                for (int j = 0; j < cell[0].length; j++) {
                    if (cell[i][j] == 6)
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
                if (cell[a][b] == 6 && stableMap[x + a + 1][y + b] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    private int countLeft() {
        int m = 0;
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (cell[a][b] == 6 && stableMap[x + a][y + b - 1] == 0)
                    m = m + 1;
            }
        }
        return m;
    }

    private int countRight() {
        int m = 0;
        for (int a = 0; a < cell.length; a++) {
            for (int b = 0; b < cell[0].length; b++) {
                if (cell[a][b] == 6 && stableMap[x + a][y + b + 1] == 0)
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

    private void destroyLine() {
        double k = 0;
        for (int i = 5; i <= 22; i++) {
            int m = 0;
            for (int j = 2; j <= 11; j++) {
                if (stableMap[i][j] == 9) {
                    m = m + 1;
                    if (m == 10) {
                        for (j = 2; j <= 11; j++) {
                            stableMap[i][j] = 0;
                            for (int n = 0; n <= i - 5; n++) {
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

    private void newMap() {
        for (int i = 0; i <= 22; i++) {
            for (int j = 2; j <= 11; j++) {
                map[i][j] = 0;
            }
        }
    }

    private void newGames() {
        for (int i = 0; i <= 23; i++) {
            for (int j = 0; j <= 13; j++) {
                map[i][j] = 0;
                stableMap[i][j] = 0;
            }
        }
        score = 0;
    }
}
