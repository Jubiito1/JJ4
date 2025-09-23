package com.mijuego.entities.enemies;

import com.mijuego.utils.AudioManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;
import com.mijuego.utils.ResourceManager;

public class Goomba extends Enemies {

    private final int DAMAGE_TO_PLAYER = 100;
    private final int DAMAGE_TAKEN = 100; // cantidad de daño que recibe al ser aplastado
    
    private BufferedImage spriteDerecha;
    private BufferedImage spriteIzquierda;

    public Goomba(double x, double y, TileMap map) {
        super(x, y, GS.SC(20), GS.SC(20), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(1.5);
        this.facingRight = true;

        // Carga de sprites
        spriteDerecha = ResourceManager.loadImage("/assets/sprites/foca.png");
        spriteIzquierda = ResourceManager.loadImage("/assets/sprites/foca1.png");
    }

    @Override
    public void update() {
        if (!active) return;

        // --- Movimiento horizontal ---
        dx = facingRight ? speed : -speed;

        // --- Colisión X con tiles ---
        CollisionManager.checkTileCollisionX(this, map);

        // Si chocó con pared (dx se volvió 0), cambiar de dirección
        if (dx == 0) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // --- Verificar borde de caída ---
        int nextCol = facingRight 
            ? (int)((x + width + dx) / Tile.SIZE)
            : (int)((x + dx) / Tile.SIZE);
        int footRow = (int)((y + height + 1) / Tile.SIZE);

        if (!map.isTileSolid(footRow, nextCol)) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        x += dx;

        // --- Gravedad simple ---
        dy += GS.DSC(0.2); // gravedad
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!active) return;

        BufferedImage spriteToDraw = facingRight ? spriteDerecha : spriteIzquierda;

        int drawWidth = width;
        int drawHeight = height;
        int drawX = (int)(x - camera.getX());
        int drawY = (int)(y - camera.getY());

        if (spriteToDraw != null) {
            g.drawImage(spriteToDraw, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            // fallback si falla la carga
            g.setColor(new Color(139, 69, 19));
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
        }
    }

    // --- Colisión con jugador ---
    public void checkPlayerCollision(Player player) {
        if (!active) return;

        Rectangle enemyBounds = this.getBounds();
        Rectangle playerBounds = player.getBounds();

        // --- Jugador cayendo sobre el enemigo (aplasta) ---
        if (player.getDy() > 0 && playerBounds.intersects(enemyBounds)) {
            if (player.getY() + player.getHeight() - GS.SC(5) < y + height / GS.DSC(2)) {
                this.damage(DAMAGE_TAKEN);
                player.setDy(GS.DSC(-5)); // rebote vertical
                if (!isAlive()) {
                    deactivate();
                    AudioManager.playGoombaStomp();
                    player.addCoins(2); // suma 200 puntos al matar enemigo
                }
                return;
            }
        }

        // --- Colisión lateral ---
        if (playerBounds.intersects(enemyBounds)) {
            player.takeDamage(DAMAGE_TO_PLAYER);

            // Cambiar de dirección al chocar
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            // Opcional: separar al jugador
            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }
}

