package com.mijuego.entities.enemies;

import com.mijuego.utils.AudioManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;
import com.mijuego.utils.ResourceManager;

public class JumperBoss extends Enemies {

    private final int DAMAGE_TO_PLAYER = 100;
    private final int DAMAGE_TAKEN = 20; // cantidad de daño que recibe al ser aplastado
    private boolean onGround = true;
    private final double JUMP_SPEED = GS.DSC(-6);
    private final double GRAVITY = GS.DSC(0.2);
    
    private final int maxHealth = 100;
    private int barWidth;
    private int barHeight = GS.SC(5); // altura de la barra de vida

    // 🔹 Sprites
    private BufferedImage spriteDerecha;
    private BufferedImage spriteIzquierda;

    public JumperBoss(double x, double y, TileMap map) {
        super(x, y, GS.SC(40), GS.SC(40), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(2.5);
        this.facingRight = true;
        this.barWidth = GS.SC(50);

        // Cargar sprites
        spriteDerecha = ResourceManager.loadImage("/assets/sprites/jumperBoss.png");
        spriteIzquierda = ResourceManager.loadImage("/assets/sprites/jumperBoss1.png");
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

        // --- Detectar si tocó el suelo ---
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

        BufferedImage spriteToDraw = facingRight ? spriteDerecha : spriteIzquierda;

        int drawX = (int)(x - camera.getX());
        int drawY = (int)(y - camera.getY());
        int drawWidth = width;
        int drawHeight = height;

        if (spriteToDraw != null) {
            g.drawImage(spriteToDraw, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            // fallback si no carga la imagen
            g.setColor(Color.DARK_GRAY);
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
        }

        // --- Barra de vida ---
        int barX = drawX + width / 2 - barWidth / 2;
        int barY = drawY - 10;

        // Fondo (gris)
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
                    player.addCoins(2);
                }
                return;
            }
        }

        // --- Colisión lateral ---
        if (playerBounds.intersects(enemyBounds)) {
            player.takeDamage(DAMAGE_TO_PLAYER);

            // Cambiar de dirección
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            // Separar al jugador
            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }
}

