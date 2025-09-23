package com.mijuego.entities.enemies;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.mijuego.entities.Player;
import com.mijuego.map.TileMap;
import com.mijuego.map.Tile;
import com.mijuego.core.GS;
import com.mijuego.core.Camera;
import com.mijuego.entities.Entities;
import com.mijuego.utils.ResourceManager;

public class Shooter extends Enemies {

    private Player player;



    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_FRAMES = 90;

    private double chaseRange = 10 * Tile.SIZE;
    private double loseRange  = 10 * Tile.SIZE;
    private boolean chasing = false;

    private List<Bullet> bullets = new ArrayList<>();

    // 🔹 Sprite del enemigo
    private BufferedImage spriteShooter;

    public Shooter(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(20), GS.SC(20), 2, map);
        this.player = player;

        // Cargar textura del enemigo
        spriteShooter = ResourceManager.loadImage("/assets/sprites/shooter.png");
    }

    @Override
    public void update() {
        if (!isAlive()) return;

        checkPlayerStomp();

        double distX = player.getX() - this.x;

        if (!chasing && Math.abs(distX) <= chaseRange) {
            chasing = true;
        } else if (chasing && Math.abs(distX) > loseRange) {
            chasing = false;
        }

        if (chasing) {
            if (shootCooldown <= 0) {
                shoot();
                shootCooldown = SHOOT_COOLDOWN_FRAMES;
            } else {
                shootCooldown--;
            }
        }

        bullets.removeIf(b -> !b.isAlive());
        for (Bullet b : bullets) {
            b.update();
            if (b.isAlive() && player != null && player.isAlive()) {
                if (b.getBounds().intersects(player.getBounds())) {
                    player.takeDamage(1);
                    b.damage(b.getHealth());
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!isAlive()) return;

        int drawX = (int)(x - camera.getX());
        int drawY = (int)(y - camera.getY());
        int drawWidth = width;
        int drawHeight = height;

        if (spriteShooter != null) {
            g.drawImage(spriteShooter, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            // fallback si no se carga la textura
            g.setColor(java.awt.Color.ORANGE);
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
        }

        for (Bullet b : bullets) b.draw(g, camera);
    }

    private void shoot() {
        double bulletSpeed = GS.SC(4);

        double dx = player.getX() + player.getWidth()/2 - (x + width/2);
        double dy = player.getY() + player.getHeight()/2 - (y + height/2);

        double dist = Math.sqrt(dx*dx + dy*dy);
        dx = dx / dist * bulletSpeed;
        dy = dy / dist * bulletSpeed;

        int bulletSize = GS.SC(8);

        bullets.add(new Bullet(x + width/2 - bulletSize/2, y + height/2 - bulletSize/2, bulletSize, bulletSize, dx, dy));
    }

    public void takeDamage(int amount) {
        this.damage(amount);
        if (!isAlive()) {
            bullets.clear();
            active = false;
        }
    }

    private void checkPlayerStomp() {
        Player player = this.player;
        if (player == null || !player.isAlive()) return;

        boolean intersects = player.getBounds().intersects(this.getBounds());
        boolean isAbove = (player.getY() + player.getHeight() <= this.getY() + GS.SC(5));

        if (intersects && isAbove && player.getDy() > 0) {
            takeDamage(this.getHealth());
            player.setDy(GS.DSC(-5));
        }

        Rectangle enemyBounds = this.getBounds();
        Rectangle playerBounds = player.getBounds();

        if (playerBounds.intersects(enemyBounds)) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;

            if (player.getX() < x) {
                player.setX(x - player.getWidth());
            } else {
                player.setX(x + width);
            }
        }
    }

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

            if (x < 0 || x > map.getCols() * Tile.SIZE || y < 0 || y > map.getRows() * Tile.SIZE) {
                this.damage(this.getHealth());
            }
        }

        public void draw(Graphics2D g, Camera camera) {
            if (!isAlive()) return;
            g.setColor(java.awt.Color.GREEN);
            g.fillOval((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        }
    }
}
