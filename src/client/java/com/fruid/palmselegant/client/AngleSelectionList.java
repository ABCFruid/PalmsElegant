package com.fruid.palmselegant.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class AngleSelectionList extends ObjectSelectionList<AngleSelectionList.AngleEntry> {

    private final Consumer<AngleData> onAngleClicked;

    public AngleSelectionList(Minecraft minecraft, int width, int height, int top, int itemHeight) {
        this(minecraft, width, height, top, itemHeight, null);
    }

    public AngleSelectionList(
            Minecraft minecraft,
            int width,
            int height,
            int top,
            int itemHeight,
            Consumer<AngleData> onAngleClicked
    ) {
        super(minecraft, width, height, top, itemHeight);

        this.onAngleClicked = onAngleClicked;

        for (AngleData angle : AngleManager.ANGLES) {
            this.addEntry(new AngleEntry(this, angle));
        }
    }

    public void handleAngleClicked(AngleData angleData) {
        if (this.onAngleClicked != null) {
            this.onAngleClicked.accept(angleData);
        }
    }

    @Override
    public int getRowWidth() {
        return 300;
    }

    public AngleData getSelectedAngleData() {
        AngleEntry selected = this.getSelected();

        if (selected == null) {
            return null;
        }

        return selected.getAngleData();
    }

    public void addAngle(AngleData angleData) {
        AngleManager.ANGLES.add(angleData);
        this.addEntry(new AngleEntry(this, angleData));
    }

    public static class AngleEntry extends ObjectSelectionList.Entry<AngleEntry> {

        private final AngleSelectionList list;
        private final AngleData angleData;

        public AngleEntry(AngleSelectionList list, AngleData angleData) {
            this.list = list;
            this.angleData = angleData;
        }

        public AngleData getAngleData() {
            return this.angleData;
        }

        @Override
        public Component getNarration() {
            return Component.literal(this.angleData.name);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            this.list.setSelected(this);

            this.list.handleAngleClicked(this.angleData);

            return true;
        }

        @Override
        public void extractContent(
                GuiGraphicsExtractor graphics,
                int mouseX,
                int mouseY,
                boolean hovered,
                float delta
        ) {
            Minecraft minecraft = Minecraft.getInstance();

            int x = this.getContentXMiddle() - 135;
            int y = this.getContentY() + 4;

            graphics.text(
                    minecraft.font,
                    this.angleData.name,
                    x,
                    y,
                    0xFF000000 | this.angleData.color
            );

            graphics.text(
                    minecraft.font,
                    "Yaw: " + this.angleData.yaw + "   Pitch: " + this.angleData.pitch,
                    x,
                    y + 12,
                    0xFFFFFFFF
            );
        }
    }
}