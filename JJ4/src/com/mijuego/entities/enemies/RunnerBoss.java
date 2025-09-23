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

public class RunnerBoss extends Enemies {

    private final int DAMAGE_TO_PLAYER = 100;
    private int DAMAGE_TAKEN = 10; // cantidad de daño que recibe al ser aplastado
    
    private final int maxHealth = 100;
    
    private boolean enfurecido = false;
    private int enfurecidoTimer = 0;
    private final int ENFURECIDO_DURATION = 180; // 3 segundos a 60fps
    
    private final double GRAVITY = GS.DSC(0.2);
    private final double JUMP_SPEED = GS.DSC(-5);

    private double chaseRange = 10 * Tile.SIZE;  // rango para empezar a perseguir
    private double loseRange  = 15 * Tile.SIZE;  // rango para dejar de perseguir
    private boolean chasing = false;
    
    private int barWidth;
    private int barHeight = GS.SC(5); // altura de la barra de vida

    private Player target;

    public RunnerBoss(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(40), GS.SC(40), 100, map); // tamaño y vida ejemplo
        this.speed = GS.DSC(1);
        this.facingRight = true;
        this.target = player;
        this.barWidth = GS.SC(50); // ancho de la barra (puede ajustarse)
    }

    @Override
    public void update() {
        if (!active) return;

        if (enfurecido) {
            enfurecidoTimer--;
            if (enfurecidoTimer <= 0) {
                enfurecido = false;
                this.speed = GS.DSC(1); // velocidad base de patrulla
            }
        }

        if (!enfurecido) {
        
	        // --- Comprobar distancia al jugador ---
	        double distX = target.getX() - this.x;
	
	        if (!chasing && Math.abs(distX) <= chaseRange) {
	            chasing = true; // empieza a perseguir
	            this.speed = GS.DSC(2.5);
	        } 
	        else if (chasing && Math.abs(distX) > loseRange) {
	            chasing = false; // deja de perseguir
	            facingRight = !facingRight; // cambia dirección al perderlo
	        }
        }

        // --- Movimiento horizontal ---
        if (chasing && !enfurecido) {
            // perseguir al jugador
            double distX = target.getX() - this.x;
            facingRight = distX > 0;
            dx = facingRight ? speed : -speed;
        } else {
            // patrullando o enfurecido
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
        boolean bloque2 = map.isTileSolid(row - 4, col);    // segundo bloque arriba del primero

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

        g.setColor(Color.CYAN);// color 
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
                this.enfurecido = true;
                this.enfurecidoTimer = ENFURECIDO_DURATION;
                this.chasing = false;
                this.speed = GS.DSC(6);
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
