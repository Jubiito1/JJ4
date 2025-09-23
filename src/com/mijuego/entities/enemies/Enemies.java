package com.mijuego.entities.enemies;

import java.awt.Graphics2D;

import com.mijuego.core.Camera;
import com.mijuego.entities.Entities;
import com.mijuego.map.TileMap;

/**
 * Clase base para todos los enemigos del juego.
 * Hereda de Entities y define comportamiento general.
 */
public abstract class Enemies extends Entities {

    protected TileMap map;        // mapa actual (para colisiones)
    protected double speed;       // velocidad de movimiento
    protected boolean facingRight; // dirección del enemigo
    protected boolean active;     // si está activo (puede usarse para spawnear/despawning)

    public Enemies(double x, double y, int width, int height, int health, TileMap map) {
        super(x, y, width, height, health);
        this.map = map;
        this.speed = 1.0;
        this.facingRight = true;
        this.active = true;
    }

    // Cada enemigo deberá implementar su propia lógica
    @Override
    public abstract void update();

    // Cada enemigo deberá dibujarse a sí mismo
    @Override
    public abstract void draw(Graphics2D g, Camera camera);

    // Métodos utilitarios comunes
    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }
}
