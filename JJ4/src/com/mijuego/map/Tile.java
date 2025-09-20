package com.mijuego.map;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Color;
import com.mijuego.core.GS;

public class Tile {
    // Tamaño base y tamaño escalado
    public static final int BASE_SIZE = 20;
    public static final int SIZE = GS.SC(BASE_SIZE);

    private int id;                   // ID del tile (ej: 0=aire, 1=suelo)
    private boolean solid;            // Si colisiona o no

    public Tile(int id, BufferedImage sprite, boolean solid) {
        this.id = id;
        this.solid = solid;
    }

    // Dibujar el tile en coordenadas (x, y) en el canvas
    public void draw(Graphics2D g, int x, int y) {
            g.setColor(Color.BLACK);
            g.fillRect(x, y, SIZE, SIZE);
    }

    // Devuelve si es sólido
    public boolean isSolid() {
        return solid;
    }

    // Rectángulo para colisiones
    public Rectangle getBounds(int x, int y) {
        return new Rectangle(x, y, SIZE, SIZE);
    }

    // Getters
    public int getId() {
        return id;
    }
}
