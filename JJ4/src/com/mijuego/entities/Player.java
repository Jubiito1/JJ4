package com.mijuego.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mijuego.utils.InputManager;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;
import com.mijuego.map.Tile;
import com.mijuego.core.GS;
import com.mijuego.core.Camera;

public class Player extends Entities {

    private TileMap map;          // mapa actual para colisiones
    private Color color = Color.BLUE;

    private boolean onGround = false;

    private final double GRAVITY = GS.DSC(0.2);
    private final double JUMP_SPEED = GS.DSC(-5);
    private final double MOVE_SPEED = GS.DSC(3);

    public Player(double x, double y, int width, int height, int health, TileMap map) {
        super(x, y, width, height, health);
        this.map = map;
    }

    @Override
    public void update() {
        // 🔹 Movimiento horizontal
        dx = 0;
        if (InputManager.isLeft())  dx = -MOVE_SPEED;
        if (InputManager.isRight()) dx = MOVE_SPEED;

        // 🔹 Salto
        if (InputManager.isUp() && onGround) {
            dy = JUMP_SPEED;
            onGround = false;
        }

        // 🔹 Gravedad
        dy += GRAVITY;

        // 🔹 Colisiones por eje
        CollisionManager.checkTileCollisionX(this, map);
        x += dx;

        CollisionManager.checkTileCollisionY(this, map);
        y += dy;

        // 🔹 Actualizar onGround basado en tiles sólidos debajo del jugador
        int tileSize = Tile.SIZE;
        int leftCol = (int)x / tileSize;
        int rightCol = (int)(x + width - 1) / tileSize;
        int bottomRow = (int)(y + height) / tileSize;

        onGround = false;
        for (int col = leftCol; col <= rightCol; col++) {
            if (map.isTileSolid(bottomRow, col)) {
                onGround = true;
                break;
            }
        }
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        g.setColor(color);
        g.fillRect(
            (int)(x - camera.getX()),
            (int)(y - camera.getY()),
            width, height
        );
    }
}
