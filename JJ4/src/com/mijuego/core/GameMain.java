package com.mijuego.core;

import javax.swing.JFrame;

public class GameMain {
    public static void main(String[] args) {
        JFrame window = new JFrame("JJ4");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);

        GamePanel panel = new GamePanel();
        window.add(panel);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Iniciar loop
        GameLoop gameLoop = new GameLoop(panel);
        panel.setGameLoop(gameLoop);
        gameLoop.start();

    }
}
