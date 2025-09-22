package com.mijuego.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import com.mijuego.entities.Player;
import com.mijuego.core.LevelManager;
import com.mijuego.core.GS;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;

public class HUD {
    private LevelManager levelManager;
    private Image healthIcon;
    private Font hudFont;

    public HUD(LevelManager levelManager) {
        this.levelManager = levelManager;
        try {
            healthIcon = ImageIO.read(getClass().getResource("/assets/sprites/1Vida.png"));
            InputStream is = getClass().getResourceAsStream("/assets/fonts/Pixeled.ttf");
            hudFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.BOLD, GS.SC(10));
        } catch (IOException | FontFormatException e) {
            hudFont = new Font("Arial", Font.BOLD, GS.SC(10));
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(hudFont);

        // ahora el HUD siempre pide el player actual al LevelManager
        Player player = levelManager.getPlayer();  

        if (player == null) return; // por seguridad

        int ancho = GS.SC(32);
        int alto = GS.SC(32);
        int x = GS.SC(10);
        int y = GS.SC(4);
        int health = player.getHealth();
        
        if (health > 0 && healthIcon != null) {
            for (int i = 0 ; i < health; i++) {
                g.drawImage(healthIcon, x + i * (ancho + 8), y, ancho, alto, null);
            }
        } else {
            g.drawString("MORISTE :(", x, GS.SC(20));
        }
        
        g.drawString("PUNTOS: " + player.getCoins() * 100 , GS.SC(10), GS.SC(50));
        g.drawString("NIVEL: " + levelManager.getCurrentLevel(), GS.SC(10), GS.SC(70));
    }
}