package com.mijuego.core;

import com.mijuego.utils.InputManager;

public class GameLoop implements Runnable {

    private Thread loopThread;
    private boolean running = false;

    private final int FPS = 60;
    private final double TIME_PER_FRAME = 1000000000.0 / FPS;

    private GamePanel panel;

    // 🔹 Estado actual del juego
    private GameState gameState = GameState.MENU;


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
            	if (InputManager.isUp()) {
                    gameState = GameState.PLAYING;
                }
                break;

            case PLAYING:

                // ESC → pausa
                if (InputManager.isEsc() && gameState == GameState.PLAYING) {
                    gameState = GameState.PAUSED;
                }
                break;

            case PAUSED:
                // ESC → volver a jugar
                if (InputManager.isEsc() && gameState == GameState.PAUSED) {
                    gameState = GameState.PLAYING;
                }

                // Botón de salir (se implementará en PauseMenu más tarde)
                // if (InputManager.isSomeExitKey()) {
                //     gameState = GameState.MENU; 
                // }
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
