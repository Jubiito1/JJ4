package com.mijuego.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mijuego.utils.InputManager;

public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private GameLoop gameLoop;


    // Canvas virtual donde dibujamos
    private BufferedImage canvas;
    private Graphics2D g2d;

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
    }

    public void setGameLoop(GameLoop loop) {
        this.gameLoop = loop;
    }

    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Limpiar pantalla
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, GS.getVirtualWidth(), GS.getVirtualHeight());

        // Dibujar cuadrado rojo
        g2d.setColor(Color.RED);
        g2d.fillRect(GS.SC(100), GS.SC(100), GS.SC(200), GS.SC(200));

        // 🔹 Dibujar el estado actual
        if (gameLoop != null) {
            g2d.setColor(Color.WHITE);
            g2d.drawString("Estado: " + gameLoop.getGameState(), 50, 50);
        }

        // Dibujar canvas escalado
        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);
    }

}
