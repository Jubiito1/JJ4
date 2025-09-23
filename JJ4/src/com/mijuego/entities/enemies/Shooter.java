package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.map.Tile;
import com.mijuego.core.GS;
import com.mijuego.core.Camera;
import com.mijuego.entities.Entities;

public class Shooter extends Enemies {

    private Player player;
    
    private final int DAMAGE_TO_PLAYER = 100;

    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_FRAMES = 90;
    
    private double chaseRange = 10 * Tile.SIZE;  // rango para empezar a perseguir
    private double loseRange  = 10 * Tile.SIZE;  // rango para dejar de perseguir
    private boolean chasing = false;

    private List<Bullet> bullets = new ArrayList<>();

    public Shooter(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(20), GS.SC(20), 2, map); // tamaño, salud, mapa
        this.player = player;
    }

    @Override
    public void update() {
        if (!isAlive()) return; // Si está muerto, no hace nada

        // Revisar si el player lo pisa
        checkPlayerStomp();
        
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
    private void checkPlayerStomp() {
        Player player = this.player; // tu referencia al player

        if (player == null || !player.isAlive()) return;


        boolean intersects = player.getBounds().intersects(this.getBounds());
        boolean isAbove = (player.getY() + player.getHeight() <= this.getY() + GS.SC(5));

        if (intersects && isAbove && player.getDy() > 0) { // el player está cayendo sobre el enemigo
            takeDamage(this.getHealth()); // mata al enemigo
            player.setDy(GS.DSC(-5)); // rebote del player al saltar encima
        }
        
        Rectangle enemyBounds = this.getBounds();
        Rectangle playerBounds = player.getBounds();
        
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