package com.mijuego.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mijuego.utils.InputManager;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;

public class Player extends Entities {

    private TileMap map;      // Para poder usar colisiones con tiles
    private Color color = Color.BLUE; // Color para dibujar el player (por ahora)

    private boolean onGround = false; // Para manejar salto

    private final double GRAVITY = 0.5;
    private final double JUMP_SPEED = -10;
    private final double MOVE_SPEED = 3;

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

        // 🔹 Colisiones con el mapa
        CollisionManager.checkTileCollision(this, map);

        // 🔹 Actualizar posición
        x += dx;
        y += dy;

        // 🔹 Actualizar si está en el suelo
        if (dy == 0) {
            onGround = true;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, width, height);
    }
}
