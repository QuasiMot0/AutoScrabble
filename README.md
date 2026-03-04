# AutoScrabble

A Java desktop application that analyzes a live Scrabble board and recommends the highest-scoring word you can play with your current tiles.

## Features

- **Interactive 15×15 Scrabble Board** — Full GUI with color-coded multiplier squares (double/triple letter and word)
- **Best Move Finder** — Enter your rack of tiles, click Submit, and the app computes the highest-scoring valid placement across every row and column
- **Score Calculation** — Accounts for letter values, board multipliers, cross-word scoring, and the 50-point bonus for playing all 7 tiles
- **Word Validator** — Instantly check whether any word is in the dictionary via binary search on sorted word files
- **Anagram Solver** — Finds all valid 3–6 letter words that can be formed from a given set of letters
- **Save / Load / Undo** — Persist board state to disk and restore it; undo the last move in one click
- **Multithreaded Search** — Word candidates are evaluated in parallel using `CountDownLatch`-coordinated threads, one per unique letter on the board

## How It Works

1. Type the current board state into the grid (use `Shift` to toggle between horizontal and vertical input direction)
2. Enter your tile rack in the letters field
3. Click **Submit**

The engine:
- Scans every row and column for valid placement gaps
- Spins up a thread per unique available letter, each filtering a sorted word list using pattern matching and letter availability checks
- Validates that all cross-words formed by a placement are legal (binary search through dictionary files)
- Scores each candidate word with full multiplier and cross-word scoring
- Places the best word on the board and displays the score

## Getting Started

### Prerequisites
- Java 17+
- IntelliJ IDEA (recommended) or any Java IDE

### Run in IntelliJ
1. Open the project folder in IntelliJ IDEA
2. Ensure `resources/allWords/` is present with the word list files
3. Press `Shift+F10` or click the green Run button — entry point is `Main.main()`

### Run from the terminal
```bash
javac -d out/classes src/*.java
java -cp out/classes Main
```

## Project Structure

```
AutoScrabble/
├── src/
│   ├── Main.java          # Entry point — launches the GUI
│   ├── CharGridGUI.java   # Swing GUI: 15×15 grid, input panel, save/load/undo
│   ├── Board.java         # Board model: state management, row/column readers, scoring helpers
│   ├── WordFinder.java    # Core engine: gap extraction, multiplier map, dictionary lookup, play orchestration
│   ├── FindWords.java     # Runnable worker: pattern matching, letter availability, per-word scoring
│   └── Anagrms.java       # Standalone anagram solver (CLI)
└── resources/
    └── allWords/          # Sorted word lists by starting letter (e.g. AWords.txt)
```

## Keyboard Shortcuts

| Key | Action |
|-----|--------|
| `Shift` | Toggle input direction (horizontal ↔ vertical) |
| `Backspace` | Delete character and move focus back one cell |

## Tech Stack

- **Java** — Core language
- **Java Swing** — GUI framework
- **Java Concurrency** — `Thread`, `CountDownLatch`, `synchronized` for parallel word search
- **Binary Search on disk** — `RandomAccessFile` for O(log n) dictionary lookups without loading the full word list into memory
