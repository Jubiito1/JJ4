package com.mijuego.entities.enemies;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.mijuego.core.Camera;
import com.mijuego.core.GS;
import com.mijuego.entities.Player;
import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.CollisionManager;

/**
 * Boss: SteelSentinel (Centinela de Acero)
 * - Tamaño grande (3x3 tiles)
 * - Varios ataques: martillazo (área), láser (proyectil rápido) y misiles (homing)
 * - Cambia comportamiento según fase (vida restante)
 */
public class SteelSentinel extends Enemies {

    // Estados (no obligatorios, pero útiles para debug/logic)
    private static final int STATE_IDLE = 0;
    private static final int STATE_WALK = 1;
    private static final int STATE_HAMMER = 2;
    private static final int STATE_LASER = 3;
    private static final int STATE_MISSILE = 4;
    private static final int STATE_DYING = 5;

    private int state = STATE_IDLE;

    private final Player target;

    private final int maxHealth = 300;

    // Ataques / tiempos (en frames, asumiendo 60 FPS)
    private int attackCooldown = 180; // frames entre decidir ataque (3s)
    private int attackTimer = 0;

    private int hammerDuration = 30; // frames que el AOE está activo
    private int hammerTimer = 0;

    // Proyectiles
    private final List<Missile> missiles = new ArrayList<>();
    private final List<Laser> lasers = new ArrayList<>();

    // físicas
    private final double GRAVITY = GS.DSC(0.2);
    
    private int currentHealth;
    private int barWidth;
    private int barHeight = 5; // altura de la barra de vida
    private Color barBackgroundColor = Color.RED;
    private Color barForegroundColor = Color.GREEN;

    public SteelSentinel(double x, double y, TileMap map, Player player) {
        // tamaño 3x3 tiles
        super(x, y, Tile.SIZE * 3, Tile.SIZE * 3, 300, map);

        this.target = player;

        // velocidad base (es lento)
        this.speed = GS.DSC(0.6);
        this.facingRight = true;
        this.active = true;
        this.barWidth = 50; // ancho de la barra (puede ajustarse)
    }

