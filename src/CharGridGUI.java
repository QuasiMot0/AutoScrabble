import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Paths;

public class CharGridGUI {

    private JFrame frame;
    private JPanel gridPanel;
    private JTextField lettersGiven;
    static private JTextField[][] cellFields;
    private final int GRID_SIZE = 15;
    private final Object lock = new Object();
    private char[][] grid;
    private JButton submitButton;
    private JButton clearButton;
    private JButton directionButton;
    private int adder = 1;
    private JButton Oxyphenbutazone;
    private JButton save;
    private JButton load;
    public JLabel score;

    public CharGridGUI() {
// Initialize the main frame
        frame = new JFrame("Scrabble Word Generator");

// Create the grid panel with GridLayout
        gridPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        cellFields = new JTextField[GRID_SIZE][GRID_SIZE];
        JTextField[] textFields = new JTextField[GRID_SIZE * GRID_SIZE];

// Initialize each cell in the grid as a JTextField
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cellFields[row][col] = new JTextField();
                JTextField txtSpot = cellFields[row][col];
                textFields[15 * row + col] = txtSpot;
                txtSpot.setHorizontalAlignment(SwingConstants.CENTER);
                txtSpot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                final int currentIndex = 15 * row + col;
                switch (WordFinder.getMultiplier(row, col)) {
                    case "doubleWord":
                        txtSpot.setBackground(Color.decode("#FFC5B8"));
                        break;
                    case "tripleWord":
                        txtSpot.setBackground(Color.decode("#FD6E55"));
                        break;
                    case "doubleLetter":
                        txtSpot.setBackground(Color.decode("#C7D9D6"));
                        break;
                    case "tripleLetter":
                        txtSpot.setBackground(Color.decode("#3791A7"));
                        break;
                    default:
                        txtSpot.setBackground(Color.decode("#CBC3A7"));
                }
                txtSpot.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        char typedChar = e.getKeyChar();
                        // Detect if Tab key is pressed
                        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                            switchDirection(); // Your method to handle Tab press
                        } else if (txtSpot.getText().length() <= 1 ) {
                            if (currentIndex < GRID_SIZE * GRID_SIZE - adder) {
                                if (Character.isAlphabetic(typedChar) || typedChar == KeyEvent.VK_BACK_SPACE){
                                    if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && currentIndex > 0) {
                                        resetColor();
                                        textFields[currentIndex - adder].requestFocus();
                                    } else if (e.getKeyCode() != KeyEvent.VK_BACK_SPACE) {
                                        textFields[currentIndex + adder].requestFocus();
                                        textFields[currentIndex].setBackground(Color.decode("#C4A484"));
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
            }
        }

        // Enter panel components
        JPanel enterPanel = new JPanel();
        directionButton = new JButton("\u2192");
        lettersGiven = new JTextField(5);
        submitButton = new JButton("Submit");
        clearButton = new JButton("Clear");
        Oxyphenbutazone = new JButton("Oxyphenbutazone");

        // Submit button functionality
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[][] bored = getGrid();
                Board board = new Board(bored);
                board.printBoard();
                WordFinder.play(bored, lettersGiven.getText());
            }
        });

        // Oxyphenbutazone button functionality
        Oxyphenbutazone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[][] array = {
                        {' ', 'P', 'A', 'C', 'I', 'F', 'Y', 'I', 'N', 'G', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'I', 'S', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'Y', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'R', 'E', 'Q', 'U', 'A', 'L', 'I', 'F', 'I', 'E', 'D', ' ', ' ', ' '},
                        {'H', ' ', 'L', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'E', 'D', 'S', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'N', 'O', ' ', ' ', 'U', 'T', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'R', 'A', 'I', 'N', 'W', 'A', 'S', 'H', 'I', 'N', 'G', ' ', ' ', ' '},
                        {'U', 'M', ' ', ' ', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'T', ' ', ' ', 'E', ' ', 'O', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'W', 'A', 'K', 'E', 'N', 'E', 'R', 'S', ' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'O', 'N', 'E', 'T', 'I', 'M', 'E', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'O', 'O', 'T', ' ', ' ', 'E', ' ', 'B', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {'N', ' ', ' ', ' ', ' ', ' ', 'U', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '},
                        {' ', 'J', 'A', 'C', 'U', 'L', 'A', 'T', 'I', 'N', 'G', ' ', ' ', ' ', ' '}
                };
                lettersGiven.setText("oxbpaze");
                updateGrid(array);
                resetColor();
            }
        });

        // Clear button functionality
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lettersGiven.setText("");
                char[][] clear = new char[15][15];
                for (int row = 0; row < GRID_SIZE; row++) {
                    for (int col = 0; col < GRID_SIZE; col++) {
                        switch (WordFinder.getMultiplier(row, col)) {
                            case "doubleWord":
                                cellFields[row][col].setBackground(Color.decode("#FFC5B8"));
                                break;
                            case "tripleWord":
                                cellFields[row][col].setBackground(Color.decode("#FD6E55"));
                                break;
                            case "doubleLetter":
                                cellFields[row][col].setBackground(Color.decode("#C7D9D6"));
                                break;
                            case "tripleLetter":
                                cellFields[row][col].setBackground(Color.decode("#3791A7"));
                                break;
                            default:
                                cellFields[row][col].setBackground(Color.decode("#CBC3A7"));
                        }
                    }
                }
                updateGrid(clear);
            }
        });

        // Direction button functionality
        directionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchDirection();
            }
        });

