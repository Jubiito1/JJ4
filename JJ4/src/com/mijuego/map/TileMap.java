package com.mijuego.map;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TileMap {
    private int rows, cols;         // Filas y columnas del nivel
    private int[][] map;            // Matriz de IDs
    private Tile[] tileset;         // Array de tiles disponibles

    public TileMap(Tile[] tileset) {
        this.tileset = tileset;
    }

    // Cargar nivel desde un InputStream (por ejemplo desde ResourceManager)
    public void loadFromStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        List<String> lines = new ArrayList<>();
        String line;
        while((line = br.readLine()) != null) {
            if(!line.trim().isEmpty()) {
                lines.add(line);
            }
        }
        br.close();

        rows = lines.size();
        cols = lines.get(0).length();
        map = new int[rows][cols];

        for(int r = 0; r < rows; r++) {
            String ln = lines.get(r);
            for(int c = 0; c < cols; c++) {
                char ch = ln.charAt(c);
                map[r][c] = Character.getNumericValue(ch);
            }
        }
    }

    // Dibujar el mapa completo
    public void draw(Graphics2D g) {
        int ts = Tile.SIZE;
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < cols; c++) {
                int id = map[r][c];
                if(id == 0) continue; // 0 = aire, no dibujamos
                Tile t = tileset[id];
                int x = c * ts;
                int y = r * ts;
                t.draw(g, x, y);
            }
        }
    }

    // Getters útiles
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTileId(int row, int col) { return map[row][col]; }
}
