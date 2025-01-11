import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class FindWords implements Runnable {
    char letter;
    Board board;
    List<String> gapList;
    List<Point> pointList;
    int splitPoint;
    String letsGiven;

    //Stuff given later that still needs to be static ig
    static int highScore = 0;
    static String highWord = "";
    static Point highPoint;
    static String highGapWord;
    static int numRow;
    static boolean row;


    //latch
    private final CountDownLatch latch;

    //Synchronize Object
    private static final Object lock = new Object();


    public FindWords(char s, Board b, List<String> gapList, List<Point> pointList, int splitPoint, String letsGiven, CountDownLatch latch) {
        this.letter = s;
        this.board = b;
        this.gapList = gapList;
        this.pointList = pointList;
        this.splitPoint = splitPoint;
        this.letsGiven = letsGiven;
        this.latch = latch;

    }

    public static String getResults() {
        String toReturn = String.format("%d:%s:%d:%d:%s:%d:%b", highScore, highWord, highPoint.x, highPoint.y, highGapWord, numRow, row);
        highScore = 0;
        highWord = "";
        highGapWord = "";
        numRow = 0;
        return toReturn;
    }

    public void run() {

        Map<String, Integer> possibleWords = new HashMap<>();
        String lastHigh = highWord;
        try {
            List<String> words = new ArrayList<>();
            try {
                // Read all lines from the file
                words = Files.readAllLines(Paths.get("resources", "allWords", letter + "Words.txt"));
            } catch (IOException e) {
                System.out.println(letter + " is not a letter");
            }

            for (int g = 0; g < gapList.size(); g++) {
                for (String s : words) {
                    if (!matchesPattern(gapList.get(g), s)) {
                        continue;
                    }
                    if (containsAllLetters(letsGiven + gapList.get(g).replace(" ", ""), s)) {
                        if (matchesSpot(board, splitPoint, s, pointList.get(g), g)) {
                            int wordScore = scoreWord(board, s, g, splitPoint, pointList.get(g), gapList.get(g), letsGiven);

                            if (!possibleWords.containsKey(s) || wordScore > possibleWords.get(s)) {
                                possibleWords.put(s, wordScore);

                                if (highScore < wordScore) {
                                    synchronized (lock) {
                                        row = g < splitPoint;
                                        highScore = wordScore;
                                        highPoint = pointList.get(g);
                                        highWord = s;
                                        highGapWord = gapList.get(g).replace(' ', '-');
                                        if (row) {
                                            numRow = pointList.get(g).y;
                                        } else {
                                            numRow = pointList.get(g).x;
                                        }

                                    }
                                }
                            }
                        }
                    }
                }
            }
        } finally {
            latch.countDown();
        }
    }

    public boolean matchesPattern(String input1, String input2) {
        // Check if the lengths of the two inputs are equal
        if (input1.length() != input2.length()) {
            return false;
        }

        for (int i = 0; i < input1.length(); i++) {
            char patternChar = input1.charAt(i);
            char inputChar = input2.charAt(i);

            // If the pattern character is an underscore or dash, it can match any character
            if (patternChar == ' ') {
                continue;
            }
            // If it is a letter, it must match exactly (case-insensitive)
            if (Character.toLowerCase(patternChar) != Character.toLowerCase(inputChar)) {
                return false;
            }
        }

        return true; // All characters matched
    }

    public boolean containsAllLetters(String word1, String word2) {
        // Convert both strings to character arrays
        char[] charArray1 = word1.toLowerCase().toCharArray();
        char[] charArray2 = word2.toLowerCase().toCharArray();

        // Count occurrences of each character in `word1`
        int[] count1 = new int[256];
        for (char c : charArray1) {
            count1[c]++;
        }

        // Check if `word1` has all characters from `word2`
        for (char c : charArray2) {
            if (count1[c] == 0) {
                return false; // If any character is missing, return false
            }
            count1[c]--;
        }

        return true; // All characters are present
    }

    public boolean matchesSpot(Board board, int splitPoint, String s, Point point, int g) {
        //[row][column]
        synchronized (lock) {
            if (g < splitPoint) {
                for (int i = s.length(); i > 0; i--) {

                    Point focus = new Point(point.x, point.y - s.length() + i);
                    char ogChar = board.charAt(focus);
                    board.setBoard(focus, s.charAt(i - 1));
                    if (!board.checkColumn(focus)) {

                        board.setBoard(focus, ogChar);
                        return false;
                    }
                    board.setBoard(focus, ogChar);
                }
            } else {
                for (int i = s.length(); i > 0; i--) {
                    Point focus = new Point(point.x - s.length() + i - 1, point.y - 1);
                    char ogChar = board.charAt(focus);
                    board.setBoard(focus, s.charAt(i - 1));
                    if (!board.checkRow(focus)) {
                        board.setBoard(focus, ogChar);
                        return false;
                    }
                    board.setBoard(focus, ogChar);

                }
            }

            return true;
        }
    }

    public int scoreWord(Board board, String s, int g, int splitPoint, Point point, String gapWord, String given) {
        //[row][column]
        int score = 0;
        int multiplier = 1;
        int rowadd = 0;
        //Get score of word played
        if (g >= splitPoint - 1) {
            for (int i = s.length(); i > 0; i--) {
                int scoreAdd = WordFinder.getCharPoints(s.charAt(i - 1));
                Point focusPoint = new Point(point.x - s.length() + i - 1, point.y - 1);
                int wMultiply = 1;
                String boardMultiplier = getMultiplier(focusPoint);
                if (Character.isAlphabetic(board.charAt(focusPoint))) {
                    score += scoreAdd;
                    continue;
                }
                switch (boardMultiplier) {
                    case "doubleWord":
                        multiplier *= 2;
                        wMultiply = 2;
                        break;
                    case "tripleWord":
                        multiplier *= 3;
                        wMultiply = 3;
                        break;
                    case "doubleLetter":
                        scoreAdd *= 2;
                        break;
                    case "tripleLetter":
                        scoreAdd *= 3;
                        break;
                    default:
                }
                score += scoreAdd;
                if(board.scoreVertical(focusPoint, s) != 0){
                    rowadd += (board.scoreVertical(focusPoint, s) + scoreAdd) * wMultiply;
                }

            }

        } else {
            for (int i = s.length(); i > 0; i--) {
                char c = s.charAt(i - 1);
                int scoreAdd = WordFinder.getCharPoints(c);
                Point focusPoint = new Point(point.x, point.y - s.length() + i);
                String boardMultiplier = getMultiplier(focusPoint);
                int wMultiply = 1;

                // if the letter is a word that has already been played
                if (Character.isAlphabetic(board.charAt(focusPoint))) {
                    score += scoreAdd;
                    continue;
                }
                //get the multiplier on the board
                switch (boardMultiplier) {
                    case "doubleWord":
                        multiplier *= 2;
                        wMultiply = 2;
                        break;
                    case "tripleWord":
                        multiplier *= 3;
                        wMultiply = 3;
                        break;
                    case "doubleLetter":
                        scoreAdd *= 2;
                        break;
                    case "tripleLetter":
                        scoreAdd *= 3;
                        break;
                    default:
                }
                score += scoreAdd;
                if(board.scoreHorizontal(focusPoint, s) != 0){
                    rowadd += (board.scoreHorizontal(focusPoint, s) + scoreAdd) * wMultiply;
                }

            }
        }
        score *= multiplier;
        score += rowadd;
        score += (gapWord.replaceAll("[^ ]", "").length() == given.length()) ? 50 : 0;

        return score;
    }

    public String getMultiplier(Point point) {
        // Initialize the 15x15 Scrabble board
        String[][] multipliers = new String[15][15];
        int row = point.x;
        int col = point.y;
        // Fill the array with default values ("normal" represents no multiplier)
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                multipliers[r][c] = "normal";
            }
        }

        // Set the positions for doubleWord
        int[][] doubleWordPositions = {{1, 1}, {2, 2}, {3, 3}, {4, 4}, {7, 7}, {10, 10}, {11, 11}, {12, 12}, {13, 13}, {1, 13}, {2, 12}, {3, 11}, {4, 10}, {10, 4}, {11, 3}, {12, 2}, {13, 1}};
        for (int[] pos : doubleWordPositions) {
            multipliers[pos[0]][pos[1]] = "doubleWord";
        }

        // Set the positions for tripleWord
        int[][] tripleWordPositions = {{0, 0}, {0, 7}, {0, 14}, {7, 0}, {7, 14}, {14, 0}, {14, 7}, {14, 14}};
        for (int[] pos : tripleWordPositions) {
            multipliers[pos[0]][pos[1]] = "tripleWord";
        }

        // Set the positions for doubleLetter
        int[][] doubleLetterPositions = {{0, 3}, {0, 11}, {2, 6}, {2, 8}, {3, 0}, {3, 7}, {3, 14}, {6, 2}, {6, 6}, {6, 8}, {6, 12}, {7, 3}, {7, 11}, {8, 2}, {8, 6}, {8, 8}, {8, 12}, {11, 0}, {11, 7}, {11, 14}, {12, 6}, {12, 8}, {14, 3}, {14, 11}};
        for (int[] pos : doubleLetterPositions) {
            multipliers[pos[0]][pos[1]] = "doubleLetter";
        }

        // Set the positions for tripleLetter
        int[][] tripleLetterPositions = {{1, 5}, {1, 9}, {5, 1}, {5, 5}, {5, 9}, {5, 13}, {9, 1}, {9, 5}, {9, 9}, {9, 13}, {13, 5}, {13, 9}};
        for (int[] pos : tripleLetterPositions) {
            multipliers[pos[0]][pos[1]] = "tripleLetter";
        }

        // Return the multiplier at the given row and column
        if (row >= 0 && row < 15 && col >= 0 && col < 15) {
            return multipliers[row][col];
        } else {
            return "Invalid position";
        }
    }

}
