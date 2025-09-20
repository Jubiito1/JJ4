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
        tileset = new Tile[5]; // ahora soportamos hasta el 4

        // 0 = aire
        tileset[0] = new Tile(Tile.EMPTY);

        // 1 = bloque sólido
        tileset[1] = new Tile(Tile.SOLID);

        // 2 = goomba (no se dibuja como tile, se reemplaza al cargar nivel)
        tileset[2] = new Tile(Tile.GOOMBA);

        // 3 = player (no se dibuja como tile, se reemplaza al cargar nivel)
        tileset[3] = new Tile(Tile.PLAYER);

        // 4 = tile mortal
        tileset[4] = new Tile(Tile.KILL);
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

                    if (tileId == 2) { // 2 = Goomba
                        Goomba g = new Goomba(c * Tile.SIZE, r * Tile.SIZE, currentTileMap);
                        enemies.add(g);
                        // Reemplazar 2 por aire en el mapa para no dibujarlo
                        currentTileMap.setTileId(r, c, 0);
                    } 
                    else if (tileId == 3) { // 3 = Player
                        // Usamos el constructor correcto: (x, y, width, height, health, map)
                        player = new Player(c * Tile.SIZE, r * Tile.SIZE, GS.SC(20), GS.SC(20), 100, currentTileMap);
                        // Reemplazar 3 por aire en el mapa para no dibujarlo
                        currentTileMap.setTileId(r, c, 0);
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

    // Devuelve el TileMap actual para que GamePanel lo dibuje
    public TileMap getCurrentTileMap() {
        return currentTileMap;
    }

    // Devuelve al player cargado desde el mapa
    public Player getPlayer() {
        return player;
    }

    // Lista de enemigos del nivel
    public List<Entities> getEnemies() {
        return enemies;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }
}
