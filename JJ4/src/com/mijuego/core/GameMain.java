package com.mijuego.core;

import javax.swing.JFrame;

public class GameMain {
    public static void main(String[] args) {
        JFrame window = new JFrame("jj4");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true);              // sin bordes
        window.setResizable(false);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH); // pantalla completa

        GamePanel panel = new GamePanel();
        window.add(panel);

        window.setVisible(true);
    }
}
