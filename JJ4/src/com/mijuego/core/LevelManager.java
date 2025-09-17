package com.mijuego.core;

import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.ResourceManager;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class LevelManager {

    private int currentLevel = 3;
    private TileMap currentTileMap;
    private Tile[] tileset;

    public LevelManager() {
        initTileset();       // Inicializa los tipos de tiles
        loadLevel(currentLevel); // Carga el primer nivel
    }

    // Inicializa los tiles básicos
    private void initTileset() {
        tileset = new Tile[2];

        // 0 = aire
        tileset[0] = new Tile(0, null, false);

        // 1 = bloque sólido (temporal, rojo)
        BufferedImage blockSprite = new BufferedImage(Tile.BASE_SIZE, Tile.BASE_SIZE, BufferedImage.TYPE_INT_RGB);
        blockSprite.getGraphics().setColor(java.awt.Color.RED);
        blockSprite.getGraphics().fillRect(0, 0, Tile.BASE_SIZE, Tile.BASE_SIZE);
        tileset[1] = new Tile(1, blockSprite, true);
    }

    // Carga un nivel por número
    public void loadLevel(int levelNumber) {
        try {
            String path = "/assets/levels/level" + levelNumber + ".txt";
            InputStream is = ResourceManager.loadText(path);
            currentTileMap = new TileMap(tileset);
            currentTileMap.loadFromStream(is);
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

    public int getCurrentLevel() {
        return currentLevel;
    }
}
