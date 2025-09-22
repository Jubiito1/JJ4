package com.mijuego.core;

import com.mijuego.map.Tile;
import com.mijuego.map.TileMap;
import com.mijuego.utils.ResourceManager;
import com.mijuego.entities.Entities;
import com.mijuego.entities.enemies.Goomba;
import com.mijuego.entities.Player;
import com.mijuego.entities.items.Coin;
import com.mijuego.entities.enemies.Jumper;
import com.mijuego.entities.items.Trampoline;
import com.mijuego.entities.enemies.Shooter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LevelManager {

    private int currentLevel = 1;
    private TileMap currentTileMap;
    private Tile[] tileset;
    private List<Entities> enemies;
    private List<Entities> items; // nueva lista de items
    private Player player;

    public LevelManager() {
        enemies = new ArrayList<>();
        items = new ArrayList<>();
        initTileset();
        loadLevel(currentLevel);
    }

    private void initTileset() {
        tileset = new Tile[10];

        tileset[0] = new Tile(Tile.EMPTY);
        tileset[1] = new Tile(Tile.SOLID);
        tileset[2] = new Tile(Tile.GOOMBA);
        tileset[3] = new Tile(Tile.PLAYER);
        tileset[4] = new Tile(Tile.KILL);
        tileset[5] = new Tile(Tile.WIN); // futuro uso, si se quiere
        tileset[6] = new Tile(Tile.COIN);
        tileset[7] = new Tile(Tile.JUMPER);
        tileset[8] = new Tile(Tile.TRAMPOLINE);
        tileset[9] = new Tile(Tile.SHOOTER);
    }

    public void loadLevel(int levelNumber) {
        try {
            enemies.clear();
            items.clear();
            player = null;

            String path = "/assets/levels/level" + levelNumber + ".txt";
            InputStream is = ResourceManager.loadText(path);
            currentTileMap = new TileMap(tileset);
            currentTileMap.loadFromStream(is);

            for (int r = 0; r < currentTileMap.getRows(); r++) {
                for (int c = 0; c < currentTileMap.getCols(); c++) {
                    int tileId = currentTileMap.getTileId(r, c);

                    if (tileId == 2) {
                        Goomba g = new Goomba(c * Tile.SIZE, r * Tile.SIZE, currentTileMap);
                        enemies.add(g);
                        currentTileMap.setTileId(r, c, 0);
                    } 
                    else if (tileId == 3) {
                        player = new Player(c * Tile.SIZE, r * Tile.SIZE, GS.SC(20), GS.SC(20), 100, currentTileMap);
                        currentTileMap.setTileId(r, c, 0);
                    }
                    else if (tileId == 6) {
                        Coin coin = new Coin(c * Tile.SIZE, r * Tile.SIZE, Tile.SIZE);
                        items.add(coin);
                        currentTileMap.setTileId(r, c, 0);
                    }
                    if (tileId == 7) {
                        Jumper j = new Jumper(c * Tile.SIZE, r * Tile.SIZE, currentTileMap, player);
                        enemies.add(j);
                        currentTileMap.setTileId(r, c, 0);
                    } 
                    else if (tileId == 8) { // TRAMPOLINE
                        int trampWidth = Tile.SIZE;
                        int trampHeight = Tile.SIZE / 2;

                        // posición ajustada: mismo X, pero Y desplazado hacia abajo
                        int trampX = c * Tile.SIZE;
                        int trampY = r * Tile.SIZE + (Tile.SIZE - trampHeight);

                        Trampoline tramp = new Trampoline(trampX, trampY, trampWidth, trampHeight);
                        items.add(tramp);
                        currentTileMap.setTileId(r, c, 0); // lo borramos del mapa
                    }
                    else if (tileId == 9) { // Shooter
                        Shooter shooter = new Shooter(c * Tile.SIZE, r * Tile.SIZE, currentTileMap, player);
                        enemies.add(shooter);
                        currentTileMap.setTileId(r, c, 0); // lo borramos del mapa
                    }
                }
            }

            currentLevel = levelNumber;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void nextLevel() {
        loadLevel(currentLevel + 1);
    }

    public TileMap getCurrentTileMap() {
        return currentTileMap;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Entities> getEnemies() {
        return enemies;
    }

    public List<Entities> getItems() {
        return items;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    // comprobación de tile win (amarillo)
    public boolean checkWin(Player player) {
        int tileSize = Tile.SIZE;
        int leftCol = (int)player.getX() / tileSize;
        int rightCol = (int)(player.getX() + player.getWidth() - 1) / tileSize;
        int topRow = (int)player.getY() / tileSize;
        int bottomRow = (int)(player.getY() + player.getHeight() - 1) / tileSize;

        for (int r = topRow; r <= bottomRow; r++) {
            for (int c = leftCol; c <= rightCol; c++) {
                if (currentTileMap.getTile(r, c).getType() == Tile.WIN) {
                    return true;
                }
            }
        }
        return false;
    }
}
