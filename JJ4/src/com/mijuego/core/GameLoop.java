package com.mijuego.core;

import com.mijuego.entities.Entities;
import com.mijuego.entities.Player;
import com.mijuego.entities.enemies.Goomba;
import com.mijuego.utils.InputManager;
import com.mijuego.entities.items.Item;
import com.mijuego.entities.enemies.Jumper;

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
                break;

            case PLAYING:
                if (InputManager.isEsc()) gameState = GameState.PAUSED;

                Player player = panel.getLevelManager().getPlayer();
                LevelManager lm = panel.getLevelManager();

                // Comprobar si tocó la tile WIN
                if (player != null && lm.checkWin(player)) {
                    lm.nextLevel();
                    panel.getEntities().clear();
                    panel.getEntities().addAll(lm.getEnemies());
                    panel.getEntities().addAll(lm.getItems());
                    if (lm.getPlayer() != null) panel.getEntities().add(lm.getPlayer());
                    panel.getCamera().setMap(lm.getCurrentTileMap());
                    player = lm.getPlayer();
                }

                // Actualizar todas las entidades
                for (Entities e : panel.getEntities()) {
                    e.update();

                    if (player != null && e instanceof Item) {
                        ((Item)e).checkPlayerCollision(player);
                    }
                }

                // Eliminar items recolectados
                panel.getEntities().removeIf(e -> (e instanceof Item) && ((Item)e).isCollected());

                // Colisiones player ↔ enemigos
                if (player != null) {
                    for (Entities e : panel.getEntities()) {
                        if (e instanceof Goomba) ((Goomba)e).checkPlayerCollision(player);
                        if (e instanceof Jumper) ((Jumper)e).checkPlayerCollision(player);
                    }
                }
                
                
             // Eliminar items recolectados
                panel.getEntities().removeIf(e -> (e instanceof Item) && ((Item)e).isCollected());


                // Actualizar cámara
                if (player != null) panel.getCamera().follow(player);
                break;

            case PAUSED:
                if (InputManager.isEsc()) gameState = GameState.PLAYING;
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
