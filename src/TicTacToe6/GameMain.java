package TicTacToe6;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.Clip;


public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(255, 255, 255);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private int crossWins = 0;
    private int noughtWins = 0;


    /**
     * Constructor
     */
    public GameMain() {
        super.addMouseListener(new MouseAdapter() {
                                   @Override
                                   public void mouseClicked(MouseEvent e) {
                                       int mouseX = e.getX();
                                       int mouseY = e.getY();
                                       if (board instanceof BoardWithBackground bgBoard) {
                                           int canvasWidth = getWidth();
                                           int canvasHeight = getHeight() - statusBar.getHeight();
                                           bgBoard.updateOffset(canvasWidth, canvasHeight);
                                       }

                                       int row = (mouseY - ((BoardWithBackground) board).offsetY) / Cell.SIZE;
                                       int col = (mouseX - ((BoardWithBackground) board).offsetX) / Cell.SIZE;

                                       if (currentState == State.PLAYING) {
                                           if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                                                   && board.cells[row][col].content == Seed.NO_SEED) {
                                               currentState = board.stepGame(currentPlayer, row, col);
                                               if (currentState == State.CROSS_WON) {
                                                   crossWins++;
                                                   showWinnerPopup("win_cross.gif");
                                               } else if (currentState == State.NOUGHT_WON) {
                                                   noughtWins++;
                                                   showWinnerPopup("win_nought.gif");
                                               } else if (currentState == State.DRAW) {
                                                   SoundEffect.EAT_FOOD.play();
                                               } else {
                                                   SoundEffect.DIE.play();
                                               }
                                               currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                                           }
                                       } else {
                                           newGame();
                                       }

                                       repaint();
                                   }
                               });


        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        super.setLayout(new BorderLayout());
        super.add(statusBar, BorderLayout.PAGE_END);
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 30));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        initGame();
        newGame();
    }

    public void initGame() {
        String[] options = {"Nailong", "Love and Deepspace", "Genshin Impact"};
        int choice = JOptionPane.showOptionDialog(null, "Pilih Tema:", "Tema",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 1) ThemeManager.selectedTheme = "theme2";
        else if (choice == 2) ThemeManager.selectedTheme = "theme3";
        else ThemeManager.selectedTheme = "theme1";
        board = new BoardWithBackground();
        Clip bgmClip = ThemeManager.loadBGM("bgm.wav");
        if (bgmClip != null) {
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
        }

    }

    public void newGame() {
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(COLOR_BG);
        if (board instanceof BoardWithBackground bgBoard) {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight() - statusBar.getHeight();
            bgBoard.paintWithOffset(g, canvasWidth, canvasHeight);
        } else {
            board.paint(g); // fallback kalau bukan BoardWithBackground
        }

            if (currentState == State.PLAYING) {
                statusBar.setForeground(Color.BLACK);
                statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("It's a Draw! Click to play again.");
            } else if (currentState == State.CROSS_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'X' Won! Click to play again.  X Wins: " + crossWins + " | O Wins: " + noughtWins);
            } else if (currentState == State.NOUGHT_WON) {
                statusBar.setForeground(Color.RED);
                statusBar.setText("'O' Won! Click to play again.  X Wins: " + crossWins + " | O Wins: " + noughtWins);
            }

    }

    private void showWinnerPopup(String gifFileName) {
        try {
            String path = "/themes/" + ThemeManager.selectedTheme + "/" + gifFileName;
            java.net.URL gifUrl = ThemeManager.class.getResource(path);
            if (gifUrl == null) {
                System.err.println("❌ GIF tidak ditemukan: " + path);
                return;
            }
            ImageIcon gif = new ImageIcon(gifUrl);
            JLabel label = new JLabel(gif);
            JOptionPane.showMessageDialog(this, label, "Winner!", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** Entry point */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDlg = new LoginDialog(null);
            loginDlg.setVisible(true);

            if (loginDlg.isSucceeded()) {
                JFrame frame = new JFrame(TITLE);
                frame.setContentPane(new GameMain());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Login dibatalkan. Keluar dari aplikasi.");
                System.exit(0);
            }
        });
    }
}
