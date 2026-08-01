package net.scoobis.svctts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SendTtsScreen extends Screen {
    public SendTtsScreen(Component title) {
        super(title);
    }

    public Button sendButton;
    public Button exitButton;
    public EditBox messageField;

    @Override
    protected void init() {
        messageField = new EditBox(font, 10, height / 2 - 10, width - 20, 20, Component.literal("very real text that will definitely show up in game"));
        messageField.setMaxLength(128);
        setFocused(messageField);

        sendButton = Button.builder(Component.translatable("button.svctts.send"), this::send).bounds(width / 2 - 50, height / 2 + 10, 100, 20).build();
        exitButton = Button.builder(Component.literal("X"), button -> Minecraft.getInstance().setScreen(null)).bounds(width - 30, 10, 20, 20).build();

        addRenderableWidget(messageField);
        addRenderableWidget(sendButton);
        addRenderableWidget(exitButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (messageField.isFocused() && keyCode == GLFW.GLFW_KEY_ENTER) send();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void send(Button buttonWidget) {
        String text = messageField.getValue();
        messageField.setValue("");
        Minecraft.getInstance().setScreen(null);
        SvcTtsMod.addToQueue(text);
    }

    private void send() {
        String text = messageField.getValue();
        messageField.setValue("");
        Minecraft.getInstance().setScreen(null);
        if (!text.isEmpty()) {
            SvcTtsMod.addToQueue(text);
        }
    }
}
