package net.scoobis.svctts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SendTtsScreen extends Screen {
    public SendTtsScreen(Text title) {
        super(title);
    }

    public ButtonWidget sendButton;
    public ButtonWidget exitButton;
    public TextFieldWidget messageField;

    @Override
    protected void init() {
        messageField = new TextFieldWidget(textRenderer, 10, height / 2 - 10, width - 20, 20, Text.literal("very real text that will definitely show up in game"));
        messageField.setMaxLength(128);
        setFocused(messageField);

        sendButton = ButtonWidget.builder(Text.translatable("button.svctts.send"), this::send).dimensions(width / 2 - 50, height / 2 + 10, 100, 20).build();
        exitButton = ButtonWidget.builder(Text.literal("X"), button -> MinecraftClient.getInstance().setScreen(null)).dimensions(width - 30, 10, 20, 20).build();

        addDrawableChild(messageField);
        addDrawableChild(sendButton);
        addDrawableChild(exitButton);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (messageField.isFocused() && input.getKeycode() == GLFW.GLFW_KEY_ENTER) send();
        return super.keyPressed(input);
    }

    private void send(ButtonWidget buttonWidget) {
        String text = messageField.getText();
        messageField.setText("");
        MinecraftClient.getInstance().setScreen(null);
        SvcTtsMod.addToQueue(text);
    }

    private void send() {
        String text = messageField.getText();
        messageField.setText("");
        MinecraftClient.getInstance().setScreen(null);
        SvcTtsMod.addToQueue(text);
    }
}