    @Override
    public void update() {
        if (!active) return;
        if (!isAlive()) {
            // handle dying state
            active = false;
            return;
        }

        // timers
        if (attackTimer > 0) attackTimer--;
        if (hammerTimer > 0) hammerTimer--;

        // --- Movimiento simple: patrulla como Goomba pero lento ---
        dx = facingRight ? speed : -speed;
        CollisionManager.checkTileCollisionX(this, map);
        if (dx == 0) {
            // choca con pared -> cambia de dirección
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        // Comprueba borde para no caer
        int nextCol = facingRight 
            ? (int)((x + width + dx) / Tile.SIZE)
            : (int)((x + dx) / Tile.SIZE);
        int footRow = (int)((y + height + 1) / Tile.SIZE);

        if (!map.isTileSolid(footRow, nextCol)) {
            facingRight = !facingRight;
            dx = facingRight ? speed : -speed;
        }

        x += dx;

        // gravedad
        dy += GRAVITY;
        CollisionManager.checkTileCollisionY(this, map);
        y += dy;

        // --- Decidir ataque si el cooldown llegó a 0 ---
        if (attackTimer <= 0) {
            chooseAttackPhase();
        }

        // --- Actualizar proyectiles ---
        Iterator<Missile> mit = missiles.iterator();
        while (mit.hasNext()) {
            Missile m = mit.next();
            m.update();
            if (!m.isAlive()) {
                mit.remove();
                continue;
            }
            // colisión con player
            if (target != null && target.isAlive() && m.getBounds().intersects(target.getBounds())) {
                target.takeDamage(2); // aplica daño al jugador (tu método internal define cómo exactamente se reduce)
                m.damage(m.getHealth());
            }
        }

        Iterator<Laser> lit = lasers.iterator();
        while (lit.hasNext()) {
            Laser l = lit.next();
            l.update();
            if (!l.isAlive()) {
                lit.remove();
                continue;
            }
            if (target != null && target.isAlive() && l.getBounds().intersects(target.getBounds())) {
                target.takeDamage(3);
                l.damage(l.getHealth());
            }
        }

        // --- Hammer AOE (si está activo) ---
        if (hammerTimer > 0 && target != null && target.isAlive()) {
            Rectangle aoe = getHammerAOE();
            if (aoe.intersects(target.getBounds())) {
                target.takeDamage(2);
            }
        }

        // --- Allow player to stomp the boss (daño por pisada) ---
        checkPlayerStomp();
    }

    private void chooseAttackPhase() {
        double fraction = (double)getHealth() / (double)maxHealth;

        if (fraction > 0.7) {
            // Fase 1: hammer o láser
            if (Math.random() < 0.5) startHammer();
            else startLaser();
            attackTimer = attackCooldown;
        } else if (fraction > 0.3) {
            // Fase 2: hammer, láser o misiles
            double r = Math.random();
            if (r < 0.33) startHammer();
            else if (r < 0.66) startLaser();
            else startMissile();
            attackTimer = (int)(attackCooldown * 0.8);
        } else {
            // Fase 3: más agresivo
            // Aumentar velocidad levemente en fase 3
            this.speed = GS.DSC(1.0);

            if (Math.random() < 0.6) {
                // combinación: láser + martillazo
                startLaser();
                startHammer();
            } else {
                startMissile();
            }
            attackTimer = (int)(attackCooldown * 0.6);
        }
    }

    private void startHammer() {
        state = STATE_HAMMER;
        hammerTimer = hammerDuration;
    }

    private void startLaser() {
        state = STATE_LASER;
        // spawn laser from "eye" roughly at top center
        double lx = x + width / 2.0 - GS.SC(4);
        double ly = y + GS.SC(12); // ojo aproximado
        boolean dirRight = (target != null) ? (target.getX() + target.getWidth()/2 > lx) : facingRight;
        lasers.add(new Laser(lx, ly, dirRight));
    }

    private void startMissile() {
        state = STATE_MISSILE;
        // lanza 2 misiles con ligeros offsets
        double mx = x + width / 2.0;
        double my = y + GS.SC(10);
        missiles.add(new Missile(mx - GS.SC(8), my, target));
        missiles.add(new Missile(mx + GS.SC(8), my, target));
    }

    private Rectangle getHammerAOE() {
        // área frontal en el suelo: ancho = width + 2 tiles, altura pequeña
        int aoeX = (int)(x - Tile.SIZE);
        int aoeY = (int)(y + height - GS.SC(10));
        int aoeW = width + Tile.SIZE * 2;
        int aoeH = GS.SC(16);
        return new Rectangle(aoeX, aoeY, aoeW, aoeH);
    }

    private void checkPlayerStomp() {
        if (target == null || !target.isAlive()) return;

        Rectangle bossBounds = this.getBounds();
        Rectangle playerBounds = target.getBounds();

        boolean intersects = playerBounds.intersects(bossBounds);
        boolean isAbove = (target.getY() + target.getHeight() <= this.getY() + GS.SC(12));

        if (intersects && isAbove && target.getDy() > 0) {
            // daño fuerte al boss si lo pisan
            this.damage(50); // recibe 50 de daño por pisada (ajustable)
            target.setVelY(GS.DSC(-5)); // rebote
            if (!isAlive()) {
                deactivate();
            }
        }
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (!active) return;
        // cuerpo del boss
        g.setColor(new Color(120, 120, 140)); // gris metálico
        g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);

        // ojo (just for visual cue)
        int eyeW = GS.SC(8);
        int eyeH = GS.SC(8);
        int eyeX = (int)(x + width/2 - eyeW/2 - camera.getX());
        int eyeY = (int)(y + GS.SC(10) - camera.getY());
        g.setColor(Color.RED);
        g.fillOval(eyeX, eyeY, eyeW, eyeH);

        // dibujar proyectiles
        for (Missile m : missiles) m.draw(g, camera);
        for (Laser l : lasers) l.draw(g, camera);

     

        int screenX = (int)(x - camera.getX());
        int screenY = (int)(y - camera.getY());


        // Posición de la barra sobre la cabeza
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
    

    @Override
    public void deactivate() {
        this.active = false;
    }

  
    public void draw(Graphics2D g) {
        // no usar: seguimos la firma draw(g, camera) usada por tu motor
    }

    // --- inner classes para proyectiles ---
    private class Laser extends com.mijuego.entities.Entities {
        private final double dx;
        private int life = 120; // frames (~2s)

        public Laser(double x, double y, boolean right) {
            super(x, y, GS.SC(6), GS.SC(4), 1);
            this.dx = right ? GS.DSC(8) : -GS.DSC(8);
        }

        @Override
        public void update() {
            x += dx;
            life--;
            // destruir si sale del mapa o choca con tile sólido
            if (x < 0 || x > map.getCols() * Tile.SIZE || life <= 0) {
                this.damage(this.getHealth());
                return;
            }
            int col = (int)((x + width/2) / Tile.SIZE);
            int row = (int)((y + height/2) / Tile.SIZE);
            if (map.isTileSolid(row, col)) {
                this.damage(this.getHealth());
            }
        }

        @Override
        public void draw(Graphics2D g, Camera camera) {
            if (!isAlive()) return;
            g.setColor(Color.PINK);
            g.fillRect((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        }
    }

    private class Missile extends com.mijuego.entities.Entities {
        private final Player tgt;
        private final double gravity = GS.DSC(0.18);

        public Missile(double x, double y, Player target) {
            super(x, y, GS.SC(8), GS.SC(8), 1);
            this.tgt = target;
            this.dy = GS.DSC(-3.2); // impulso inicial hacia arriba
            this.dx = 0;
        }

        @Override
        public void update() {
            // homing suave hacia la X del target
            if (tgt != null) {
                double tx = tgt.getX() + tgt.getWidth() / 2.0;
                double diff = tx - x;
                dx += diff * 0.02; // ajuste suave
                double maxDx = GS.DSC(4.0);
                if (dx > maxDx) dx = maxDx;
                if (dx < -maxDx) dx = -maxDx;
            }

            dy += gravity;
            x += dx;
            y += dy;

            // destruir si choca con tile sólido o sale del mapa
            int col = (int)((x + width/2) / Tile.SIZE);
            int row = (int)((y + height/2) / Tile.SIZE);
            if (row < 0 || row >= map.getRows() || col < 0 || col >= map.getCols() || map.isTileSolid(row, col)) {
                // explosion (simple): muere y en outer update se aplicará daño si intersecta player
                this.damage(this.getHealth());
            }
        }

        @Override
        public void draw(Graphics2D g, Camera camera) {
            if (!isAlive()) return;
            g.setColor(Color.ORANGE);
            g.fillOval((int)(x - camera.getX()), (int)(y - camera.getY()), width, height);
        }
    }

    // --- override checkPlayerCollision para caso particular si lo necesitás ---
    public void checkPlayerCollision(Player player) {
        // el boss hace daño por contacto si el jugador choca lateralmente
        if (!isAlive() || player == null || !player.isAlive()) return;

        if (player.getBounds().intersects(this.getBounds())) {
            // si el jugador está cayendo encima, lo tratamos en checkPlayerStomp()
            // si no, daño por contacto
            if (!(player.getDy() > 0 && (player.getY() + player.getHeight() <= this.getY() + GS.SC(12)))) {
                player.takeDamage(2);
                // empujar al jugador hacia afuera
                if (player.getX() < this.getX()) {
                    player.setX(this.getX() - player.getWidth());
                } else {
                    player.setX(this.getX() + this.getWidth());
                }
            }
        }
    }
    
    public void takeDamage(int damage) {
       health -= damage;
        if(health < 0) currentHealth = 0;
    }
}
