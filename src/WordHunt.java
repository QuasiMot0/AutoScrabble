import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WordHunt {
    static char[][] board;
    static ArrayList<String> words = new ArrayList<String>();
    static WordFinderGUI wf;


    public static void main(String[] args) {
        wf = new WordFinderGUI();
    }

    public static void getWords(char[][] funcboard) {
        //System.out.println("2");
        board = funcboard;
        char[][] open = {{' ', ' ', ' ', ' '}, {' ', ' ', ' ', ' '}, {' ', ' ', ' ', ' '}, {' ', ' ', ' ', ' '}};
        for (int i = 0; i < open.length; i++) {
            for (int j = 0; j < open[i].length; j++) {
                System.out.printf("%3s", board[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < open.length; i++) {
            for (int j = 0; j < open[i].length; j++) {
                recurWords("", new Point (i,j), open, 0);
            }
        }
        for (String word : words) {
            System.out.println(word);
        }
        words.clear();
        System.out.println("done innit");

    }

    public static void recurWords(String builtWord, Point curLoc, char[][] open, int deep) {

        /*for (int i = 0; i < open.length; i++) {
            for (int j = 0; j < open[i].length; j++) {
                if (i == curLoc.x && j == curLoc.y) {
                    System.out.printf("%3s", "@");;
                }
                else if (open[i][j] == ' ') {
                    System.out.printf("%3s", "+");;
                }
                else {
                    System.out.printf("%3s", open[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println(" _  _  _  _  _");*/

        builtWord = builtWord + board[curLoc.x][curLoc.y];
        open[curLoc.x][curLoc.y] = '.';

        if (WordFinder.isAWord(builtWord)) {
            if(!words.contains(builtWord) && builtWord.length() >= 3) {
                System.out.println("queef");
                words.add(builtWord);
            }
        }
        if (!containedInWord(builtWord)) {
            open[curLoc.x][curLoc.y] = ' ';
            return;
        }

        ArrayList<Point> directions;
        directions = directions(curLoc, open);

        for (Point direction : directions) {
            recurWords(builtWord, direction, open, deep + 1);
        }
        open[curLoc.x][curLoc.y] = ' ';
    }
    private static boolean goable(Point point, char[][] open) {
        if (point.x < 0 || point.x > 3 || point.y < 0 || point.y > 3 || open[point.x][point.y] != ' ') {
            return false;
        }
        return true;

    }

    public static ArrayList<Point> directions(Point curLoc, char[][] open) {
        ArrayList<Point> directions = new ArrayList<>();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue; // skip curLoc itself
                Point p = new Point(curLoc.x + dx, curLoc.y + dy);
                if (goable(p, open)) directions.add(p);
            }
        }
        return directions;
    }

    public static boolean containedInWord(String lets){

        Path filePath = Paths.get("resources", "allWords", lets.charAt(0) + "Words.txt");
        File file = new File(filePath.toString());
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                if (line.startsWith(lets)) return true;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
