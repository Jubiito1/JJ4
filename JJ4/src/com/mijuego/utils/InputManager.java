package com.mijuego.utils;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputManager implements KeyListener {

    // Array que guarda el estado de cada tecla
    private static boolean[] keys = new boolean[256];
    private static boolean escProcessed = false;


    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key >= 0 && key < keys.length) {
            keys[key] = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key >= 0 && key < keys.length) {
            keys[key] = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // No lo usamos, pero es obligatorio por KeyListener
    }

    // Métodos para consultar teclas específicas
    public static boolean isUp() {
        return keys[KeyEvent.VK_UP];
    }

    public static boolean isDown() {
        return keys[KeyEvent.VK_DOWN];
    }

    public static boolean isLeft() {
        return keys[KeyEvent.VK_LEFT];
    }

    public static boolean isRight() {
        return keys[KeyEvent.VK_RIGHT];
    }

    public static boolean isEsc() {
        return keys[KeyEvent.VK_ESCAPE];
    }
    
    public static boolean isEscJustPressed() {
        if (escPressed && !escProcessed) {
            escProcessed = true; // marcamos que ya la usamos
            return true;
        }
        return false;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            escPressed = false;
            escProcessed = false; // importante
        }
        // otras teclas...
    }


    
    
}
