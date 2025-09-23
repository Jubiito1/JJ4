package com.mijuego.core;

public class GS {
    // Resolución base en la que diseñamos el juego
    public static final int BASE_WIDTH = 640;
    public static final int BASE_HEIGHT = 360;

    private static int ScaleValue = 2;
    private static double DoubleScaleValue = ScaleValue;

    // Método centralizado de escalado
    public static int SC(int in) {
        return in * ScaleValue;
    }
    
    public static double DSC(double in) {
        return in * DoubleScaleValue;
    }

    // Resolución interna que se usará para el canvas
    public static int getVirtualWidth() {
        return SC(BASE_WIDTH);
    }

    public static int getVirtualHeight() {
        return SC(BASE_HEIGHT);
    }
    
    public static void setScaleValue(int nuevoValor) {
    	ScaleValue = nuevoValor;
    }
    
    public static int getScaleValue() {
    	return ScaleValue;
    }
    
    
}
