package com.mijuego.core;

import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.ResourceManager;
import com.mijuego.entities.Entities;
import com.mijuego.entities.enemies.Goomba;
import com.mijuego.entities.Player;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private int currentLevel = 1;
    private TileMap currentTileMap;
    private Tile[] tileset;
    private List<Entities> enemies; // Lista de enemigos del nivel
    private Player player;

    public LevelManager() {
        enemies = new ArrayList<>();
        initTileset();            // Inicializa los tipos de tiles
        loadLevel(currentLevel);  // Carga el primer nivel
    }

    // Inicializa los tiles básicos
    private void initTileset() {
        tileset = new Tile[6]; // ahora soportamos hasta el 5

        tileset[0] = new Tile(Tile.EMPTY);   // aire
        tileset[1] = new Tile(Tile.SOLID);   // bloque sólido
        tileset[2] = new Tile(Tile.GOOMBA);  // goomba (se reemplaza al cargar nivel)
        tileset[3] = new Tile(Tile.PLAYER);  // player (se reemplaza al cargar nivel)
        tileset[4] = new Tile(Tile.KILL);    // tile mortal
        tileset[5] = new Tile(Tile.WIN);     // tile win amarilla
    }

    // Carga un nivel por número
    public void loadLevel(int levelNumber) {
        try {
            enemies.clear(); // Limpiar enemigos del nivel anterior
            player = null;   // Resetear el player

            String path = "/assets/levels/level" + levelNumber + ".txt";
            InputStream is = ResourceManager.loadText(path);
            currentTileMap = new TileMap(tileset);
            currentTileMap.loadFromStream(is);

            // --- Crear entidades según el mapa ---
            for (int r = 0; r < currentTileMap.getRows(); r++) {
                for (int c = 0; c < currentTileMap.getCols(); c++) {
                    int tileId = currentTileMap.getTileId(r, c);

                    if (tileId == 2) { // Goomba
                        Goomba g = new Goomba(c * Tile.SIZE, r * Tile.SIZE, currentTileMap);
                        enemies.add(g);
                        currentTileMap.setTileId(r, c, 0); // reemplazar por aire
                    } else if (tileId == 3) { // Player
                        player = new Player(c * Tile.SIZE, r * Tile.SIZE, GS.SC(20), GS.SC(20), 100, currentTileMap);
                        currentTileMap.setTileId(r, c, 0); // reemplazar por aire
                    }
                }
            }

            currentLevel = levelNumber;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Avanzar al siguiente nivel
    public void nextLevel() {
        loadLevel(currentLevel + 1);
    }

    // 🔹 Comprueba si el player toca la tile WIN
    public boolean checkWin(Player player) {
        if (player == null || currentTileMap == null) return false;

        int tileSize = Tile.SIZE;
        int leftCol   = (int)player.getX() / tileSize;
        int rightCol  = (int)(player.getX() + player.getWidth() - 1) / tileSize;
        int topRow    = (int)player.getY() / tileSize;
        int bottomRow = (int)(player.getY() + player.getHeight() - 1) / tileSize;

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (currentTileMap.getTile(row, col).isWin()) {
                    return true;
                }
            }
        }
        return false;
    }

    // Getters
    public TileMap getCurrentTileMap() { return currentTileMap; }
    public Player getPlayer() { return player; }
    public List<Entities> getEnemies() { return enemies; }
    public int getCurrentLevel() { return currentLevel; }
}
