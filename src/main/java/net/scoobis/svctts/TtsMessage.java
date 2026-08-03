package net.scoobis.svctts;

public class TtsMessage {
    public String text;
    public float pitch;

    public TtsMessage(String text, float pitch) {
        this.text = text;
        this.pitch = pitch;
    }
}
