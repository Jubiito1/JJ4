package com.mijuego.core;

import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.map.Tile;

public class Camera {
    private double x, y;
    private int viewWidth, viewHeight;
    private TileMap map;

    public Camera(int viewWidth, int viewHeight, TileMap map) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.map = map;
        this.x = 0;
        this.y = 0;
    }

    public void follow(Player player) {
        x = player.getX() + player.getWidth() / 2 - viewWidth / 2;
        y = player.getY() + player.getHeight() / 2 - viewHeight / 2;

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        int maxX = map.getCols() * Tile.SIZE - viewWidth;
        int maxY = map.getRows() * Tile.SIZE - viewHeight;

        if (x > maxX) x = maxX;
        if (y > maxY) y = maxY;
    }

    // 🔹 NUEVO setter para actualizar TileMap
    public void setMap(TileMap map) {
        this.map = map;
    }

    public int getX() { return (int)x; }
    public int getY() { return (int)y; }
}
