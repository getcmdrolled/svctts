package net.scoobis.svctts.providers;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.scoobis.svctts.SvcTtsMod;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

public class LibFliteProvider implements TtsProvider {
    private static LibFlite flite_instance;

    @Override
    public short[] synthesizeAudio(String text, float pitch) {
        byte[] rawBytes = new byte[0];
        try {
            Pointer voice = switch (SvcTtsMod.CONFIG.libflite_voice) {
                case KAL16 -> LibFliteKal.INSTANCE.register_cmu_us_kal16(null);
                case SLT -> LibFliteSlt.INSTANCE.register_cmu_us_slt(null);
                case AWB -> LibFliteAwb.INSTANCE.register_cmu_us_awb(null);
            };

            if (voice == Pointer.NULL) {
                SvcTtsMod.LOGGER.error("Unknown voice: {}", SvcTtsMod.CONFIG.libflite_voice);
                throw new IllegalArgumentException();
            }

            // scale based on config pitch
            Pointer features = voice.getPointer(Native.POINTER_SIZE); // i sure hope cst_voice is the same for every version because i have no idea how to do it otherwise
            float currentPitch = flite_instance.flite_get_param_float(features, "int_f0_target_mean", 100);
            flite_instance.flite_feat_set_float(features, "int_f0_target_mean", currentPitch * pitch / 100);

            String fileName = UUID.randomUUID().toString();
            flite_instance.flite_text_to_speech(text, voice, fileName + ".wav");

            try {
                File wavFile = new File(fileName + ".wav");
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);

                rawBytes = audioInputStream.readAllBytes();
                Files.delete(wavFile.toPath());
            } catch (UnsupportedAudioFileException | IOException e) {
                SvcTtsMod.LOGGER.error(e.getMessage());
            }
        } catch (UnsatisfiedLinkError | IllegalArgumentException e) {
            SvcTtsMod.LOGGER.error("Unable to load library for selected libflite voice! Please change config. {}", e.getMessage());

            // replace audio with error sfx
            Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(ResourceLocation.fromNamespaceAndPath(SvcTtsMod.MOD_ID, "sounds/flite_library_error.wav"));
            if (resource.isPresent()) {
                try (InputStream inputStream = resource.get().open()) {
                    rawBytes = inputStream.readAllBytes();
                } catch (IOException e2) {
                    SvcTtsMod.LOGGER.error("Somehow failed to play the error message? {}", e2.getMessage());
                }
            } else {
                SvcTtsMod.LOGGER.error("what the fuck why doesnt the resource exist");
            }
        }

        short[] shorts = new short[rawBytes.length / 2];

        for (int i = 0; i < shorts.length; i++) {
            int low = rawBytes[2 * i] & 255;
            int high = rawBytes[2 * i + 1];
            shorts[i] = (short) ((high << 8) | low);
        }

        return SvcTtsMod.resampleAudio(shorts, 16000);
    }

    @Override
    public void init() {
        flite_instance = LibFlite.INSTANCE;
        flite_instance.flite_init();
    }

    public interface LibFlite extends Library {
        LibFlite INSTANCE = Native.load("flite", LibFlite.class);

        void flite_init();

        void flite_feat_set_float(Pointer features, String name, float value);
        float flite_get_param_float(Pointer features, String name, float def);

        void flite_text_to_speech(String text, Pointer voice, String outtype);
    }

    public interface LibFliteKal extends Library {
        LibFliteKal INSTANCE = Native.load("flite_cmu_us_kal16", LibFliteKal.class);

        Pointer register_cmu_us_kal16(String voxdir);
    }

    public interface LibFliteSlt extends Library {
        LibFliteSlt INSTANCE = Native.load("flite_cmu_us_slt", LibFliteSlt.class);

        Pointer register_cmu_us_slt(String voxdir);
    }

    public interface LibFliteAwb extends Library {
        LibFliteAwb INSTANCE = Native.load("flite_cmu_us_awb", LibFliteAwb.class);

        Pointer register_cmu_us_awb(String voxdir);
    }
}
