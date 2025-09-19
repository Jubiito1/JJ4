package com.mijuego.core;

import com.mijuego.utils.InputManager;
import com.mijuego.entities.Entities;
import com.mijuego.entities.Player;
import com.mijuego.entities.enemies.Goomba;


public class GameLoop implements Runnable {

    private Thread loopThread;
    private boolean running = false;

    private final int FPS = 60;
    private final double TIME_PER_FRAME = 1000000000.0 / FPS;

    private GamePanel panel;
    private GameState gameState = GameState.PLAYING;

    public GameLoop(GamePanel panel) {
        this.panel = panel;
    }

    public void start() {
        if (running) return;
        running = true;
        loopThread = new Thread(this);
        loopThread.start();
    }

    public void stop() {
        running = false;
        try {
            loopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double delta = 0;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / TIME_PER_FRAME;
            lastTime = now;

            while (delta >= 1) {
                update();        
                panel.repaint(); 
                delta--;
                frames++;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
    }

    private void update() {
        switch (gameState) {
            case MENU:
                // Lógica futura del menú
                break;

            case PLAYING:
                if (InputManager.isEsc()) {
                    gameState = GameState.PAUSED;
                }

                // 🔹 Actualizar todas las entidades
                for (Entities e : panel.getEntities()) {
                    e.update();
                }

                // Chequear colisiones jugador ↔ enemigos
                Player player = null;
                for (Entities e : panel.getEntities()) {
                    if (e instanceof Player) {
                        player = (Player) e;
                        break;
                    }
                }
                if (player != null) {
                    for (Entities e : panel.getEntities()) {
                        if (e instanceof Goomba) {
                            ((Goomba) e).checkPlayerCollision(player);
                        }
                    }
                }

                // Actualizar cámara
                if (player != null) {
                    panel.getCamera().follow(player);
                }
                break;

            case PAUSED:
                if (InputManager.isEsc()) {
                    gameState = GameState.PLAYING;
                }
                break;
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }
}
