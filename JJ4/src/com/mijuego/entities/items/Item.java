package com.mijuego.entities.items;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.mijuego.entities.Player;
import com.mijuego.entities.Entities;
import com.mijuego.core.Camera;

public abstract class Item extends Entities {
	protected boolean collected = false;

    public Item(double x, double y, int width, int height) {
        super(x, y, width, height, 1); // health no se usa en items
    }
    
    public boolean isCollected() {
        return collected;
    }

    protected void collect() {
        collected = true;
    }
    
    // dibujar
    @Override
    public abstract void draw(Graphics2D g, Camera camera);

    // actualizar
    @Override
    public abstract void update();

    // interacción con player
    public abstract void checkPlayerCollision(Player player);

    // Rectángulo para colisiones
    @Override
    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }
}
