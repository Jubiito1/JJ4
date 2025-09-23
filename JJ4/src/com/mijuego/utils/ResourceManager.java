package com.mijuego.utils;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class ResourceManager {

    // 🔹 Cargar imagen desde resources
    public static BufferedImage loadImage(String path) {
        try (InputStream is = ResourceManager.class.getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("No se encontró la imagen: " + path);
            return ImageIO.read(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 🔹 Cargar sonido desde resources
    public static Clip loadSound(String path) {
        try (InputStream is = ResourceManager.class.getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("No se encontró el sonido: " + path);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(is));
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 🔹 Cargar archivo de texto / nivel
    public static InputStream loadText(String path) {
        InputStream is = ResourceManager.class.getResourceAsStream(path);
        if (is == null) throw new RuntimeException("No se encontró el archivo: " + path);
        return is;
    }

    // 🔹 Cargar fuente desde resources
    public static Font loadFont(String path, float size) {
        try (InputStream is = ResourceManager.class.getResourceAsStream(path)) {
            if (is == null) throw new RuntimeException("No se encontró la fuente: " + path);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
