package com.mijuego.core;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import com.mijuego.core.LevelManager;
import com.mijuego.entities.Entities;
import com.mijuego.entities.Player;
import com.mijuego.ui.HUD;
import com.mijuego.utils.ResourceManager;


public class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private BufferedImage background;
    private BufferedImage canvas;
    private Graphics2D g2d;
    private LevelManager levelManager;
    private Camera camera;
    private javax.swing.JButton restartButton;
    private javax.swing.JLabel victoryLabel;
    int btnW = GS.SC(380);
    int btnH = GS.SC(120);

    private List<Entities> entities;
    private HUD hud;

    public GamePanel() {
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        background = ResourceManager.loadImage("/assets/sprites/cielo.png");
       // Configurar fuente del botón
        

        // Inicializar nivel/entidades
        levelManager = new LevelManager();
        entities = new ArrayList<>();

        // Crear cámara usando el TileMap cargado
        camera = new Camera(GS.getVirtualWidth(), GS.getVirtualHeight(), levelManager.getCurrentTileMap());

        // Agregar enemigos
        entities.addAll(levelManager.getEnemies());

        // Agregar items
        entities.addAll(levelManager.getItems());

        // Agregar player
        Player p = levelManager.getPlayer();
        if (p != null) entities.add(p);
        // Crear HUD
        hud = new HUD(levelManager);
        
        // Botón de reinicio
        
        restartButton = new javax.swing.JButton("REINICIAR");
        restartButton.setFocusable(false);
        restartButton.setBounds(1, 1, GS.SC(380), GS.SC(120)); // posición inicial (que luego se reposiciona en paintComponent), y tamaño del botón
        restartButton.addActionListener(e -> resetGame());
        restartButton.setVisible(false); // inicialmente oculto
        this.setLayout(null); // posiciones absolutas
        this.add(restartButton);
        restartButton.setBackground(Color.BLACK);
        restartButton.setForeground(Color.WHITE);
        
        int fontSize = GS.SC(40); // tamaño de letra
        try {
            InputStream is = getClass().getResourceAsStream("/assets/fonts/Pixeled.ttf");
            java.awt.Font pixelFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is).deriveFont((float) fontSize);                
            restartButton.setFont(pixelFont);
        } catch (Exception ex) {
            ex.printStackTrace();
            restartButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));
        }
        
        
        
        
        victoryLabel = new javax.swing.JLabel("¡VICTORIA!");
        victoryLabel.setOpaque(true);
        victoryLabel.setBackground(Color.BLACK);
        victoryLabel.setForeground(Color.WHITE);
        victoryLabel.setVisible(false); // inicialmente oculto
        victoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        
        try {
            InputStream is = getClass().getResourceAsStream("/assets/fonts/Pixeled.ttf");
            java.awt.Font pixelFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is)
                                                  .deriveFont((float) fontSize);
            victoryLabel.setFont(pixelFont);
        } catch (Exception ex) {
            ex.printStackTrace();
            victoryLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, fontSize));
        }

        victoryLabel.setBounds(0, getHeight() / 4, getWidth(), GS.SC(100));
        this.add(victoryLabel);
        


        this.setLayout(null);
        this.add(restartButton);
        
        
        
		

        // Crear canvas virtual para el reescalado
        canvas = new BufferedImage(
            GS.getVirtualWidth(),
            GS.getVirtualHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        g2d = canvas.createGraphics();

        setFocusable(true);
        requestFocus();
        addKeyListener(new com.mijuego.utils.InputManager());
    }

    public void addEntity(Entities e) {
        entities.add(e);
    }

    public List<Entities> getEntities() {
        return entities;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public Camera getCamera() {
        return camera;
    }
    
    //metodo para reiniciar el juego
    public void resetGame() {
        // Limpiar entidades actuales
        entities.clear();
        

        // Recargar el nivel inicial (nivel 1)
        levelManager.loadLevel(1);

        // Reagregar enemigos
        entities.addAll(levelManager.getEnemies());

        // Reagregar items
        entities.addAll(levelManager.getItems());

        // Reagregar player
        Player p = levelManager.getPlayer();
        if (p != null) entities.add(p);

        // Reiniciar cámara
        camera = new Camera(GS.getVirtualWidth(), GS.getVirtualHeight(), levelManager.getCurrentTileMap());

        // Actualizar HUD si es necesario
        hud = new HUD(levelManager);

        // Forzar repintado
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // --- Fondo ---
        if (background != null) {
            double parallaxFactor = 0.5;
            int bgX = (int)(-camera.getX() * parallaxFactor);
            int bgY = (int)(-camera.getY() * parallaxFactor);

            for (int x = bgX; x < GS.getVirtualWidth(); x += background.getWidth()) {
                for (int y = bgY; y < GS.getVirtualHeight(); y += background.getHeight()) {
                	g2d.drawImage(background, x, y, GS.SC(background.getWidth()), GS.SC(background.getHeight()), null);
                }
            }
        } else {
            g2d.setColor(new Color(135, 206, 235));
            g2d.fillRect(0, 0, GS.getVirtualWidth(), GS.getVirtualHeight());
        }

        // --- Mapa ---
        if (levelManager.getCurrentTileMap() != null) {
            levelManager.getCurrentTileMap().draw(g2d, camera);
        }

        // --- Entidades ---
        for (Entities e : entities) {
            e.draw(g2d, camera);
        }

        // --- HUD ---
        if (hud != null) hud.draw(g2d);

        g.drawImage(canvas, 0, 0, getWidth(), getHeight(), null);

        // --- Mostrar botón si el jugador murió ---
        Player player = levelManager.getPlayer();
        if (player != null && !player.isAlive()) {
        	restartButton.setVisible(true);
        	restartButton.setSize(btnW, btnH);
        	restartButton.setLocation(
        	    (getWidth() - btnW) / 2,
        	    (getHeight() - btnH) / 2
            );
        } else {
            restartButton.setVisible(false);
            victoryLabel.setVisible(false);
        }
        if (LevelManager.currentLevel==4) {
			restartButton.setVisible(true);
			restartButton.setSize(btnW, btnH);
			restartButton.setLocation((getWidth() - btnW) / 2, (getHeight() - btnH) / 2);
			
			victoryLabel.setVisible(true);
		    victoryLabel.setSize(btnW, btnH); // mismo tamaño que el botón
		    victoryLabel.setLocation(
		        (getWidth() - btnW) / 2,
		        (getHeight() - btnH) / 2 - GS.SC(140)
			    );
			
			
		}
    }

}