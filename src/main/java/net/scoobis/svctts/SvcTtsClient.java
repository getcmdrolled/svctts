package net.scoobis.svctts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class SvcTtsClient implements ClientModInitializer {

    public static final KeyBinding.Category TTS_CATEGORY = new KeyBinding.Category(
            Identifier.of("svctts", "category")
    );

    private static KeyBinding openTtsScreenKeybind;

    @Override
    public void onInitializeClient() {
        openTtsScreenKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.svctts.openscreen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                TTS_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openTtsScreenKeybind.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new SendTtsScreen(Text.literal("yo")));
            }
        });
    }
}
