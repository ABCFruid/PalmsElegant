package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfirmActionScreen extends Screen {

    private final Screen parent;
    private final Component line1;
    private final Component line2;
    private final Runnable confirmAction;

    public ConfirmActionScreen(Screen parent, Component line1, Component line2, Runnable confirmAction) {
        super(Component.literal("Confirm"));
        this.parent = parent;
        this.line1 = line1;
        this.line2 = line2;
        this.confirmAction = confirmAction;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int textY = this.height / 2 - 60;

        StringWidget line1Widget = new StringWidget(this.line1, this.font);
        line1Widget.setPosition(centerX - line1Widget.getWidth() / 2, textY);
        this.addRenderableWidget(line1Widget);

        StringWidget line2Widget = new StringWidget(this.line2, this.font);
        line2Widget.setPosition(centerX - line2Widget.getWidth() / 2, textY + 25);
        this.addRenderableWidget(line2Widget);

        this.addRenderableWidget(
                Button.builder(Component.literal("Yes"), b -> {
                            this.confirmAction.run();
                            this.minecraft.gui.setScreen(this.parent);
                        })
                        .bounds(centerX - 155, textY + 80, 150, 20)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("No"), b -> {
                            this.minecraft.gui.setScreen(this.parent);
                        })
                        .bounds(centerX + 5, textY + 80, 150, 20)
                        .build()
        );
    }
}