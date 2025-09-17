package com.mijuego.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mijuego.utils.InputManager;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Canvas virtual donde dibujamos
    private BufferedImage canvas;
    private Graphics2D g2d;
    private LevelManager levelManager;


    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        // Creamos el buffer interno con la resolución virtual
        canvas = new BufferedImage(
            GS.getVirtualWidth(),
            GS.getVirtualHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        g2d = canvas.createGraphics();
        
     // 🔹 Configurar teclado
        setFocusable(true);          // hace que el panel pueda recibir eventos de teclado
        requestFocus();              // solicita que tenga el foco al iniciar
        addKeyListener(new InputManager()); // agrega el KeyListener
        
        levelManager = new LevelManager();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 🔴 Dibujamos en el canvas virtual (resolución interna)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, GS.getVirtualWidth(), GS.getVirtualHeight());
        
        if (levelManager.getCurrentTileMap() != null) {
            levelManager.getCurrentTileMap().draw(g2d);
        }

        // 🔴 Dibujamos el canvas escalado a toda la pantalla real
        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);
    }
}
