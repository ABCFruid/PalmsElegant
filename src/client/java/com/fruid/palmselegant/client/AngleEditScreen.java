package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

public class AngleEditScreen extends Screen {

    private final Screen parent;
    private AngleSelectionList angleList;
    private AngleSetDropdownWidget setDropdown;

    public AngleEditScreen(Screen parent, AngleData angleData) {
        super(Component.literal("Edit Mode"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        StringWidget title = new StringWidget(
                Component.literal("Edit Mode").withColor(0xFF5555),
                this.font
        );

        title.setPosition(centerX - title.getWidth() / 2, 10);
        this.addRenderableWidget(title);

        int topButtonY = 26;

        int sideButtonWidth = 90;
        int dropdownWidth = 180;
        int gap = 10;

        int totalTopWidth = sideButtonWidth + gap + dropdownWidth + gap + sideButtonWidth;
        int topStartX = centerX - totalTopWidth / 2;

        int optionsX = topStartX;
        int dropdownX = optionsX + sideButtonWidth + gap;
        int clearSetX = dropdownX + dropdownWidth + gap;

        int listTop = 52;
        int listBottom = this.height - 62;
        int listHeight = listBottom - listTop;

        this.angleList = new AngleSelectionList(
                this.minecraft,
                this.width,
                listHeight,
                listTop,
                36,
                angle -> this.minecraft.setScreen(
                        new AngleDetailsScreen(this, angle)
                )
        );

        this.addRenderableWidget(this.angleList);

        this.addRenderableWidget(
                Button.builder(Component.literal("Options"), b -> {
                            this.minecraft.setScreen(new OptionsScreen(this));
                        })
                        .bounds(optionsX, topButtonY, sideButtonWidth, 20)
                        .build()
        );

        this.setDropdown = new AngleSetDropdownWidget(
                dropdownX,
                topButtonY,
                dropdownWidth,
                20,
                () -> this.minecraft.setScreen(new AngleEditScreen(this.parent, null))
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Clear Set"), b -> {
                            this.minecraft.setScreen(
                                    new ConfirmActionScreen(
                                            new AngleEditScreen(this.parent, null),
                                            Component.literal("Are you sure you would like to clear the current set: ")
                                                    .append(Component.literal(AngleManager.getCurrentSetName()))
                                                    .append(Component.literal("?")),
                                            Component.literal("All angles from this set will be deleted!"),
                                            () -> AngleManager.clearCurrentSet()
                                    )
                            );
                        })
                        .bounds(clearSetX, topButtonY, sideButtonWidth, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Add Angle"), b -> {
                            this.minecraft.setScreen(new AngleDetailsScreen(this, null));
                        })
                        .bounds(centerX - 150, this.height - 55, 300, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Return to Menu"), b -> {
                            this.minecraft.setScreen(this.parent);
                        })
                        .bounds(centerX - 150, this.height - 25, 140, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), b -> {
                            this.minecraft.setScreen(this.parent);
                        })
                        .bounds(centerX + 10, this.height - 25, 140, 20)
                        .build()
        );

        this.addRenderableOnly(this.setDropdown);

    }

    public void addAngleToList(AngleData angleData) {
        this.angleList.addAngle(angleData);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (this.setDropdown != null && this.setDropdown.handleScreenClick(event)) {
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.setDropdown != null && this.setDropdown.handleScreenScroll(mouseX, mouseY, scrollY)) {
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

}