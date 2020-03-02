class RandomBlocks {
    private int[][] block = new int[3][5];//create random blocks in this array
    private int row;//the actual row of the block
    private int column;//the actual column of the array
    private int a, b;//the first cell in the row,the first cell in the column
    private int[][] myBlock;//the actual block
    private double rotateCenterRow, rotateCenterColumn;

    /**
     * create a connected block in the 3 * 5 array block[][]
     */
    private void createMyBlock() {
        do {
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 4; j++) {
                    block[i][j] = (int) (0 + Math.random() * 2);
                }
            }
        } while (getCount(block) != 1);
    }

    /**
     * @param A block[][]
     * @return If result == 1,the cells are all connected
     */
    private int getCount(int[][] A) {
        int result = 0;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (A[i][j] == 1) {
                    result++;
                    erase(A, i, j);
                }
            }
        }
        return result;
    }

    /**
     * Judge whether the cells are connected
     *
     * @param A block[][]
     * @param i rows
     * @param j columns
     */
    private void erase(int[][] A, int i, int j) {
        A[i][j] = 6;
        while (i - 1 >= 0 && A[i - 1][j] == 1) {
            erase(A, i - 1, j);
        }
        while (i + 1 < A.length && A[i + 1][j] == 1) {
            erase(A, i + 1, j);
        }
        while (j - 1 >= 0 && A[i][j - 1] == 1) {
            erase(A, i, j - 1);
        }
        while (j + 1 < A[0].length && A[i][j + 1] == 1) {
            erase(A, i, j + 1);
        }
    }

    /**
     * Get the actual row and column of the block
     */
    private void actualBlock() {
        int numY = 0, x = 0;
        int numX = 0, y = 0;
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 4; j++) {
                if (block[i][j] == 0) {
                    numY++;
                }
            }
            if (numY == 5) {
                x++;
            }
            numY = 0;
        }
        for (int j = 0; j <= 4; j++) {
            for (int i = 0; i <= 2; i++) {
                if (block[i][j] == 0) {
                    numX++;
                }
            }
            if (numX == 3) {
                y++;
            }
            numX = 0;
        }

        row = 3 - x;//final row
        column = 5 - y;//final col
    }

    /**
     * Get the first cell in the block's rows and columns
     */
    private void firstCell() {
        Row:
        {
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 4; j++) {
                    if (block[i][j] == 6) {
                        a = i;
                        break Row;
                    }
                }
            }
        }
        Column:
        {
            for (int j = 0; j <= 4; j++) {
                for (int i = 0; i <= 2; i++) {
                    if (block[i][j] == 6) {
                        b = j;
                        break Column;
                    }
                }
            }
        }
    }

    /**
     * create the actual block according to the actual row and column
     */
    private void myBlock() {
        myBlock = new int[row][column];
        for (int i = 0; i <= row - 1; i++) {
            if (column - 1 + 1 >= 0) System.arraycopy(block[i + a], b, myBlock[i], 0, column - 1 + 1);
        }//final block
    }

    /**
     * get the rotating center of the actual block
     */
    private void getCenter() {
        if ((row - 1) % 2 == 0 && (column - 1) % 2 == 0) {
            rotateCenterRow = (row - 1) / 2.0;
            rotateCenterColumn = (column - 1) / 2.0;
        } else if (row % 2 == 0 && column % 2 == 0) {
            rotateCenterRow = (row - 1) / 2.0;
            rotateCenterColumn = (column - 1) / 2.0;
        } else if (row % 2 == 0 && column % 2 != 0) {
            rotateCenterRow = row / 2.0;
            rotateCenterColumn = column / 2.0 - 0.5;
        } else {
            rotateCenterRow = (row - 1) / 2.0;
            rotateCenterColumn = column / 2.0 - 1;
        }
    }

    /**
     * Put the actual block into a 5 * 5 array
     * Put the actual block's rotating center to the array's rotating center
     *
     * @return the 5 * 5 cell
     */
    int[][] setCell() {
        int[][] cell = new int[5][5];
        createMyBlock();
        actualBlock();
        firstCell();
        myBlock();
        getCenter();
        if (row % 2 == 0 && column % 2 == 0) {
            int x = 0;
            int y = 0;
            while (true) {
                if (rotateCenterRow != 1.5) {
                    x = x + 1;
                    rotateCenterRow = rotateCenterRow + 1;
                } else break;
            }
            while (true) {
                if (rotateCenterColumn != 1.5) {
                    y = y + 1;
                    rotateCenterColumn = rotateCenterColumn + 1;
                } else break;
            }
            for (int i = 0; i < row; i++) {
                if (column >= 0) System.arraycopy(myBlock[i], 0, cell[i + x], y, column);
            }
        } else {
            int x = 0;
            int y = 0;
            while (true) {
                if (rotateCenterRow != 2) {
                    x = x + 1;
                    rotateCenterRow = rotateCenterRow + 1;
                } else break;
            }
            while (true) {
                if (rotateCenterColumn != 2) {
                    y = y + 1;
                    rotateCenterColumn = rotateCenterColumn + 1;
                } else break;
            }
            for (int i = 0; i < row; i++) {
                if (column >= 0) System.arraycopy(myBlock[i], 0, cell[i + x], y, column);
            }
        }
        return cell;
    }

}
