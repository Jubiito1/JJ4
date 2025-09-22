package com.mijuego.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

import com.mijuego.entities.Entities;
import com.mijuego.entities.Player;
import com.mijuego.ui.HUD;
import com.mijuego.utils.ResourceManager;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private BufferedImage background;
    private BufferedImage canvas;
    private Graphics2D g2d;
    private LevelManager levelManager;
    private Camera camera;

    private List<Entities> entities;
    private HUD hud;

    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        background = ResourceManager.loadImage("/assets/sprites/lanzarocas.png");

        // Inicializar nivel/entidades
        levelManager = new LevelManager();
        entities = new ArrayList<>();

        // Crear cámara usando el TileMap cargado
        camera = new Camera(GS.getVirtualWidth(), GS.getVirtualHeight(), levelManager.getCurrentTileMap());

        // Agregar enemigos
        entities.addAll(levelManager.getEnemies());

        // Agregar items
        entities.addAll(levelManager.getItems());

        // Agregar player
        Player p = levelManager.getPlayer();
        if (p != null) entities.add(p);
        // Crear HUD
        hud = new HUD(levelManager);

        canvas = new BufferedImage(
            GS.getVirtualWidth(),
            GS.getVirtualHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        g2d = canvas.createGraphics();

        setFocusable(true);
        requestFocus();
        addKeyListener(new com.mijuego.utils.InputManager());
    }

    public void addEntity(Entities e) {
        entities.add(e);
    }

    public List<Entities> getEntities() {
        return entities;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public Camera getCamera() {
        return camera;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- Dibujar fondo (parallax) ---
        if (background != null) {
            double parallaxFactor = 0.5; // cuánto se mueve en relación a la cámara
            int bgX = (int)(-camera.getX() * parallaxFactor);
            int bgY = (int)(-camera.getY() * parallaxFactor);

            // Dibujar el fondo cubriendo toda la pantalla
            for (int x = bgX; x < GS.getVirtualWidth(); x += background.getWidth()) {
                for (int y = bgY; y < GS.getVirtualHeight(); y += background.getHeight()) {
                    g2d.drawImage(background, x, y, null);
                }
            }
        } else {
            // fallback: cielo celeste
            Color cielo = new Color(135, 206, 235);
            g2d.setColor(cielo);
            g2d.fillRect(0, 0, GS.getVirtualWidth(), GS.getVirtualHeight());
        }

        // --- Dibujar mapa ---
        if (levelManager.getCurrentTileMap() != null) {
            levelManager.getCurrentTileMap().draw(g2d, camera);
        }

        // --- Dibujar entidades ---
        for (Entities e : entities) {
            e.draw(g2d, camera);
        }

        // --- Dibujar HUD ---
        if (hud != null) {
            hud.draw(g2d);
        }

        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);
    }
}