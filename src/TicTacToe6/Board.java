package TicTacToe6;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

/**
 * The Board class models the ROWS-by-COLS game board.
 */
public class Board {
    // Define named constants
    public static final int ROWS = 3;  // ROWS x COLS cells
    public static final int COLS = 3;
    // Define named constants for drawing
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;  // the drawing canvas
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;  // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;  // grid lines
    public static final int Y_OFFSET = 1;  // Fine tune for better display

    // Define properties (package-visible)
    /** Composes of 2D array of ROWS-by-COLS Cell instances */
    Cell[][] cells;

    /** Constructor to initialize the game board */
    public Board() {
        initGame();
    }

    /** Initialize the game objects (run once) */
    public void initGame() {
        cells = new Cell[ROWS][COLS]; // allocate the array
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                // Allocate element of the array
                cells[row][col] = new Cell(row, col);
                // Cells are initialized in the constructor
            }
        }
    }

    /** Reset the game board, ready for new game */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame(); // clear the cell content
            }
        }
    }

    /**
     *  The given player makes a move on (selectedRow, selectedCol).
     *  Update cells[selectedRow][selectedCol]. Compute and return the
     *  new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Update game board
        cells[selectedRow][selectedCol].content = player;

        // Compute and return the new game state
        if (cells[selectedRow][0].content == player  // 3-in-the-row
                && cells[selectedRow][1].content == player
                && cells[selectedRow][2].content == player
                || cells[0][selectedCol].content == player // 3-in-the-column
                && cells[1][selectedCol].content == player
                && cells[2][selectedCol].content == player
                || selectedRow == selectedCol     // 3-in-the-diagonal
                && cells[0][0].content == player
                && cells[1][1].content == player
                && cells[2][2].content == player
                || selectedRow + selectedCol == 2 // 3-in-the-opposite-diagonal
                && cells[0][2].content == player
                && cells[1][1].content == player
                && cells[2][0].content == player) {
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            // Nobody win. Check for DRAW (all cells occupied) or PLAYING.
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (cells[row][col].content == Seed.NO_SEED) {
                        return State.PLAYING; // still have empty cells
                    }
                }
            }
            return State.DRAW; // no empty cell, it's a draw
        }
    }

    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Draw the grid-lines
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH_HALF,
                    CANVAS_WIDTH - 1, GRID_WIDTH,
                    GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH_HALF, 0 + Y_OFFSET,
                    GRID_WIDTH, CANVAS_HEIGHT - 1,
                    GRID_WIDTH, GRID_WIDTH);
        }

        // Draw all the cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);  // ask the cell to paint itself
            }
        }
    }

    public static class BoardWithBackground extends JPanel {
        private Image bgImage;
        private Image crossImage;
        private Image notImage;

        private int[][] board = new int[3][3]; // 0 = empty, 1 = X, 2 = O
        private boolean isCrossTurn = true;

        public BoardWithBackground() {
            setPreferredSize(new Dimension(400, 400));
            loadImages();

            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int cellWidth = getWidth() / 3;
                    int cellHeight = getHeight() / 3;

                    int row = e.getY() / cellHeight;
                    int col = e.getX() / cellWidth;

                    if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == 0) {
                        board[row][col] = isCrossTurn ? 1 : 2;
                        playClickSound();  // 🔊 Mainkan suara di sini
                        isCrossTurn = !isCrossTurn;
                        repaint();
                    }
                }
            });
        }

        private void loadImages() {
            try {
                bgImage = ImageIO.read(getClass().getResource("/images2/bg.jpg"));
                crossImage = new ImageIcon(getClass().getResource("/images/cross.gif")).getImage();
                notImage = new ImageIcon(getClass().getResource("/images/not.gif")).getImage();
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("Gagal memuat gambar: " + e.getMessage());
            }
        }

        private void playClickSound() {
            try {
                URL url = getClass().getClassLoader().getResource("audio/eatfood.wav");
                if (url == null) {
                    System.out.println("Sound not found: audio/eatfood.wav");
                    return;
                }
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Gambar background
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            // Gambar grid
            g.setColor(Color.GRAY);
            int w = getWidth(), h = getHeight();
            g.drawLine(w / 3, 0, w / 3, h);
            g.drawLine(2 * w / 3, 0, 2 * w / 3, h);
            g.drawLine(0, h / 3, w, h / 3);
            g.drawLine(0, 2 * h / 3, w, 2 * h / 3);

            // Gambar X dan O
            int cellWidth = w / 3;
            int cellHeight = h / 3;

            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 3; col++) {
                    int x = col * cellWidth;
                    int y = row * cellHeight;
                    if (board[row][col] == 1) {
                        g.drawImage(crossImage, x + 20, y + 20, cellWidth - 40, cellHeight - 40, this);
                    } else if (board[row][col] == 2) {
                        g.drawImage(notImage, x + 20, y + 20, cellWidth - 40, cellHeight - 40, this);
                    }
                }
            }
        }
    }
}