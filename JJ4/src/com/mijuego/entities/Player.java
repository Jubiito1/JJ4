package com.mijuego.entities;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mijuego.map.TileMap;
import com.mijuego.map.Tile;
import com.mijuego.utils.CollisionManager;
import com.mijuego.utils.InputManager;
import com.mijuego.core.GS;
import com.mijuego.core.Camera;

public class Player extends Entities {

    private TileMap map;
    private Color color = Color.BLUE;
    private int coins = 0; // contador de monedas

    private int damageCooldown = 0; // frames restantes hasta poder recibir daño otra vez
    private final int DAMAGE_COOLDOWN_FRAMES = 20;

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
        if (damageCooldown > 0) damageCooldown--;

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

        // 🔹 Actualizar onGround
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

        // 🔹 Chequear si toca tile mortal
        int topRow = (int)y / tileSize;
        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (map.getTile(row, col).isKill()) {
                    this.damage(this.getHealth()); // vida a 0
                    return;
                }
            }
        }
    }
    
    public void addCoins(int amount) {
        coins += amount;
    }
    
 // Getter
    public int getCoins() {
        return coins;
    }

    public void takeDamage(int amount) {
        if (damageCooldown == 0) {
            damage(amount);
            damageCooldown = DAMAGE_COOLDOWN_FRAMES;
        }
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!isAlive()) {
            g.setColor(Color.RED);
        } else {
            g.setColor(color);
        }
        g.fillRect(
            (int)(x - camera.getX()),
            (int)(y - camera.getY()),
            width, height
        );
    }
}
