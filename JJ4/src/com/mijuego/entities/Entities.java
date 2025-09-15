package com.mijuego.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public abstract class Entities {
    protected double x, y;           // posición
    protected int width, height;     // tamaño
    protected double dx, dy;         // velocidad en x e y
    protected int health;  // estado vivo o muerto

    public Entities(double x, double y, int width, int height, int health) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.health = health;
    }

    // 🔹 Actualizar la lógica de la entidad (se implementa en subclases)
    public abstract void update();

    // 🔹 Dibujar en pantalla
    public abstract void draw(Graphics2D g);

    // 🔹 Rectángulo para colisiones
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
    
    public void damage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    public void heal(int amount) {
        health += amount;
    }

    public boolean isAlive() {
        return health > 0;
    }

    // Getters y setters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getHealth() { return health; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }
    public void setHealth(int health) { this.health = Math.max(0, health); }
}
