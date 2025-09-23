package com.mijuego.map;

import java.awt.Color;
import java.awt.Graphics2D;
import com.mijuego.core.Camera;
import com.mijuego.core.GS;

public class Tile {
    public static final int SIZE = GS.SC(20);

    public static final int EMPTY = 0; //Vacio
    public static final int SOLID = 1; //Plataforma
    public static final int GOOMBA = 2; //Enemigo Goomba
    public static final int PLAYER = 3; //Jugador
    public static final int KILL = 4;  // Muerte
    public static final int WIN = 5; // Next Level
    public static final int COIN = 6; //Moneda
    public static final int JUMPER = 7; //Enemigo Jumper
    public static final int TRAMPOLINE = 8; //Trampolin
    public static final int SHOOTER = 9; //Enemigo Shooter
    public static final int STEELSENTINEL = 10; //Boss lvl 1
    
    
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
            case COIN: g.setColor(Color.YELLOW); break;
            case JUMPER: g.setColor(Color.MAGENTA); break; 
            case TRAMPOLINE: g.setColor(Color.CYAN); break;
            case SHOOTER: g.setColor(Color.ORANGE); break;
            case STEELSENTINEL: g.setColor(Color.PINK); break;
        }
        g.fillRect(
                x * SIZE - camera.getX(),
                y * SIZE - camera.getY(),
                SIZE, SIZE
            );
    }
}