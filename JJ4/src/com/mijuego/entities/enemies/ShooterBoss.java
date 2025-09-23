package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
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
import com.mijuego.utils.ResourceManager;

public class ShooterBoss extends Enemies {

    private Player player;
    
    private final int DAMAGE_TAKEN = 10;


    private int shootCooldown = 0;
    private final int SHOOT_COOLDOWN_FRAMES = 90;
    
    private final int maxHealth = 100;
    
    private double chaseRange = 20 * Tile.SIZE;
    private double loseRange  = 20 * Tile.SIZE;
    private boolean chasing = false;
    
    private int barWidth;
    private int barHeight = GS.SC(5);

    private List<Bullet> bullets = new ArrayList<>();

    // 🔹 Sprite del jefe
    private BufferedImage spriteShooterBoss;

    public ShooterBoss(double x, double y, TileMap map, Player player) {
        super(x, y, GS.SC(40), GS.SC(40), 100, map);
        this.player = player;
        this.barWidth = GS.SC(50);
        this.speed = GS.DSC(1.5);
        this.isBoss = true;

        // Cargar textura del jefe
        spriteShooterBoss = ResourceManager.loadImage("/assets/sprites/shooterBoss.png");
    }

    @Override
    public void update() {
        if (!isAlive()) return;
        
        dx = facingRight ? speed : -speed;

        CollisionManager.checkTileCollisionX(this, map);

        if (dx == 0) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        int nextCol = facingRight 
            ? (int)((x + width + dx) / Tile.SIZE)
            : (int)((x + dx) / Tile.SIZE);
        int footRow = (int)((y + height + 1) / Tile.SIZE);

        if (!map.isTileSolid(footRow, nextCol)) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        x += dx;

        dy += GS.DSC(0.2);
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;
        
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

        if (spriteShooterBoss != null) {
            g.drawImage(spriteShooterBoss, drawX, drawY, width, height, null);
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(drawX, drawY, width, height);
        }

        for (Bullet b : bullets) b.draw(g, camera);
        
        // 🔹 Barra de vida
        int barX = drawX + width / 2 - barWidth / 2;
        int barY = drawY - 10;

        g.setColor(Color.GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        int healthWidth = (int)((health / (double) maxHealth) * barWidth);
        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, healthWidth, barHeight);

        g.setColor(Color.RED);
        g.fillRect(barX + healthWidth, barY, barWidth - healthWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
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

    public void checkPlayerCollision(Player player) {
        if (!active) return;

        Rectangle enemyBounds = this.getBounds();
        Rectangle playerBounds = player.getBounds();

        if (player.getDy() > 0 && playerBounds.intersects(enemyBounds)) {
            if (player.getY() + player.getHeight() - GS.SC(5) < y + height / GS.DSC(2)) {
                this.damage(DAMAGE_TAKEN);
                player.setDy(GS.DSC(-5));
                if (!isAlive()) {
                    deactivate();
                    AudioManager.playGoombaStomp();
                    player.addCoins(2);
                }
                return;
            }
        }

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
            g.setColor(Color.white);
            g.fillOval((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        }
    }
}