// Save and load buttons
        save = new JButton("Save");
        load = new JButton("Load");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[][] grid = getGrid();
                File file = new File(String.valueOf(Paths.get("resources" , "output.txt")));
                try (PrintWriter pw = new PrintWriter(new FileWriter(file), false)) {
                    for (int i = 0; i <= grid.length - 1; i++) {
                        pw.append(printArray(grid[i]) + "\n");
                        pw.flush();
                    }
                } catch (IOException er) {
                    System.out.println("you got the file name wrong moron");
                }
            }
        });
        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[][] grid = new char[GRID_SIZE][GRID_SIZE];
                File file = new File(String.valueOf(Paths.get("resources", "output.txt")));
                try (BufferedReader bfr = new BufferedReader(new FileReader(file))) {
                    for (int i = 0; i <= grid.length - 1; i++) {
                        String line = bfr.readLine();
                        for (int j = 0; j <= line.length() - 1; j++) {
                            grid[i][j] = line.charAt(j);
                        }
                    }
                } catch (IOException er) {
                    System.out.println("you got the file name wrong");
                }
                updateGrid(grid);
                resetColor();
            }
        });

// Bottom panel components
        JPanel bottomPanel = new JPanel();
        score = new JLabel("0");
        bottomPanel.add(save);
        bottomPanel.add(score);
        bottomPanel.add(load);

// Add components to the frame
        enterPanel.add(directionButton);
        enterPanel.add(clearButton);
        enterPanel.add(lettersGiven);
        enterPanel.add(submitButton);
        enterPanel.add(Oxyphenbutazone);
        frame.add(bottomPanel, BorderLayout.SOUTH);
        frame.add(enterPanel, BorderLayout.NORTH);
        frame.add(gridPanel);

// Finalize the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.setVisible(true);

    }

    public void switchDirection(){
        if (directionButton.getText().equals("\u2192")) {
            directionButton.setText("\u2193");
            adder = 15;
        } else {
            directionButton.setText("\u2192");
            adder = 1;
        }
    }

    // Method to update the grid with a 2D char array
    public void updateGrid(char[][] charArray) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // Ensure bounds are respected in case of smaller arrays
                if (row < charArray.length && col < charArray[row].length) {
                    cellFields[row][col].setText(String.valueOf(charArray[row][col]));
                } else {
                    cellFields[row][col].setText(" "); // Leave empty if no char provided
                }
            }
        }
    }

    public char[][] getGrid() {
        char[][] grid = new char[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String c = cellFields[row][col].getText().trim().toUpperCase();
                if (c.isEmpty() || c.equals("\u0000")) {
                    grid[row][col] = ' ';
                } else {
                    grid[row][col] = c.charAt(0);
                }
            }
        }

        return grid;
    }

    private String chartoString() {
        String build = "";
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                build += Character.toString(grid[row][col]);
            }
            build += "\n";
        }
        JOptionPane.showMessageDialog(null, build);
        return build;
    }

    public void lightCell(Point point) {
        cellFields[point.x][point.y].setBackground(new Color(210, 180, 140));
    }

    public void lightCell(int x, int y, Color color) {
        cellFields[x][y].setBackground(color);
    }

    public void lightCell(int x, int y) {
        cellFields[x][y].setBackground(new Color(210, 180, 140));
    }

    public void lightText(int x, int y) {
        if (x >= 15 || x < 0) {
            throw new IndexOutOfBoundsException("X out of bounds: " + x);
        }
        if (y >= 15 || y < 0) {
            throw new IndexOutOfBoundsException("Y out of bounds: " + y);
        }
        cellFields[x][y].setForeground(Color.decode("#FFFFE0"));
    }

    public void lightCell(Point point, Color color) {
        cellFields[point.x][point.y].setBackground(color);
    }

    public void resetColor() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cellFields[row][col].setForeground(Color.BLACK);
                if (cellFields[row][col].getText().isEmpty()
                        || cellFields[row][col].getText().charAt(0) == '\u0000'
                        || cellFields[row][col].getText().charAt(0) == ' ') {
                    switch (WordFinder.getMultiplier(row, col)) {
                        case "doubleWord":
                            cellFields[row][col].setBackground(Color.decode("#FFC5B8"));
                            break;
                        case "tripleWord":
                            cellFields[row][col].setBackground(Color.decode("#FD6E55"));
                            break;
                        case "doubleLetter":
                            cellFields[row][col].setBackground(Color.decode("#C7D9D6"));
                            break;
                        case "tripleLetter":
                            cellFields[row][col].setBackground(Color.decode("#3791A7"));
                            break;
                        default:
                            cellFields[row][col].setBackground(Color.decode("#CBC3A7"));
                    }
                } else {
                    cellFields[row][col].setBackground(Color.decode("#C4A484"));
                }
            }
        }
    }

    private void updateGrid(char[][] charArray, int selectedRow, int selectedCol) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                // Ensure bounds are respected in case of smaller arrays
                if (row < charArray.length && col < charArray[row].length) {
                    cellFields[row][col].setText(String.valueOf(charArray[row][col]));
                } else {
                    cellFields[row][col].setText(""); // Leave empty if no char provided
                }
                if (selectedRow == row && selectedCol == col) {
                    cellFields[row - 1][col - 1].setBackground(Color.CYAN);
                }
            }
        }
    }

    private String printArray(char[] array){
        String s = "";
        for(char c : array ){
            s += c;
        }
        return s;
    }
}
