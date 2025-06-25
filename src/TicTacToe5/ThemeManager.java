package TicTacToe5;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

public class ThemeManager {
    public static String selectedTheme = "theme1";

    public static Image loadImage(String filename) {
        String path = "/themes/" + selectedTheme + "/" + filename;
        try (InputStream is = ThemeManager.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("❌ File tidak ditemukan: " + path);
                System.out.println(">> Load: " + path);
                return null;
            }
            System.out.println("✅ File ditemukan: " + path);
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("❌ Gagal memuat gambar: " + path);
            return null;
        }
    }


}
