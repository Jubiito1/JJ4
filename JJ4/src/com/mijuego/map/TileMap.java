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

    public void loadFromStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        br.close();

        rows = lines.size();
        cols = lines.get(0).length();
        map = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            String ln = lines.get(r);
            for (int c = 0; c < cols; c++) {
                char ch = ln.charAt(c);
                map[r][c] = Character.getNumericValue(ch);
            }
        }
    }

    public void draw(Graphics2D g, Camera camera) {
        int ts = Tile.SIZE;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int id = map[r][c];
                if (id == 0) continue;

                Tile t = tileset[id];
                int x = c * ts - camera.getX();
                int y = r * ts - camera.getY();
                t.draw(g, x, y);
            }
        }
    }

    // ✅ Nuevo: preguntar si un tile es sólido
    public boolean isTileSolid(int row, int col) {
        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return true; // fuera del mapa = sólido (pared invisible)
        }
        int id = map[row][col];
        return tileset[id].isSolid();
    }

    // Getters
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTileId(int row, int col) { return map[row][col]; }
}
