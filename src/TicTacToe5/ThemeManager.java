package TicTacToe5;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


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
    public static Clip loadBGM(String filename) {
        String path = "/themes/" + selectedTheme + "/" + filename;
        try (InputStream is = ThemeManager.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("❌ BGM tidak ditemukan: " + path);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (Exception e) {
            System.err.println("❌ Gagal memuat BGM: " + e.getMessage());
            return null;
        }
    }



}
