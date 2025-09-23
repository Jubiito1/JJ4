package com.mijuego.core;

import javax.swing.JFrame;

public class GameMain {
    public static void main(String[] args) {
        // Crear el panel
        GamePanel panel = new GamePanel();
        
        // Configurar la ventana
        JFrame window = new JFrame("JJ4");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.add(panel);
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        // Iniciar loop
        GameLoop loop = new GameLoop(panel);
        loop.start();
    }
}
