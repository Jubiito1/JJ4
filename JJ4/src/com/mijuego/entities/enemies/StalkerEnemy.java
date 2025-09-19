package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mijuego.core.Camera;
import com.mijuego.entities.Player;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;

public class StalkerEnemy extends Enemies {

    // Configuración (en tiles)
    private final int DETECT_RANGE_TILES = 10;      // entra a perseguir si player <= 10 tiles
    private final int STOP_CHASE_RANGE_TILES = 15; // deja de perseguir si player >= 15 tiles

    // físicas
    private final double JUMP_SPEED = -5;
    private final double GRAVITY = 0.2;
    private final double CHASE_SPEED_MULT = 1.6; // multiplicador de velocidad en persecución

    // Estado
    private boolean chasing = false;
    private Player targetPlayer = null;

    public StalkerEnemy(double x, double y, TileMap map) {
        super(x, y, 20, 20, 100, map); // mismo tamaño/vida que Goomba (ajustá si querés)
        this.speed = 1.5;              // velocidad base (puede ajustarse)
        this.facingRight = true;
    }

    @Override
    public void update() {
        if (!active) return;

        // --- Decidir velocidad horizontal (patrulla vs persecución) ---
        double intendedDx = facingRight ? speed : -speed;

        if (chasing && targetPlayer != null) {
            // Si está persiguiendo, moverse hacia el player
            if (targetPlayer.getX() < x) {
                intendedDx = -speed * CHASE_SPEED_MULT;
                facingRight = false;
            } else {
                intendedDx = speed * CHASE_SPEED_MULT;
                facingRight = true;
            }
        }

        dx = intendedDx;

        // --- Colisión X con tiles (ajusta x/dx si colisiona) ---
        CollisionManager.checkTileCollisionX(this, map);

        // Si chocó con pared (dx se volvió 0), cambiar de dirección
        if (dx == 0) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // --- Verificar borde y muros delante ---
        int nextCol = facingRight
            ? (int)((x + width + dx) / Tile.SIZE)
            : (int)((x + dx) / Tile.SIZE);
        int footRow = (int)((y + height + 1) / Tile.SIZE);

        // Si no hay suelo en la celda de adelante -> dar vuelta (borde)
        if (!map.isTileSolid(footRow, nextCol)) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // Calcular altura del muro en frente (cuántos tiles sólidos consecutivos desde el suelo hacia arriba)
        int wallHeight = 1;
        for (int h = 0; h < 3; h++) { // 0..2 (nos interesa 1 ó 2)
            int row = footRow - h;
            if (row < 0) break;
            if (map.isTileSolid(row, nextCol)) wallHeight++;
            else break;
        }

        // Determinar si está en el suelo (mirando tiles justo debajo)
        boolean onGround = false;
        int leftCol = (int)(x) / Tile.SIZE;
        int rightCol = (int)(x + width - 1) / Tile.SIZE;
        int bottomRow = (int)((y + height) / Tile.SIZE);
        for (int c = leftCol; c <= rightCol; c++) {
            if (map.isTileSolid(bottomRow, c)) {
                onGround = true;
                break;
            }
        }

        // Si muro de 1 bloque y está en el suelo -> saltar
        if (wallHeight == 1 && onGround) {
            dy = JUMP_SPEED;
        } else if (wallHeight >= 2) {
            // muro de 2 o más -> dar vuelta
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // Aplicar movimiento horizontal final
        x += dx;

        // --- Gravedad Y movimiento vertical ---
        dy += GRAVITY;
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!active) return;
        g.setColor(Color.MAGENTA);
        g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
    }

    /**
     * Método para manejar colisión con el player y, además,
     * actualizar el estado de persecución según la distancia.
     *
     * En tu GameLoop actualmente llamás esto para Goomba; hacelo también
     * para StalkerEnemy para que la persecución se active/desactive.
     */
    public void checkPlayerCollision(Player player) {
        if (!active) return;

        // guardar referencia y actualizar bandera de persecución según distancia en tiles
        this.targetPlayer = player;
        double dxTiles = (player.getX() - this.x) / Tile.SIZE;
        if (Math.abs(dxTiles) <= DETECT_RANGE_TILES) {
            chasing = true;
        } else if (Math.abs(dxTiles) >= STOP_CHASE_RANGE_TILES) {
            chasing = false;
        }

        Rectangle enemyBounds = this.getBounds();
        Rectangle playerBounds = player.getBounds();

        // --- Jugador cayendo sobre el enemigo (aplasta) ---
        if (player.getDy() > 0 && playerBounds.intersects(enemyBounds)) {
            if (player.getY() + player.getHeight() - 5 < y + height / 2) {
                this.damage(100);           // recibe daño masivo (aplaste)
                player.setDy(-5);           // rebote del jugador
                if (!isAlive()) deactivate();
                return;
            }
        }

        // --- Colisión lateral: dañar al player y separar ---
        if (playerBounds.intersects(enemyBounds)) {
            player.takeDamage(20);

            // Cambiar de dirección al impactar
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            // Separar al jugador para que no quede "pegado"
            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }
}
