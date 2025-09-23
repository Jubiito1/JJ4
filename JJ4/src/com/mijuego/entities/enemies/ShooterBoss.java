package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.utils.AudioManager;
import com.mijuego.utils.CollisionManager;
import com.mijuego.map.Tile;
import com.mijuego.core.GS;
import com.mijuego.core.Camera;
import com.mijuego.entities.Entities;

public class ShooterBoss extends Enemies {

    private Player player;
    
    private final int DAMAGE_TAKEN = 10;
    private final int DAMAGE_TO_PLAYER = 100;

    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_FRAMES = 90;
    
    private final int maxHealth = 100;
    
    private double chaseRange = 20 * Tile.SIZE;  // rango para empezar a perseguir
    private double loseRange  = 20 * Tile.SIZE;  // rango para dejar de perseguir
    private boolean chasing = false;
    
    private int barWidth;
    private int barHeight = GS.SC(5); // altura de la barra de vida

    private List<Bullet> bullets = new ArrayList<>();

    public ShooterBoss(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(40), GS.SC(40), 100, map); // tamaño, salud, mapa
        this.player = player;
        this.barWidth = GS.SC(50);
        this.speed = GS.DSC(1.5);
    }

    @Override
    public void update() {
        if (!isAlive()) return; // Si está muerto, no hace nada
        
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
        
        double distX = player.getX() - this.x;
        
        if (!chasing && Math.abs(distX) <= chaseRange) {
            chasing = true; // empieza a perseguir
        } 
        else if (chasing && Math.abs(distX) > loseRange) {
            chasing = false; // deja de perseguir
        }
        
        if (chasing) {
            // perseguir al jugador
            // disparo
            if (shootCooldown <= 0) {
                shoot();
                shootCooldown = SHOOT_COOLDOWN_FRAMES;
            } else {
                shootCooldown--;
            }
        }
        
        // actualizar balas
        bullets.removeIf(b -> !b.isAlive());
        for (Bullet b : bullets) {
            b.update();

            // Verificar colisión con player
            if (b.isAlive() && player != null && player.isAlive()) {
                if (b.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(1); // le quitamos vida al player
                    b.damage(b.getHealth()); // destruimos la bala
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!isAlive()) return;

        g.setColor(Color.ORANGE);
        g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);

        for (Bullet b : bullets) b.draw(g, camera);
        
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

    private void shoot() {
        double bulletSpeed = GS.SC(4); // velocidad, la escala ya la aplicamos

        // calcular dirección hacia el jugador
        double dx = player.getX() + player.getWidth()/2 - (x + width/2);
        double dy = player.getY() + player.getHeight()/2 - (y + height/2);

        double dist = Math.sqrt(dx*dx + dy*dy);
        dx = dx / dist * bulletSpeed;
        dy = dy / dist * bulletSpeed;

        // tamaño de la bala más grande, por ejemplo 10x10 escalado
        int bulletSize = GS.SC(8);

        bullets.add(new Bullet(x + width/2 - bulletSize/2, y + height/2 - bulletSize/2, bulletSize, bulletSize, dx, dy));
    }
    public void takeDamage(int amount) {
        this.damage(amount); // reduce health
        if (!isAlive()) {
           
            bullets.clear(); // destruye todas las balas activas
            active = false;  // marca al enemigo como inactivo
        }
    }

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


    // Clase interna para proyectiles
    public class Bullet extends Entities {
        private double dx, dy;

        public Bullet(double x, double y, int w, int h, double dx, double dy) {
            super(x, y, w, h, 1);
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public void update() {
            x += dx;
            y += dy;

            // destruir si sale del mapa
            if (x < 0 || x > map.getCols() * Tile.SIZE || y < 0 || y > map.getRows() * Tile.SIZE) {
                this.damage(this.getHealth());
            }
        }

        public void draw(Graphics2D g, Camera camera) {
            if (!isAlive()) return;
            g.setColor(Color.GREEN);
            // Dibujar óvalo en vez de rectángulo
            g.fillOval((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        }

    }
}