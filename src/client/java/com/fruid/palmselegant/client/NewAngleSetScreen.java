package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class NewAngleSetScreen extends Screen {

    private final Screen parent;

    private EditBox nameBox;
    private Button confirmButton;

    public NewAngleSetScreen(Screen parent) {
        super(Component.literal("New Angle Set"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        StringWidget title = new StringWidget(
                Component.literal("New Angle Set"),
                this.font
        );
        title.setPosition(centerX - title.getWidth() / 2, 35);
        this.addRenderableWidget(title);

        int boxWidth = this.font.width("W".repeat(AngleManager.MAX_SET_NAME_LENGTH)) + 12;
        int boxX = centerX - boxWidth / 2;

        this.nameBox = new EditBox(
                this.font,
                boxX,
                95,
                boxWidth,
                20,
                Component.literal("Set Name")
        );

        this.nameBox.setMaxLength(AngleManager.MAX_SET_NAME_LENGTH);
        this.nameBox.setValue("");
        this.nameBox.setResponder(text -> updateConfirmButton());

        this.addRenderableWidget(this.nameBox);

        this.confirmButton = Button.builder(Component.literal("Confirm"), b -> {
                    String setName = this.nameBox.getValue().trim();

                    if (AngleManager.canCreateSet(setName)) {
                        AngleManager.createNewSet(setName);
                        this.minecraft.setScreen(this.parent);
                    }
                })
                .bounds(centerX - 150, this.height - 55, 140, 20)
                .build();

        this.addRenderableWidget(this.confirmButton);

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), b -> {
                            this.minecraft.setScreen(this.parent);
                        })
                        .bounds(centerX + 10, this.height - 55, 140, 20)
                        .build()
        );

        this.setInitialFocus(this.nameBox);
        updateConfirmButton();
    }

    private void updateConfirmButton() {
        if (this.confirmButton == null || this.nameBox == null) {
            return;
        }

        String setName = this.nameBox.getValue().trim();
        this.confirmButton.active = AngleManager.canCreateSet(setName);
    }
}