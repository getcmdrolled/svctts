package net.scoobis.svctts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;
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
    public EditBox messageField;
    public AbstractSliderButton pitchSlider;
    private float pitch = SvcTtsMod.lastPitch;

    @Override
    protected void init() {
        messageField = new EditBox(font, 10, height / 2 - 10, width - 20, 20, Component.literal("very real text that will definitely show up in game"));
        messageField.setMaxLength(128);
        setFocused(messageField);

        sendButton = Button.builder(Component.translatable("button.svctts.send"), this::send).bounds(width / 2 + 25, height / 2 + 10, 50, 20).build();
        pitchSlider = new AbstractSliderButton(width / 2 - 75, height / 2 + 10, 100, 20, Component.literal(String.valueOf(SvcTtsMod.lastPitch)), SvcTtsMod.lastPitch / 4) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.literal(String.valueOf(this.value * 4)));
            }

            @Override
            protected void applyValue() {
                this.value = (double) Math.round(this.value * 8) / 8;
                pitch = (float) this.value * 4;
            }
        };

        addRenderableWidget(messageField);
        addRenderableWidget(sendButton);
        addRenderableWidget(pitchSlider);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) send();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void send(Button buttonWidget) {
        send();
    }

    private void send() {
        String text = messageField.getValue();
        messageField.setValue("");
        Minecraft.getInstance().setScreen(null);
        SvcTtsMod.lastPitch = pitch;
        if (!text.isEmpty()) {
            SvcTtsMod.addToQueue(new TtsMessage(text, pitch));
        }
    }
}
