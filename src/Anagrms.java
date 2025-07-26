import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Anagrms {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        System.out.print("5 Letters Given: ");
        String letters = sc.nextLine();
        String bag = letters.toLowerCase().replaceAll("[^a-z]", "");
        String line = "";
        ArrayList<String> words = new ArrayList<String>();

        for (int len = 3; len < 7; len++) {
            for (int i = 0; i < bag.length(); i++) {
                Path filePath = Paths.get("resources", "allWords", bag.charAt(i) + "Words.txt");
                File file = new File(filePath.toString());
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    while ((line = br.readLine()) != null) {
                        if (line.length() == len) {
                            if (anagram(bag, line)) {
                                if (!words.contains(line)) {
                                    words.add(line);
                                    System.out.println(line);
                                }
                            }
                        }

                    }
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private static boolean anagram(String pool, String word) {
        if (word == null || pool == null) return false;

        int[] cnt = new int[26];
        for (char c : pool.toLowerCase().toCharArray())
            if (c >= 'a' && c <= 'z') cnt[c - 'a']++;

        for (char c : word.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;
            if (--cnt[c - 'a'] < 0) return false;
        }
        return true;
    }
}

