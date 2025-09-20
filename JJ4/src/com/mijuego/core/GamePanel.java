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

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private BufferedImage canvas;
    private Graphics2D g2d;
    private LevelManager levelManager;
    private Camera camera;

    private List<Entities> entities;

    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

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
        
        Color cielo = new Color(135, 206, 235);
        g2d.setColor(cielo);
        g2d.fillRect(0, 0, GS.getVirtualWidth(), GS.getVirtualHeight());

        if (levelManager.getCurrentTileMap() != null) {
            levelManager.getCurrentTileMap().draw(g2d, camera);
        }

        // Dibujar todas las entidades, incluyendo items
        for (Entities e : entities) {
            e.draw(g2d, camera);
        }

        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);
    }
}
