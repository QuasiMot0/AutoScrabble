import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class WordFinderGUI {
    private JFrame frame;
    private JPanel gridPanel;
    public char[][] board;
    private JButton submitButton;

    static private JTextField[][] cellFields;
    private final int GRID_SIZE = 4;
    int adder = 1;

// Create the grid panel with GridLayout
    public WordFinderGUI() {
        frame = new JFrame("Word Hunt");

// Create the grid panel with GridLayout
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        cellFields = new JTextField[GRID_SIZE][GRID_SIZE];
        JTextField[] textFields = new JTextField[GRID_SIZE * GRID_SIZE];

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cellFields[row][col] = new JTextField();
                JTextField txtSpot = cellFields[row][col];
                textFields[4 * row + col] = txtSpot;
                txtSpot.setHorizontalAlignment(SwingConstants.CENTER);
                txtSpot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                final int currentIndex = 4 * row + col;
                txtSpot.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        char typedChar = e.getKeyChar();
                        // Detect if Tab key is pressed
                        if (txtSpot.getText().length() <= 1 ) {
                            if (currentIndex < GRID_SIZE * GRID_SIZE - adder) {
                                if (Character.isAlphabetic(typedChar) || typedChar == KeyEvent.VK_BACK_SPACE){
                                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && currentIndex > 0) {
                                        textFields[currentIndex - adder].requestFocus();
                                    } else if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
                                        textFields[currentIndex + adder].requestFocus();
                                    }
                                } else {
                                    if(!textFields[currentIndex].getText().isEmpty()){
                                        textFields[currentIndex].setText(textFields[currentIndex].getText().substring(0, textFields[currentIndex].getText().length() - 1));
                                    }

                                }

                            }
                        }
                    }
                });
                gridPanel.add(txtSpot);
                frame.add(gridPanel);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(600, 600);
                frame.setVisible(true);

                JPanel enterPanel = new JPanel();
                submitButton = new JButton("Submit");
                submitButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //System.out.println("1");
                        char[][] bored = getBoard();
                        WordHunt.getWords(bored);
                    }
                });
                enterPanel.add(submitButton);
                frame.add(enterPanel, BorderLayout.NORTH);
            }
        }
    }
    public char[][] getBoard() {
        board = new char[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                board[row][col] = cellFields[row][col].getText().charAt(0);
            }
        }
        return board;
    }
    public static void setColor(int row, int col, Color color) {
        cellFields[row][col].setOpaque(true);
        cellFields[row][col].setBackground(Color.YELLOW);
        cellFields[row][col].setForeground(Color.BLACK); // for text visibility
        cellFields[row][col].revalidate();
        cellFields[row][col].repaint();

    }
    public static void resetColor() {
        for (int i = 0; i < 4; i++) {

            for (int j = 0; j < 4; j++) {
                cellFields[i][j].setBackground(Color.WHITE);
            }
        }
    }
    public static String getText(int row, int col) {
        return cellFields[row][col].getText();
    }

}
