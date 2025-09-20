package com.mijuego.map;

import java.awt.Color;
import java.awt.Graphics2D;
import com.mijuego.core.Camera;

public class Tile {
    public static final int SIZE = 20;

    public static final int EMPTY = 0;
    public static final int SOLID = 1;
    public static final int GOOMBA = 2;
    public static final int PLAYER = 3;
    public static final int KILL = 4;  // 🔹 nuevo tile rojo mortal
    public static final int WIN = 5; // nuevo tile amarillo

    private int type;

    public Tile(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public boolean isSolid() {
        return type == SOLID;
    }

    public boolean isKill() {
        return type == KILL;
    }
    
    public boolean isWin() {
        return type == WIN;
    }

    public void draw(Graphics2D g, int x, int y, Camera camera) {
        if (type == EMPTY) return;

        switch (type) {
            case SOLID: g.setColor(Color.GRAY); break;
            case GOOMBA: g.setColor(new Color(139,69,19)); break; // marrón
            case PLAYER: g.setColor(Color.BLUE); break;
            case KILL: g.setColor(Color.RED); break;
            case WIN: g.setColor(Color.YELLOW); break; // amarillo
        }
        g.fillRect(
                x * SIZE - camera.getX(),
                y * SIZE - camera.getY(),
                SIZE, SIZE
            );
    }
}
