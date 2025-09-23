package com.mijuego.entities.items;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mijuego.core.Camera;
import com.mijuego.entities.Player;
import com.mijuego.core.GS;
import com.mijuego.utils.AudioManager;

public class Trampoline extends Item {
    
    private int bouncePower = GS.SC(-8); // fuerza del impulso hacia arriba
    
    public Trampoline(double x, double y,int width, int height) {
        super(x, y,width, height); 
    }
    
    @Override
    public void draw(Graphics2D g, Camera camera) {
        if (collected) return; // aunque acá no se "colecciona"
        
        int drawX = (int)x - camera.getX();
        int drawY = (int)y - camera.getY();

        // Dibujamos el trampolín como un rectángulo cian
        g.setColor(Color.CYAN);
        g.fillRect(drawX, drawY, width, height);
    }

    @Override
    public void update() {
        // En este caso, el trampolín no necesita lógica propia
    }

    @Override
    public void checkPlayerCollision(Player player) {
        if (collected) return;

        if (getBounds().intersects(player.getBounds())) {
            // Aplica el rebote
            player.setDy(bouncePower);
            AudioManager.playSpringJump();

            // Opcional: evitar que se pueda "coleccionar"
            // collected = true; // si quisieras que desaparezca después
        }
    }
}
