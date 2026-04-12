package net.scoobis.svctts.providers;

public interface TtsProvider {
    short[] synthesizeAudio(String text);
    void init();
}
