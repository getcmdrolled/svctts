package net.scoobis.svctts;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

public class SvcTtsClient implements ClientModInitializer {
    private static KeyMapping openTtsScreenKeybind;

    @Override
    public void onInitializeClient() {
        openTtsScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.svctts.openscreen",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                new KeyMapping.Category(ResourceLocation.fromNamespaceAndPath(SvcTtsMod.MOD_ID, "key.svctts.category"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openTtsScreenKeybind.consumeClick()) {
                Minecraft.getInstance().setScreen(new SendTtsScreen(Component.literal("yo")));
            }
        });
    }
}
