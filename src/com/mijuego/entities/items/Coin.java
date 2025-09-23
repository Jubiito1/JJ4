package com.mijuego.entities.items;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mijuego.core.Camera;
import com.mijuego.entities.Player;

public class Coin extends Item {

    private boolean collected = false;
    private final int COIN_VALUE = 1;

    public Coin(double x, double y, int size) {
        super(x, y, size, size);
    }

    @Override
    public void update() {
        // la moneda no hace nada por sí sola
    }

    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (collected) return;

        g.setColor(Color.YELLOW);
        g.fillOval(
            (int)(x - camera.getX()),
            (int)(y - camera.getY()),
            width,
            height
        );
    }

    @Override
    public void checkPlayerCollision(Player player) {
        if (collected) return;

        if (this.getBounds().intersects(player.getBounds())) {
            player.addCoins(COIN_VALUE); // aumentar contador de monedas
            collect();
        }
    }
}
