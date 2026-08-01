package net.scoobis.svctts;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.audio.AudioConverter;
import de.maxhenkel.voicechat.api.audiochannel.ClientAudioChannel;
import de.maxhenkel.voicechat.api.events.*;

import java.util.UUID;

public class SvcTtsVoicechatPlugin implements VoicechatPlugin {
    private ClientAudioChannel channel;

    public static AudioConverter audioConverter;

    @Override
    public String getPluginId() {
        return net.scoobis.svctts.SvcTtsMod.MOD_ID;
    }

    @Override
    public void initialize(VoicechatApi api) {
        net.scoobis.svctts.SvcTtsMod.LOGGER.info("Voice chat plugin initialized!");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(ClientVoicechatConnectionEvent.class, this::onClientConnect);
        registration.registerEvent(MergeClientSoundEvent.class, this::onClientMerge);
    }

    public void onClientConnect(ClientVoicechatConnectionEvent event) {
        VoicechatClientApi api = event.getVoicechat();
        channel = api.createStaticAudioChannel(UUID.randomUUID());
        audioConverter = api.getAudioConverter();
    }

    public void onClientMerge(MergeClientSoundEvent event) {
        if (!SvcTtsMod.QUEUE.isEmpty()) {
            short[] audio = SvcTtsMod.QUEUE.getFirst();
            SvcTtsMod.QUEUE.removeFirst();

            event.mergeAudio(audio);
            channel.play(audio);
        }
    }
}