package com.mijuego.core;

public class GS {
    // Resolución base en la que diseñamos el juego
    public static final int BASE_WIDTH = 1920;
    public static final int BASE_HEIGHT = 1080;

    // Valor de escala (ej. 1 = 1920x1080, 2 = 960x540, 3 = 640x360)
    public static int ScaleValue = 1;

    // Método centralizado de escalado
    public static int SC(int in) {
        return in / ScaleValue;
    }

    // Resolución interna que se usará para el canvas
    public static int getVirtualWidth() {
        return SC(BASE_WIDTH);
    }

    public static int getVirtualHeight() {
        return SC(BASE_HEIGHT);
    }
}
