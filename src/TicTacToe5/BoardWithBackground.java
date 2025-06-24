package TicTacToe5;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoardWithBackground extends Board {
    private Image bgImage;
    private Image crossImage;
    private Image notImage;

    public BoardWithBackground() {
        super();
        loadImages();
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

    @Override
    public void paint(Graphics g) {
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, CANVAS_WIDTH, CANVAS_HEIGHT, null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
        }

        // Gambar garis grid
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

        // Gambar isi sel (pakai gambar)
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                int x = col * Cell.SIZE + Cell.PADDING;
                int y = row * Cell.SIZE + Cell.PADDING;
                int size = Cell.SIZE - 2 * Cell.PADDING;

                if (cells[row][col].content == Seed.CROSS) {
                    g.drawImage(crossImage, x, y, size, size, null);
                } else if (cells[row][col].content == Seed.NOUGHT) {
                    g.drawImage(notImage, x, y, size, size, null);
                }
            }
        }
    }

    @Override
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        cells[selectedRow][selectedCol].content = player;

        // Cek kemenangan
        State result;
        if (cells[selectedRow][0].content == player
                && cells[selectedRow][1].content == player
                && cells[selectedRow][2].content == player
                || cells[0][selectedCol].content == player
                && cells[1][selectedCol].content == player
                && cells[2][selectedCol].content == player
                || selectedRow == selectedCol
                && cells[0][0].content == player
                && cells[1][1].content == player
                && cells[2][2].content == player
                || selectedRow + selectedCol == 2
                && cells[0][2].content == player
                && cells[1][1].content == player
                && cells[2][0].content == player) {
            result = (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (cells[row][col].content == Seed.NO_SEED) {
                        result = State.PLAYING;
                        playMoveSound(result, player);
                        return result;
                    }
                }
            }
            result = State.DRAW;
        }

        playMoveSound(result, player);
        return result;
    }

    private void playMoveSound(State result, Seed player) {
        if (result == State.PLAYING) {
            if (player == Seed.CROSS) {
                SoundEffect.DIE.play(); // giliran X
            } else if (player == Seed.NOUGHT) {
                SoundEffect.EXPLODE.play(); // giliran O
            }
        } else if (result == State.DRAW || result == State.CROSS_WON || result == State.NOUGHT_WON) {
            SoundEffect.EAT_FOOD.play(); // draw atau menang
        }
    }

}
