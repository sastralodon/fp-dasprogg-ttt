package TicTacToe6;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BoardWithBackground extends Board {
    private Image bgImage;
    private Image crossImage;
    private Image notImage;
    public int offsetX;
    public int offsetY;

    private Image gridBgImage;
    private Image mainBgImage;

    public BoardWithBackground() {
        super();
        loadImages();
    }

    public void updateOffset(int canvasWidth, int canvasHeight) {
        int totalWidth = Cell.SIZE * COLS;
        int totalHeight = Cell.SIZE * ROWS;
        offsetX = (canvasWidth - totalWidth) / 2;
        offsetY = (canvasHeight - totalHeight) / 2;
    }

    private void loadImages() {
        gridBgImage = ThemeManager.loadImage("bg.png");
        mainBgImage = ThemeManager.loadImage("mainBg.jpg");
        crossImage = ThemeManager.loadImage("cross.gif");
        notImage = ThemeManager.loadImage("not.gif");
    }



    public void paintWithOffset(Graphics g, int canvasWidth, int canvasHeight) {
        int totalWidth = Cell.SIZE * COLS;
        int totalHeight = Cell.SIZE * ROWS;
        offsetX = (canvasWidth - totalWidth) / 2;
        offsetY = (canvasHeight - totalHeight) / 2;

        if (mainBgImage != null) {
            g.drawImage(mainBgImage, 0, 0, canvasWidth, canvasHeight, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, canvasWidth, canvasHeight);
        }

        if (gridBgImage != null) {
            g.drawImage(gridBgImage, offsetX, offsetY, totalWidth, totalHeight, null);
        }

        g.setColor(new Color(255, 255, 255, 120));
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(offsetX, offsetY + Cell.SIZE * row - GRID_WIDTH_HALF,
                    totalWidth - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(offsetX + Cell.SIZE * col - GRID_WIDTH_HALF, offsetY,
                    GRID_WIDTH, totalHeight - 1, GRID_WIDTH, GRID_WIDTH);
        }

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                int x = offsetX + col * Cell.SIZE + Cell.PADDING;
                int y = offsetY + row * Cell.SIZE + Cell.PADDING;
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

        // Cek yang menang
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
