package com.mijuego.utils;

import com.mijuego.entities.Entities;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;

public class CollisionManager {

    // --- Colisión en eje X ---
    public static void checkTileCollisionX(Entities e, TileMap map) {
        if (e.getDx() == 0) return;

        double nextX = e.getX() + e.getDx();
        int width  = e.getWidth();
        int height = e.getHeight();
        int tileSize = Tile.SIZE;

        int leftCol   = (int)(nextX) / tileSize;
        int rightCol  = (int)(nextX + width - 1) / tileSize;
        int topRow    = (int)(e.getY()) / tileSize;
        int bottomRow = (int)(e.getY() + height - 1) / tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (map.isTileSolid(row, col)) {
                    // Ajustar posición según dirección
                    if (e.getDx() > 0) { // moviéndose a la derecha
                        e.setX(col * tileSize - width);
                    } else if (e.getDx() < 0) { // moviéndose a la izquierda
                        e.setX((col + 1) * tileSize);
                    }
                    e.setDx(0);
                    return; // colisión encontrada, no necesitamos seguir
                }
            }
        }
    }

    // --- Colisión en eje Y ---
    public static void checkTileCollisionY(Entities e, TileMap map) {
        if (e.getDy() == 0) return;

        double nextY = e.getY() + e.getDy();
        int width  = e.getWidth();
        int height = e.getHeight();
        int tileSize = Tile.SIZE;

        int leftCol   = (int)(e.getX()) / tileSize;
        int rightCol  = (int)(e.getX() + width - 1) / tileSize;
        int topRow    = (int)(nextY) / tileSize;
        int bottomRow = (int)(nextY + height - 1) / tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (map.isTileSolid(row, col)) {
                    // Ajustar posición según dirección
                    if (e.getDy() > 0) { // moviéndose hacia abajo
                        e.setY(row * tileSize - height);
                    } else if (e.getDy() < 0) { // moviéndose hacia arriba
                        e.setY((row + 1) * tileSize);
                    }
                    e.setDy(0);
                    return; // colisión encontrada
                }
            }
        }
    }
}
