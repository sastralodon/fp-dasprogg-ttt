package TicTacToe5;

import TicTacToe5.BoardWithBackground;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Define game objects
    private BoardWithBackground board;   // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message

    /** Constructor to setup the UI and game components */
    public GameMain() {

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                int row = (mouseY - board.offsetY) / Cell.SIZE;
                int col = (mouseX - board.offsetX) / Cell.SIZE;



                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        // Update cells[][] and return the new game state after the move
                        currentState = board.stepGame(currentPlayer, row, col);
                        // Switch player
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                    }
                    if (currentState == State.PLAYING) {
                        SoundEffect.DIE.play();
                    } else if(currentState == State.DRAW){
                        SoundEffect.EAT_FOOD.play();
                    } else{
                        SoundEffect.EXPLODE.play();
                    }
                } else {        // game over
                    newGame();  // restart the game
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Setup the status bar (JLabel) to display status message
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END); // same as SOUTH
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        // account for statusBar in height
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Set up Game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        String[] options = {"Tema 1", "Tema 2", "Tema 3"};
        int choice = JOptionPane.showOptionDialog(null, "Pilih Tema:", "Tema",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) ThemeManager.selectedTheme = "theme2";
        else if (choice == 2) ThemeManager.selectedTheme = "theme3";
        else ThemeManager.selectedTheme = "theme1";

        board = new BoardWithBackground();  // buat board setelah tema dipilih
    }


    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int canvasWidth = getWidth();
        int canvasHeight = getHeight() - statusBar.getHeight(); // biar status nggak ganggu tengah

        board.paintWithOffset(g, canvasWidth, canvasHeight);  // ini yang bikin grid di tengah

        // Status bar text
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
        }
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                GameMain gamePanel = new GameMain(); // 🔽 panel utama game

                frame.setContentPane(gamePanel); // 🔁 masukkan panel ke frame
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


                frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // maksimal layar
                frame.setUndecorated(true); // hilangkan bar jendela (opsional)

                frame.setVisible(true);


                frame.getRootPane().registerKeyboardAction(e -> System.exit(0),
                        KeyStroke.getKeyStroke("ESCAPE"),
                        JComponent.WHEN_IN_FOCUSED_WINDOW);
            }
        });
    }
}