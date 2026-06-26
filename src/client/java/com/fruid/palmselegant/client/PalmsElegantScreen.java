package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.input.MouseButtonEvent;

public class PalmsElegantScreen extends Screen {

    private AngleSelectionList angleList;;
    private AngleSetDropdownWidget setDropdown;

    public PalmsElegantScreen() {
        super(Component.literal("PalmsElegant Menu"));
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        StringWidget titleWidget = new StringWidget(
                Component.literal("PalmsElegant Menu"),
                this.font
        );

        titleWidget.setPosition(
                centerX - titleWidget.getWidth() / 2,
                10
        );

        this.addRenderableWidget(titleWidget);

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
        int listBottom = this.height - 32;
        int listHeight = listBottom - listTop;

        this.angleList = new AngleSelectionList(
                this.minecraft,
                this.width,
                listHeight,
                listTop,
                36,
                angle -> {
                    if (this.minecraft.player != null) {
                        this.minecraft.player.setYRot(angle.yaw);
                        this.minecraft.player.setXRot(angle.pitch);
                    }

                    this.minecraft.gui.setScreen(null);
                }
        );
        this.addRenderableWidget(this.angleList);

        this.addRenderableWidget(
                Button.builder(Component.literal("Options"), b -> {
                            this.minecraft.gui.setScreen(new OptionsScreen(this));
                        })
                        .bounds(optionsX, topButtonY, sideButtonWidth, 20)
                        .build()
        );

        this.setDropdown = new AngleSetDropdownWidget(
                dropdownX,
                topButtonY,
                dropdownWidth,
                20,
                () -> this.minecraft.gui.setScreen(new PalmsElegantScreen())
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Clear Set"), b -> {
                            this.minecraft.gui.setScreen(
                                    new ConfirmActionScreen(
                                            new PalmsElegantScreen(),
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
                Button.builder(Component.literal("Add/Edit/Delete"), b -> {
                            this.minecraft.gui.setScreen(
                                    new AngleEditScreen(this, null)
                            );
                        })
                        .bounds(centerX - 150, this.height - 25, 140, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), b -> this.onClose())
                        .bounds(centerX + 10, this.height - 25, 140, 20)
                        .build()
        );
        this.addRenderableOnly(this.setDropdown);
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