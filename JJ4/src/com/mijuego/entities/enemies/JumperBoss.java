package com.mijuego.entities.enemies;

import com.mijuego.utils.AudioManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;

public class JumperBoss extends Enemies {

    private final int DAMAGE_TO_PLAYER = 100;
    private final int DAMAGE_TAKEN = 20; // cantidad de daño que recibe al ser aplastado
    private boolean onGround = true;
    private final double JUMP_SPEED = GS.DSC(-6);
    private final double GRAVITY = GS.DSC(0.2);
    
    private final int maxHealth = 100;
    private int barWidth;
    private int barHeight = GS.SC(5); // altura de la barra de vida

    public JumperBoss(double x, double y, TileMap map) {
        super(x, y, GS.SC(40), GS.SC(40), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(2.5);
        this.facingRight = true;
        this.barWidth = GS.SC(50);
    }

    @Override
    public void update() {
        if (!active) return;

        // Movimiento horizontal
        dx = facingRight ? speed : -speed;

        // Colisión X con tiles
        CollisionManager.checkTileCollisionX(this, map);
        
        // Si chocó con pared, cambia dirección
        if (dx == 0) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // Aplicar gravedad
        dy += GRAVITY;

        // Colisiones en Y
        double oldDy = dy;
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;

        // --- Aquí detectamos si tocó el suelo ---
        if (dy == 0 && oldDy > 0) {
            onGround = true;
        }

        // Saltar automáticamente al tocar suelo
        if (onGround) {
            dy = JUMP_SPEED;
            onGround = false;
        }

        x += dx;
    }



    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!active) return;

        g.setColor(Color.DARK_GRAY); // color marrón
        g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        
        int screenX = (int)(x - camera.getX());
        int screenY = (int)(y - camera.getY());
        
        int barX = screenX + width / 2 - barWidth / 2;
        int barY = screenY - 10;

        // Fondo de la barra (gris)
        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        // Vida actual (verde)
        int healthWidth = (int)((health / (double) maxHealth) * barWidth);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, barHeight);

        // Vida perdida (roja)
        g.setColor(Color.RED);
        g.fillRect(barX + healthWidth, barY, barWidth - healthWidth, barHeight);

        // Borde negro
        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
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
