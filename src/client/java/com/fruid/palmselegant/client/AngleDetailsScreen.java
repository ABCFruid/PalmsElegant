package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class AngleDetailsScreen extends Screen {

    private final AngleEditScreen parent;
    private final AngleData angleData;
    private final boolean editing;

    private EditBox nameBox;
    private EditBox yawBox;
    private EditBox pitchBox;
    private EditBox colorBox;

    private StringWidget hexColorLabel;

    public AngleDetailsScreen(AngleEditScreen parent, AngleData angleData) {
        super(Component.literal(angleData == null ? "Add Angle" : "Edit Angle"));

        this.parent = parent;
        this.angleData = angleData;
        this.editing = angleData != null;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        StringWidget title = new StringWidget(
                Component.literal(this.editing ? "Edit Angle" : "Add Angle"),
                this.font
        );
        title.setPosition(centerX - title.getWidth() / 2, 20);
        this.addRenderableWidget(title);

        if (this.editing) {
            this.addRenderableWidget(
                    Button.builder(Component.literal("Delete").withColor(0xFF5555), b -> {
                                this.minecraft.setScreen(
                                        new ConfirmActionScreen(
                                                this.parent,
                                                Component.literal("Are you sure you would like to delete the current angle: ")
                                                        .append(Component.literal(this.angleData.name).withColor(this.angleData.color))
                                                        .append(Component.literal("?")),
                                                Component.literal("This action is permanent!"),
                                                () -> {
                                                    AngleManager.removeAngle(this.angleData);
                                                }
                                        )
                                );
                            })
                            .bounds(this.width - 90, 10, 70, 20)
                            .build()
            );
        }

        int fieldWidth = 240;
        int fieldX = centerX - fieldWidth / 2;

        addLabel("Name:", centerX, 45);
        this.nameBox = new EditBox(this.font, fieldX, 60, fieldWidth, 20, Component.literal("Name"));
        this.nameBox.setValue(this.editing ? this.angleData.name : "New Angle");
        this.addRenderableWidget(this.nameBox);

        addLabel("Yaw:", centerX, 90);
        this.yawBox = new EditBox(this.font, fieldX, 105, fieldWidth, 20, Component.literal("Yaw"));
        this.yawBox.setValue(this.editing ? String.valueOf(this.angleData.yaw) : "0");
        this.addRenderableWidget(this.yawBox);

        addLabel("Pitch:", centerX, 135);
        this.pitchBox = new EditBox(this.font, fieldX, 150, fieldWidth, 20, Component.literal("Pitch"));
        this.pitchBox.setValue(this.editing ? String.valueOf(this.angleData.pitch) : "0");
        this.addRenderableWidget(this.pitchBox);

        int startingColor = this.editing ? this.angleData.color : 0xFFFFFF;

        this.hexColorLabel = new StringWidget(
                Component.literal("Hex Color:").withColor(startingColor),
                this.font
        );
        this.hexColorLabel.setPosition(centerX - this.hexColorLabel.getWidth() / 2, 180);
        this.addRenderableWidget(this.hexColorLabel);

        this.colorBox = new EditBox(this.font, fieldX, 195, fieldWidth, 20, Component.literal("Hex Color"));
        this.colorBox.setValue(String.format("%06X", startingColor));
        this.colorBox.setResponder(text -> {
            int color = parseHexColor(text, 0xFFFFFF);

            this.hexColorLabel.setMessage(
                    Component.literal("Hex Color:").withColor(color)
            );
        });
        this.addRenderableWidget(this.colorBox);

        this.addRenderableWidget(
                Button.builder(Component.literal("Confirm"), b -> {
                            String name = this.nameBox.getValue();
                            float yaw = parseFloat(this.yawBox.getValue(), 0.0F);
                            float pitch = parseFloat(this.pitchBox.getValue(), 0.0F);
                            int color = parseHexColor(this.colorBox.getValue(), 0xFFFFFF);

                            if (this.editing) {
                                this.angleData.name = name;
                                this.angleData.yaw = yaw;
                                this.angleData.pitch = pitch;
                                this.angleData.color = color;
                            } else {
                                this.parent.addAngleToList(new AngleData(name, yaw, pitch, color));
                            }

                            AngleManager.saveCurrentSet();

                            this.minecraft.setScreen(this.parent);
                        })
                        .bounds(centerX - 150, this.height - 25, 140, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("Cancel"), b -> {
                            this.minecraft.setScreen(this.parent);
                        })
                        .bounds(centerX + 10, this.height - 25, 140, 20)
                        .build()
        );
    }

    private void addLabel(String text, int centerX, int y) {
        StringWidget label = new StringWidget(
                Component.literal(text),
                this.font
        );

        label.setPosition(centerX - label.getWidth() / 2, y);
        this.addRenderableWidget(label);
    }

    private float parseFloat(String text, float fallback) {
        try {
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private int parseHexColor(String text, int fallback) {
        try {
            String cleaned = text.replace("#", "");
            return Integer.parseInt(cleaned, 16);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}