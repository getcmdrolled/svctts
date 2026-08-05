package net.scoobis.svctts;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class SvcTtsClient implements ClientModInitializer {
    private static KeyMapping openTtsScreenKeybind;

    @Override
    public void onInitializeClient() {
        openTtsScreenKeybind = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.svctts.openscreen",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                new KeyMapping.Category(Identifier.fromNamespaceAndPath(SvcTtsMod.MOD_ID, "key.svctts.category"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(_ -> {
            while (openTtsScreenKeybind.consumeClick()) {
                Minecraft.getInstance().gui.setScreen(new SendTtsScreen(Component.literal("yo")));
            }
        });
    }
}