package net.scoobis.svctts.providers;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;
import net.scoobis.svctts.SvcTtsMod;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class FreeTtsProvider implements TtsProvider {
    public FreeTtsProvider() {}

    @Override
    public void init() {}

    @Override
    public short[] synthesizeAudio(String text, float pitch) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice("kevin16");

        String fileName = UUID.randomUUID().toString();

        voice.setPitch(pitch);
        voice.setAudioPlayer(new SingleFileAudioPlayer(fileName, AudioFileFormat.Type.WAVE));
        voice.allocate();
        voice.speak(text);
        voice.getAudioPlayer().close();
        voice.deallocate();

        byte[] rawBytes = new byte[0];

        try {
            File wavFile = new File(fileName + ".wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
            AudioFormat format = audioInputStream.getFormat();
            SvcTtsMod.LOGGER.info("format: {}", format);

            rawBytes = audioInputStream.readAllBytes();
            Files.delete(wavFile.toPath());
        } catch (UnsupportedAudioFileException | IOException e) {
            SvcTtsMod.LOGGER.error(e.getMessage());
        }

        short[] shorts = new short[rawBytes.length / 2];

        for (int i = 0; i < shorts.length; i++) {
            int low = rawBytes[2 * i] & 255;
            int high = rawBytes[2 * i + 1];
            shorts[i] = (short) ((high << 8) | low);
        }

        return SvcTtsMod.resampleAudio(shorts, 16000);
    }
}
