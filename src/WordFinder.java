import java.awt.*;
import java.util.List;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//distlcn
public class WordFinder {


    public static void main(String[] args) {
        Board.gui = new CharGridGUI();

    }

    public static void play(char[][] bored, String given) {
        System.out.println("Playing");

        Board board = new Board(bored);

        String word = given.toUpperCase();


        List<String> boardLiterals = new ArrayList<>();
        List<Point> literalPoints = new ArrayList<>();
        int splitPoint;


        for (int i = 0; i < board.readRows().size(); i++) {
            for (String gaps : extractSubstringsR(board.readRows().get(i), word.length(), i)) {
                String[] parts = gaps.split("\\|");
                // Extract the string and the point
                boardLiterals.add(parts[0]);

                // Extract the numbers, split by the comma
                String[] pointParts = parts[1].split(",");
                int x = Integer.parseInt(pointParts[0]);
                int y = Integer.parseInt(pointParts[1]);

                // Create a Point object to store the x and y values
                literalPoints.add(new Point(x, y));
            }

        }


        splitPoint = literalPoints.size();
        for (int i = 0; i < board.readColumns().size(); i++) {
            for (String gaps : extractSubstringsC(board.readColumns().get(i), word.length(), i)) {
                String[] parts = gaps.split("\\|");
                // Extract the string and the point
                boardLiterals.add(parts[0]);  // "flarp"

                // Extract the numbers, split by the comma
                String[] pointParts = parts[1].split(",");
                int x = Integer.parseInt(pointParts[0]);
                int y = Integer.parseInt(pointParts[1]);

                // Create a Point object to store the x and y values
                literalPoints.add(new Point(x, y));
            }

        }
        //Map<String, Integer> possibleWords = getWords(board, given, boardLiterals, literalPoints, splitPoint);

        String noDupword = "";
        for (String g : boardLiterals) {
            if (g.charAt(0) != ' ') {
                noDupword += g;
            }
        }
        noDupword += word;
        noDupword = removeDuplicates(noDupword).replace(" ", "");
        System.out.println(noDupword);
        CountDownLatch latch = new CountDownLatch(noDupword.length());
        for (int i = 0; i < noDupword.length(); i++) {
            Thread t = new Thread(new FindWords(noDupword.charAt(i), board, boardLiterals, literalPoints, splitPoint, word, latch));
            t.start();
        }


        try {
            // Wait for all threads to finish
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        try {
            // Wait for all threads to finish
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread interrupted: " + e.getMessage());
        }

        String[] parts = FindWords.getResults().split(":");

        // Assigning values to variables
        int highScore = Integer.parseInt(parts[0]);
        String highWord = parts[1];
        Point point = new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        String highGapWord = parts[4];
        int numRow = Integer.parseInt(parts[5]);
        boolean row = Boolean.parseBoolean(parts[6]);
        String rowCol = (row) ? "Row" : "Column";

        System.out.printf("""
                Best Word: %s
                Score: %d
                %s: %d
                Layout: %s
                """, highWord, highScore, rowCol, numRow, highGapWord);

        System.out.println(point);
        board.putOnBoard(highWord, rowCol, point, highScore);

    }

    //Gets all the words from a given file and puts them in a list
    public static List<String> words(Path fileName) {
        List<String> words = new ArrayList<>();

        try {
            // Read all lines from the file
            words = Files.readAllLines(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return the list
        return words;

    }

    //removes duplicate letters from a word
    private static String removeDuplicates(String word) {
        // Use LinkedHashSet to maintain the order of characters
        Set<Character> uniqueChars = new LinkedHashSet<>();

        // Add each character to the set
        for (char c : word.toCharArray()) {
            uniqueChars.add(c);
        }

        // Build the result string
        StringBuilder sb = new StringBuilder();
        for (char c : uniqueChars) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static boolean isAWord(String word){
        if(word.isEmpty()) {return false;}
        if(word.length() == 1) {return true;}
        Path filePath = Paths.get("resources", "allWords", word.charAt(0) + "Words.txt");
        File raf = new File(filePath.toString());
        try(RandomAccessFile file = new RandomAccessFile(raf, "r")){
            long low = 0;
            long high = file.length();
            while(low <= high){
                long mid = (low + high) / 2;

                //Move to the middle of the file
                file.seek(mid);

                //Adjust to the beginning of the current line
                if(mid != 0){
                    file.readLine();
                }

                String line = file.readLine();

                // if we reach End of file, adjust high pointer
                if(line == null){
                    high = mid -1;
                    continue;
                }

                int comparison = line.compareTo(word);
                if(comparison == 0){
                    return true;
                }else if(comparison > 0){
                    high = mid -1;
                }
                else{
                    low = mid + 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static List<String> extractSubstringsR(String input, int number, int lineNum) {
        List<String> results = new ArrayList<>();
        int length = input.length();
        char[] word = input.toCharArray();
        int index = 0;
        for (int n = 0; n < length; n++) {
            if (n > 0) {
                if (Character.isLetter(word[n - 1])) {
                    continue;
                }
            }
            for (int j = 0; j < number; j++) {
                String output = "";
                int spaceCount = 0;
                for (int i = n; i < length; i++) {

                    if (word[i] == ' ') {
                        spaceCount++;
                        if (spaceCount > number - j) {
                            break;

                        }
                    }
                    output += word[i];
                    index = i;
                }
                if (!results.contains(output + "|" + lineNum + "," + index)) {
                    if (!(output.replace(" ", "")).isEmpty() && output.contains(" ")) {
                        results.add(output + "|" + lineNum + "," + index);
                    }
                }

            }

        }
        return results;
    }

    public static List<String> extractSubstringsC(String input, int number, int lineNum) {
        List<String> results = new ArrayList<>();
        int length = input.length();
        char[] word = input.toCharArray();
        int index = 0;

        for (int n = 0; n < length; n++) {

            if (n > 0) {
                if (Character.isLetter(word[n - 1])) {
                    continue;
                }
            }
            for (int j = 0; j < number; j++) {
                String output = "";
                int spaceCount = 0;
                for (int i = n; i < length; i++) {

                    if (word[i] == ' ') {
                        spaceCount++;
                        if (spaceCount > number - j) {
                            break;

                        }
                    }
                    output += word[i];
                    index = i;
                }
                if (!results.contains(output + "|" + (index + 1) + "," + (lineNum + 1))) {
                    if (!(output.replace(" ", "")).isEmpty() && output.contains(" ")) {
                        results.add(output + "|" + (index + 1) + "," + (lineNum + 1));
                    }
                }

            }

        }
        return results;
    }

    public static String getMultiplier(int x, int y) {
        // Initialize the 15x15 Scrabble board
        String[][] multipliers = new String[15][15];
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
        if (x >= 0 && x < 15 && y >= 0 && y < 15) {
            return multipliers[x][y];
        } else {
            return "Invalid position";
        }
    }

    public static int getCharPoints(char letter) {
        // Convert the letter to uppercase to handle both cases
        letter = Character.toUpperCase(letter);

        return switch (letter) {
            case 'A', 'E', 'I', 'O', 'U', 'L', 'N', 'S', 'T', 'R' -> 1;
            case 'D', 'G' -> 2;
            case 'B', 'C', 'M', 'P' -> 3;
            case 'F', 'H', 'V', 'W', 'Y' -> 4;
            case 'K' -> 5;
            case 'J', 'X' -> 8;
            case 'Q', 'Z' -> 10;
            default -> 0; // For non-alphabetic characters
        };
    }
}