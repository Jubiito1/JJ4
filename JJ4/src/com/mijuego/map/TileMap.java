package com.mijuego.map;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.mijuego.core.Camera;

public class TileMap {
    private int rows, cols;
    private int[][] map;    // IDs de tiles en cada celda
    private Tile[] tileset; // Definiciones de tiles

    public TileMap(Tile[] tileset) {
        this.tileset = tileset;
    }

 // Cargar nivel desde un InputStream (archivo .txt)
    public void loadFromStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line.trim());
            }
        }
        br.close();

        rows = lines.size();
        cols = lines.get(0).trim().split("\\s+").length; // cantidad de columnas según espacios
        map = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            String[] tokens = lines.get(r).trim().split("\\s+"); // divide por espacio
            for (int c = 0; c < cols; c++) {
                map[r][c] = Integer.parseInt(tokens[c]); // convierte "10" en 10, "11" en 11...
            }
        }
    }

    // Dibujar tiles en pantalla
    public void draw(Graphics2D g, Camera camera) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = map[r][c];
                if (id == Tile.EMPTY || id == Tile.GOOMBA || id == Tile.PLAYER) continue;

                Tile t = tileset[id];
                t.draw(g, c, r, camera);
            }
        }
    }

    // Consultar si un tile es sólido
    public boolean isTileSolid(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true; // fuera del mapa = sólido
        }
        int id = map[row][col];
        return tileset[id].isSolid();
    }

    // 🔹 Nuevo: devuelve el Tile en esa posición
    public Tile getTile(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return new Tile(Tile.EMPTY);
        }
        int id = map[row][col];
        return tileset[id];
    }

    public void setTileId(int row, int col, int id) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) return;
        map[row][col] = id;
    }

    // Getters
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTileId(int row, int col) { return map[row][col]; }
}
