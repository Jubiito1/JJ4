package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;

public class Goomba extends Enemies {

    private final int DAMAGE_TO_PLAYER = 20;
    private final int DAMAGE_TAKEN = 100; // cantidad de daño que recibe al ser aplastado

    public Goomba(double x, double y, TileMap map) {
        super(x, y, GS.SC(20), GS.SC(20), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(1.5);
        this.facingRight = true;
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

        g.setColor(new Color(139, 69, 19)); // color marrón
        g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
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
                if (!isAlive()) deactivate();
                return;
            }
        }

        // --- Colisión lateral ---
        if (playerBounds.intersects(enemyBounds)) {
            // Aplicar daño con cooldown
            player.takeDamage(DAMAGE_TO_PLAYER);

            // Cambiar de dirección al Goomba
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            // Opcional: separar al jugador para que no se quede pegado
            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }
}
