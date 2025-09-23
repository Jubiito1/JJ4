package com.mijuego.utils;

import javax.sound.sampled.Clip;

public class AudioManager {
    private static Clip music;
    private static Clip coin;
    private static Clip goombastomp;
    private static Clip hurt;
    private static Clip jump;
    private static Clip lose;
    private static Clip springjump;
    private static Clip victory;

    // Inicializa todos los sonidos usando ResourceManager para portabilidad
    public static void init() {
        music = loadClip("/assets/sounds/music.wav");
        coin = loadClip("/assets/sounds/coin.wav");
        goombastomp = loadClip("/assets/sounds/goombastomp.wav");
        hurt = loadClip("/assets/sounds/hurt.wav");
        jump = loadClip("/assets/sounds/jump.wav");
        lose = loadClip("/assets/sounds/lose.wav");
        springjump = loadClip("/assets/sounds/springjump.wav");
        victory = loadClip("/assets/sounds/victory.wav");

        // Música de fondo en loop
        if (music != null) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Ahora usamos ResourceManager para cargar sonidos
    private static Clip loadClip(String path) {
        return ResourceManager.loadSound(path);
    }

    // Métodos para reproducir cada sonido
    public static void playCoin() { play(coin); }
    public static void playGoombaStomp() { play(goombastomp); }
    public static void playHurt() { play(hurt); }
    public static void playJump() { play(jump); }
    public static void playLose() { play(lose); stopMusic(); }
    public static void playSpringJump() { play(springjump); }
    public static void playVictory() { play(victory); stopMusic(); }

    // Control de música
    public static void stopMusic() {
        if (music != null && music.isRunning()) {
            music.stop();
        }
    }

    public static void startMusic() {
        if (music != null) {
            music.setFramePosition(0);
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Método genérico para reproducir cualquier efecto
    private static void play(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
