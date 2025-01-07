import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Board {

    char[][] board;
    static CharGridGUI gui;
    public static final String RESET = "\u001B[0m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public Board(char[][] board) {
        this.board = board;
    }

    public Board() {
        this.board = new char[15][15];
    }

    public void setBoard(int x, int y, char c) {
        if (x >= 15 || x < 0) {
            throw new IndexOutOfBoundsException("x out of bounds: " + x);
        } else if (y >= 15 || y < 0) {
            throw new IndexOutOfBoundsException("y out of bounds: " + y);
        }
        board[x][y] = c;
    }

    public void setBoard(Point p, char c) {
        board[p.x][p.y] = c;
    }


    public Map<Integer, String> readRows() {
        Map<Integer, String> lines = new HashMap<>();

        for (int i = 0; i < 15; i++) {
            String line = "";
            for (int j = 0; j < 15; j++) {
                line += this.board[i][j];
            }
            lines.put(i, line);
        }
        return lines;
    }

    public Map<Integer, String> readColumns() {
        //[row][column]
        Map<Integer, String> lines = new HashMap<>();
        for (int i = 0; i < 15; i++) {
            String line = "";
            for (int j = 0; j < 15; j++) {
                line += this.board[j][i];
            }
            lines.put(i, line);
        }
        return lines;
    }

    public char charAt(Point p) {
        if (p.x >= 15 || p.x < 0) {
            throw new IndexOutOfBoundsException("x out of bounds: " + p.x);
        } else if (p.y >= 15 || p.y < 0) {
            throw new IndexOutOfBoundsException("y out of bounds: " + p.y);
        }
        return this.board[p.x][p.y];
    }

    public char charAt(int x, int y) {
        if (x >= 15 || x < 0) {
            throw new IndexOutOfBoundsException("x out of bounds: " + x);
        } else if (y >= 15 || y < 0) {
            throw new IndexOutOfBoundsException("y out of bounds: " + y);
        }
        return board[x][y];
    }

    public void printBoard(Point spot) {

        String color = WHITE;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (i == spot.x && j == spot.y) {
                    color = CYAN;
                }
                if (board[i][j] == '\u0000' || board[i][j] == ' ') {
                    System.out.print(color + "[ ]" + RESET);
                } else {
                    System.out.printf(color + "%2s " + RESET, board[i][j]);
                }
                color = RESET;
            }
            System.out.println();
        }
    }

    public void printBoard(Point spot, char c) {

        String color = WHITE;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {

                char ogChar = charAt(i, j);
                if (i == spot.x && j == spot.y) {
                    color = CYAN;
                    board[i][j] = c;
                }
                if (board[i][j] == '\u0000' || board[i][j] == ' ') {
                    System.out.print(color + "[ ]" + RESET);
                    board[i][j] = ogChar;
                } else {
                    System.out.printf(color + "%2s " + RESET, board[i][j]);
                    board[i][j] = ogChar;
                }
                color = RESET;

            }
            System.out.println();
        }
        System.out.println();
    }

    public void printBoard() {

        String color = WHITE;
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                if (board[i][j] == '\u0000' || board[i][j] == ' ') {
                    System.out.print(color + "[ ]" + RESET);
                } else {
                    System.out.printf(color + "%2s " + RESET, board[i][j]);
                }
                color = RESET;
            }
            System.out.println();
        }
    }

    public boolean checkRow(Point place) {
        // [row][column]
        boolean wording = false;
        String row = "";
        String word = "";
        //check row word is in
        for (int i = 0; i < 15; i++) {
            row += board[place.x][i];
            if (board[place.x][i] == '\u0000' || board[place.x][i] == ' ') {
                wording = false;
                if (!word.isEmpty()) {
                    if (WordFinder.isNotWord(word)) {
                        return false;
                    }
                    word = "";
                }
            } else //board [row][column]
            {
                word += String.valueOf(board[place.x][i]);
                wording = true;
            }

        }
        if (wording) {
            if (WordFinder.isNotWord(word)) {
                return false;
            }
        }

        return true;

    }

    public boolean checkColumn(Point place) {
        boolean wording = false;
        String column = "";
        String word = "";
        for (int i = 0; i < 15; i++) {
            column += board[i][place.y];
            if (board[i][place.y] == '\u0000' || board[i][place.y] == ' ') {
                wording = false;
                if (!word.isEmpty()) {
                    if (WordFinder.isNotWord(word) && word.length() > 1) {
                        return false;
                    }
                    word = "";
                }
            } else //board [row][column]
            {
                word += String.valueOf(board[i][place.y]);
                wording = true;
            }

        }
        if (wording) {
            if (WordFinder.isNotWord(word)) {
                return false;
            }
        }
        return true;
    }

    public void putOnBoard(String s, String colOrRow, Point point, int score) {
        //Switch points
        gui.resetColor();
        if (colOrRow.equals("Column")) {
            for (int i = s.length(); i > 0; i--) {
                gui.lightText(point.x - s.length() + i - 1, point.y - 1);
                gui.lightCell(point.x - s.length() + i - 1, point.y - 1, Color.decode("#C4A484"));
                this.setBoard(point.x - s.length() + i - 1, point.y - 1, s.charAt(i - 1));
            }
        } else {
            for (int i = s.length(); i > 0; i--) {
                gui.lightText(point.x, point.y - s.length() + i);
                gui.lightCell(point.x, point.y - s.length() + i, Color.decode("#C4A484"));
                this.setBoard(point.x, point.y - s.length() + i, s.charAt(i - 1));
            }
        }
        gui.updateGrid(this.board);
        gui.score.setText(String.valueOf(score));
    }

    public int scoreHorizontal(Point point, String s) {
        int score = 0;
        int x = point.x;
        String word = "";

        boolean print = s.equalsIgnoreCase("Oxyphenbutazone");

        while (x - 1 >= 0 && Character.isLetter(charAt(x - 1, point.y))) {
            x--;
        }

        while (x + 1 < board[0].length && Character.isLetter(charAt(x + 1, point.y))) {
            score += WordFinder.getCharPoints(board[x + 1][point.y]);
            word += String.valueOf(board[x + 1][point.y]);
            x++;
        }

        if(word.length() <= 1){
            return 0;
        }

        return score;
    }

    public int scoreVertical(Point point, String s) {
        int score = 0;
        int y = point.y;
        String word = "";
        boolean print = s.equalsIgnoreCase("Oxyphenbutazone");

        while (y - 1 >= 0 && Character.isLetter(charAt(point.x, y - 1))) {
            y--;
        }


        while (y + 1 < board.length && Character.isLetter(charAt(point.x, y + 1))) {
            score += WordFinder.getCharPoints(board[point.x][y + 1]);
            word += String.valueOf(board[point.x][y + 1]);
            y++;
        }

        if(word.length() <= 1){
            return 0;
        }

        return score;
    }
}
