package com.mijuego.utils;

import java.awt.Rectangle;
import com.mijuego.entities.Entities;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;

public class CollisionManager {

    // Verifica colisiones entre una entidad y el mapa
    public static void checkTileCollision(Entities e, TileMap map) {
        double nextX = e.getX() + e.getDx();
        double nextY = e.getY() + e.getDy();

        Rectangle futureBounds = new Rectangle(
            (int) nextX,
            (int) nextY,
            e.getWidth(),
            e.getHeight()
        );

        int tileSize = Tile.SIZE;
        int leftCol   = futureBounds.x / tileSize;
        int rightCol  = (futureBounds.x + futureBounds.width - 1) / tileSize;
        int topRow    = futureBounds.y / tileSize;
        int bottomRow = (futureBounds.y + futureBounds.height - 1) / tileSize;

        boolean collision = false;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (map.isTileSolid(row, col)) {
                    collision = true;
                }
            }
        }

        if (collision) {
            e.setDx(0);
            e.setDy(0);
        }
    }
}
