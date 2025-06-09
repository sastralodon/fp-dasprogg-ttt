import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphics version with full OO Design (incorporating Cell, Board, State, Seed classes)
 */
public class TTTGraphics extends JFrame {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // --- Nested Enums ---
    /**
     * The enum State defines the various game states of the TTT game
     */
    public enum State {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }

    /**
     * This enum is used by:
     * 1. Player: takes value of CROSS or NOUGHT
     * 2. Cell content: takes value of CROSS, NOUGHT, or NO_SEED.
     */
    public enum Seed {
        CROSS, NOUGHT, NO_SEED; // NO_SEED means empty cell

        // We could add a display icon (text or image) here, but for now, we'll draw based on the enum directly in paint()
    }

    // --- Nested Classes for Game Logic ---

    /**
     * The Cell class models each individual cell of the TTT 3x3 grid.
     */
    public class Cell {
        // Define properties (package-visible for simplicity in inner class)
        Seed content; // Content of this cell (CROSS, NOUGHT, NO_SEED)
        int row, col; // Row and column of this cell

        /** Constructor to initialize this cell */
        public Cell(int row, int col) {
            this.row = row;
            this.col = col;
            clear(); // Initialize to empty
        }

        /** Reset the cell content to EMPTY, ready for a new game. */
        public void clear() {
            content = Seed.NO_SEED;
        }

        /** The cell paints itself */
        public void paint(Graphics2D g2d) { // Now takes Graphics2D object
            int x1 = col * CELL_SIZE + CELL_PADDING;
            int y1 = row * CELL_SIZE + CELL_PADDING;

            if (content == Seed.CROSS) { // draw a 2-line cross
                g2d.setColor(COLOR_CROSS);
                int x2 = (col + 1) * CELL_SIZE - CELL_PADDING;
                int y2 = (row + 1) * CELL_SIZE - CELL_PADDING;
                g2d.drawLine(x1, y1, x2, y2);
                g2d.drawLine(x2, y1, x1, y2);
            } else if (content == Seed.NOUGHT) { // draw a circle
                g2d.setColor(COLOR_NOUGHT);
                g2d.drawOval(x1, y1, SYMBOL_SIZE, SYMBOL_SIZE);
            }
            // If NO_SEED, nothing is drawn in the cell
        }
    }

    /**
     * The Board class models the TTT game-board of 3x3 cells.
     */
    public class Board {
        // Define named constants for the grid (can be accessed via TTTGraphics.ROWS etc.)
        // These are effectively constants for the outer class, accessible here.

        /** A board composes of [ROWS]x[COLS] Cell instances */
        Cell[][] cells; // Use Cell objects instead of Seed enum directly

        /** Constructor to initialize the game board */
        public Board() {
            initBoard(); // Initialize the board array and cells
        }

        /** Initialize the board (run once for object creation) */
        public void initBoard() {
            cells = new Cell[ROWS][COLS]; // allocate the array
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    // Allocate element of the array
                    cells[row][col] = new Cell(row, col);
                }
            }
        }

        /** Reset the contents of the game board, ready for new game. */
        public void newGame() {
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    cells[row][col].clear(); // The cells init itself
                }
            }
        }

        /**
         * The given player makes a move on (selectedRow, selectedCol).
         * Update cells[selectedRow][selectedCol]. Compute and return the
         * new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
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
                    || selectedRow == selectedCol         // 3-in-the-diagonal
                    && cells[0][0].content == player
                    && cells[1][1].content == player
                    && cells[2][2].content == player
                    || selectedRow + selectedCol == 2     // 3-in-the-opposite-diagonal
                    && cells[0][2].content == player
                    && cells[1][1].content == player
                    && cells[2][0].content == player) {
                return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
            } else {
                // Nobody wins. Check for DRAW (all cells occupied) or PLAYING.
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

        /** The board paints itself */
        public void paint(Graphics g) { // Now takes Graphics object
            Graphics2D g2d = (Graphics2D)g;
            g2d.setStroke(new BasicStroke(SYMBOL_STROKE_WIDTH,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            // Draw the Seeds of all the cells if they are not empty
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    cells[row][col].paint(g2d); // Each cell paints itself
                }
            }
        }
    }

    // --- Main TTTGraphics Class Members (Controller) ---

    // Define named constants for the game board (these remain in TTTGraphics as global constants)
    public static final int ROWS = 3;  // ROWS x COLS cells
    public static final int COLS = 3;

    // Define named constants for the drawing graphics (these remain in TTTGraphics)
    public static final int CELL_SIZE = 120; // cell width/height (square)
    public static final int BOARD_WIDTH  = CELL_SIZE * COLS; // the drawing canvas
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 10;                  // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final int CELL_PADDING = CELL_SIZE / 5;
    public static final int SYMBOL_SIZE = CELL_SIZE - CELL_PADDING * 2; // width/height
    public static final int SYMBOL_STROKE_WIDTH = 8; // pen's stroke width
    public static final Color COLOR_BG = Color.WHITE;  // background
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_GRID   = Color.LIGHT_GRAY;  // grid lines
    public static final Color COLOR_CROSS  = new Color(211, 45, 65);  // Red #D32D41
    public static final Color COLOR_NOUGHT = new Color(76, 181, 245); // Blue #4CB5F5
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // The game board (now an instance of our new Board class)
    private Board board;
    private State currentState;  // the current game state
    private Seed currentPlayer; // the current player

    // UI Components
    private GamePanel gamePanel; // Drawing canvas (JPanel) for the game board
    private JLabel statusBar;  // Status Bar

    /** Constructor to setup the game and the GUI components */
    public TTTGraphics() {
        // Initialize the game objects (Board is now a class)
        board = new Board(); // Create an instance of our new Board class

        // Set up GUI components
        gamePanel = new GamePanel();  // Construct a drawing canvas (a JPanel)
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // The canvas (JPanel) fires a MouseEvent upon mouse-click
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / CELL_SIZE;
                int col = mouseX / CELL_SIZE;

                if (currentState == State.PLAYING) {
                    // Check if the clicked cell is within bounds and empty
                    if (row >= 0 && row < ROWS && col >= 0
                            && col < COLS && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update board[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);
                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                } else {       // game over
                    newGame(); // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel("       ");
        statusBar.setFont(FONT_STATUS);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(COLOR_BG_STATUS);

        // Set up content pane
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();  // pack all the components in this JFrame
        setTitle("Tic Tac Toe");
        setVisible(true);  // show this JFrame

        newGame(); // Start a new game after GUI is set up
    }

    /** Reset the game-board contents and the status, ready for new game */
    public void newGame() {
        board.newGame(); // Let the Board class handle clearing its cells
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState  = State.PLAYING; // ready to play
    }

    /**
     * Inner class GamePanel (extends JPanel) used for custom graphics drawing.
     */
    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L; // to prevent serializable warning

        @Override
        public void paintComponent(Graphics g) {  // Callback via repaint()
            super.paintComponent(g);
            setBackground(COLOR_BG);  // set its background color

            // Draw the grid lines
            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH-1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT-1, GRID_WIDTH, GRID_WIDTH);
            }

            // Draw the game board content
            board.paint(g); // Delegate drawing the cells to the Board object

            // Print status message
            if (currentState == State.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click to play again");
            } else if (currentState == State.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'X' Won! Click to play again");
            } else if (currentState == State.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'O' Won! Click to play again");
            }
        }
    }

    /** The entry main() method */
    public static void main(String[] args) {
        // Run GUI codes in the Event-Dispatching thread for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TTTGraphics(); // Let the constructor do the job
            }
        });
    }
}