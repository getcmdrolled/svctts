package net.scoobis.svctts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class SendTtsScreen extends Screen {
    public SendTtsScreen(Component title) {
        super(title);
    }

    public Button sendButton;
    public EditBox messageField;
    public TtsPitchSliderButton pitchSlider;
    public HistoryList historyList;
    private float pitch = (float) SvcTtsMod.CONFIG.pitch / 100;

    @Override
    protected void init() {
        messageField = new EditBox(font, 10, height - 80, width - 20, 20, Component.literal("very real text that will definitely show up in game"));
        messageField.setMaxLength(128);
        setFocused(messageField);

        sendButton = Button.builder(Component.translatable("button.svctts.send"), this::send).bounds(width / 2 + 30, height - 50, 50, 20).build();
        pitchSlider = new TtsPitchSliderButton(width / 2 - 80, height - 50, 100, 20, Component.literal(String.valueOf(SvcTtsMod.CONFIG.pitch)), (double) SvcTtsMod.CONFIG.pitch / 500);
        historyList = new HistoryList(width - 12, height - 104, 12, 20);
        historyList.setX(12);

        addRenderableWidget(messageField);
        addRenderableWidget(sendButton);
        addRenderableWidget(pitchSlider);
        addRenderableWidget(historyList);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER) send();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void update(Button buttonWidget) {
        for (HistoryListEntry historyListEntry : historyList.children()) {
            if (historyListEntry.children.getFirst() == buttonWidget) {
                updateMessage(historyListEntry.children.get(1));
                updatePitch(historyListEntry.children.get(2));
                send(false);
                return;
            }
        }
    }

    public void updateMessage(Button buttonWidget) {
        messageField.setValue(buttonWidget.getMessage().getString());
    }

    public void updatePitch(Button buttonWidget) {
        pitchSlider.setValue((double) Integer.parseInt(buttonWidget.getMessage().getString()) / 500);
    }

    private void send(Button buttonWidget) {
        send();
    }

    private void send() {
        send(true);
    }

    private void send(boolean historical) {
        String text = messageField.getValue();
        messageField.setValue("");
        Minecraft.getInstance().setScreen(null);
        if (!text.isEmpty()) {
            SvcTtsMod.addToQueue(new TtsMessage(text, pitch), historical);
        }
    }

    public class TtsPitchSliderButton extends AbstractSliderButton {
        public TtsPitchSliderButton(int i, int j, int k, int l, Component component, double d) {
            super(i, j, k, l, component, d);
        }

        @Override
        protected void updateMessage() {
            this.setMessage(Component.literal(String.valueOf((int) Math.floor(this.value * 500))));
        }

        @Override
        protected void applyValue() {
            this.value = (double) Math.round(this.value * 10) / 10;
            pitch = (float) this.value * 5;
        }
    }

    public class HistoryList extends ContainerObjectSelectionList<HistoryListEntry> {
        public HistoryList(int width, int height, int top, int size) {
            super(Minecraft.getInstance(), width, height, top, size);

            for (int i = SvcTtsMod.HISTORY.size(); i > 0; i--) {
                this.addEntry(new HistoryListEntry(i - 1));
            }
        }

        @Override
        protected int getScrollbarPosition() {
            return SendTtsScreen.this.width - 9;
        }

        @Override
        public int getRowLeft() {
            return 10;
        }

        @Override
        public int getRowWidth() {
            return SendTtsScreen.this.width - 20;
        }

        @Override
        protected int getRowTop(int i) {
            return this.getY() - (int)this.getScrollAmount() + i * this.itemHeight;
        }

        @Override
        public int getMaxScroll() {
            return Math.max(0, this.getMaxPosition() - this.height);
        }

        @Override
        protected void renderListBackground(GuiGraphics guiGraphics) {}

        @Override
        public void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
            guiGraphics.fill(9, 9, SendTtsScreen.this.width - 9, SendTtsScreen.this.height - 89, this.isFocused() ? -1 : -6250336);
            guiGraphics.fill(10, 10, SendTtsScreen.this.width - 10, SendTtsScreen.this.height - 90, -16777216);
            super.renderWidget(guiGraphics, i, j, f);
        }
    }

    public class HistoryListEntry extends ContainerObjectSelectionList.Entry<HistoryListEntry> {
        protected final List<Button> children;

        public HistoryListEntry(int historyIndex) {
            this.children = Lists.newArrayList();

            TtsMessage historyMessage = SvcTtsMod.HISTORY.get(historyIndex);
            this.children.add(Button.builder(Component.literal(""), SendTtsScreen.this::update).bounds(12, 0, 20, 20).build());
            this.children.add(Button.builder(Component.literal(historyMessage.text), SendTtsScreen.this::updateMessage).bounds(32, 0, width - 74, 20).build());
            this.children.add(Button.builder(Component.literal(String.valueOf((int) Math.floor(historyMessage.pitch * 100))), SendTtsScreen.this::updatePitch).bounds(width - 42, 0, 30, 20).build());
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return children;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            for (Button child : this.children) {
                child.setPosition(child.getX(), top);
                child.render(guiGraphics, mouseX, mouseY, delta);
            }
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return children;
        }
    }
}
