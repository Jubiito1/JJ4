package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.AudioManager;
import com.mijuego.utils.CollisionManager;
import com.mijuego.utils.ResourceManager;

public class Jumper extends Enemies {

    private final int DAMAGE_TO_PLAYER = 100;
    private final int DAMAGE_TAKEN = 50; // cantidad de daño que recibe al ser aplastado
    
    private final double GRAVITY = GS.DSC(0.2);
    private final double JUMP_SPEED = GS.DSC(-2);

    private double chaseRange = 10 * Tile.SIZE;  // rango para empezar a perseguir
    private double loseRange  = 15 * Tile.SIZE;  // rango para dejar de perseguir
    private boolean chasing = false;

    private Player target;

    // 🔹 Sprites
    private BufferedImage spriteDerecha;
    private BufferedImage spriteIzquierda;

    public Jumper(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(20), GS.SC(20), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(1);
        this.facingRight = true;
        this.target = player;

        // Cargar sprites
        spriteDerecha = ResourceManager.loadImage("/assets/sprites/orca.png");
        spriteIzquierda = ResourceManager.loadImage("/assets/sprites/orca1.png");
    }

    @Override
    public void update() {
        if (!active) return;

        // --- Comprobar distancia al jugador ---
        double distX = target.getX() - this.x;

        if (!chasing && Math.abs(distX) <= chaseRange) {
            chasing = true; // empieza a perseguir
            this.speed = GS.DSC(2);
        } 
        else if (chasing && Math.abs(distX) > loseRange) {
            chasing = false; // deja de perseguir
            facingRight = !facingRight; // cambia dirección al perderlo
        }

        // --- Movimiento horizontal ---
        if (chasing) {
            // perseguir al jugador
            facingRight = distX > 0;
            dx = facingRight ? speed : -speed;
        } else {
            // patrullando como goomba
            dx = facingRight ? speed : -speed;
        }

        manejarMuro();

        // --- Colisión eje X ---
        CollisionManager.checkTileCollisionX(this, map);
        if (dx == 0) { 
            // choca con pared
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }
        x += dx;

        // --- Gravedad y colisión eje Y ---
        dy += GRAVITY;
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;
    }

    private void manejarMuro() {
        int col = facingRight ? (int)((x + width) / Tile.SIZE) : (int)((x - 2) / Tile.SIZE);
        int row = (int)(y / Tile.SIZE);

        boolean bloque1 = map.isTileSolid(row, col);        // primer bloque frente a él
        boolean bloque2 = map.isTileSolid(row - 1, col);    // segundo bloque arriba del primero

        if (bloque1 && !bloque2) {
            // Muro de 1 tile → lo salta
            dy = JUMP_SPEED;
        } else if (bloque1 && bloque2) {
            // Muro de 2 tiles o más → cambia de dirección
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }
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
            // fallback si no carga la imagen
            g.setColor(Color.MAGENTA);
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
                if (!isAlive()) deactivate();
                AudioManager.playGoombaStomp();
                return;
            }
        }

        // --- Colisión lateral ---
        if (playerBounds.intersects(enemyBounds)) {
            player.takeDamage(DAMAGE_TO_PLAYER);

            // Cambiar de dirección
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            // Separar jugador
            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }
}
