package com.mijuego.core;

import javax.swing.JFrame;
import com.mijuego.entities.Player;

public class GameMain {
    public static void main(String[] args) {
        // Crear el panel
        GamePanel panel = new GamePanel();

        // 🔹 Crear el player y agregarlo al panel
        Player player = new Player(GS.SC(100), GS.SC(100), GS.SC(20), GS.SC(20), 100, panel.getLevelManager().getCurrentTileMap());
        panel.addEntity(player);

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
