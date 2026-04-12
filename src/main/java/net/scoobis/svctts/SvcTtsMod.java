package net.scoobis.svctts;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.scoobis.svctts.providers.TtsProvider;
import net.scoobis.svctts.providers.FreeTtsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class SvcTtsMod implements ModInitializer {

	public static final String MOD_ID = "svctts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfig CONFIG;
    public static ArrayList<short[]> QUEUE = new ArrayList<>();
    public static TtsProvider TTSPROVIDER;

    private static ModConfig.provider lastProvider;

    @Override
	public void onInitialize() {
        LOGGER.info("SVC TTS initialized!");
        AutoConfig.register(ModConfig.class, Toml4jConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();
        updateFromConfig();
	}
    
    public static void updateFromConfig() {
        switch (CONFIG.providerOption) {
            case ModConfig.provider.FREETTS -> TTSPROVIDER = new FreeTtsProvider();
            default -> TTSPROVIDER = null;
        }

        if (TTSPROVIDER != null) {
            TTSPROVIDER.init();
        }

        lastProvider = CONFIG.providerOption;
    }

    public static void addToQueue(String text) {
        if (!lastProvider.equals(CONFIG.providerOption)) updateFromConfig();
        short[] audio = TTSPROVIDER.synthesizeAudio(text);
        int separator = 960;
        int length = audio.length / separator - 1;
        if (audio.length == 0) return;

        for (int i = 0; i < length; i++) {
            short[] newAudio;
            int offset = separator * i;
            if (i < length - 1) {
                newAudio = Arrays.copyOfRange(audio, offset, offset + separator);
            } else {
                newAudio = Arrays.copyOfRange(audio, offset, audio.length - 1);
            }

            QUEUE.add(newAudio);
        }
    }

    public static short[] resampleAudio(short[] input, int srcRate) {
        double ratio = (double) 48000 / srcRate;
        int newLength = (int) Math.round(input.length * ratio);
        short[] output = new short[newLength];

        for (int i = 0; i < newLength; i++) {
            double srcIndex = i / ratio;
            int indexInt = (int) Math.floor(srcIndex);
            double fraction = srcIndex - indexInt;

            int srcPos = Math.min(indexInt, input.length);

            short sample1 = input[indexInt];
            short sample2 = input[srcPos];

            output[i] = (short) ((1 - fraction) * sample1 + fraction * sample2);
        }

        return output;
    }
}
