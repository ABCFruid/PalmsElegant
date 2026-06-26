package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OptionsScreen extends Screen {

    private final Screen parent;

    private Button showPreciseAngleButton;
    private Button snapToMarkersButton;

    public OptionsScreen(Screen parent) {
        super(Component.literal("PalmsElegant Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;

        StringWidget title = new StringWidget(
                Component.literal("PalmsElegant Options"),
                this.font
        );
        title.setPosition(centerX - title.getWidth() / 2, 10);
        this.addRenderableWidget(title);

        int labelX = 35;
        int widgetX = labelX + 155;
        int widgetWidth = 150;
        int rowSpacing = 24;
        int y = 35;

        addLabel("Show Precise Angle", labelX, y + 5);
        this.showPreciseAngleButton = Button.builder(Component.empty(), b -> {
                    PalmsElegantConfig.showPreciseAngle = !PalmsElegantConfig.showPreciseAngle;
                    updateButtons();
                })
                .bounds(widgetX, y, widgetWidth, 20)
                .build();
        this.addRenderableWidget(this.showPreciseAngleButton);

        y += rowSpacing;

        addLabel("Marker Scale", labelX, y + 5);
        this.addRenderableWidget(new PalmsElegantSliderButton(
                widgetX, y, widgetWidth, 20,
                PalmsElegantConfig.markerScale,
                0.0F,
                1.0F,
                "x",
                value -> PalmsElegantConfig.markerScale = value
        ));

        y += rowSpacing;

        addLabel("Text Scale", labelX, y + 5);
        this.addRenderableWidget(new PalmsElegantSliderButton(
                widgetX, y, widgetWidth, 20,
                PalmsElegantConfig.textScale,
                0.0F,
                1.0F,
                "x",
                value -> PalmsElegantConfig.textScale = value
        ));

        y += rowSpacing;

        addLabel("Snap to Markers", labelX, y + 5);
        this.snapToMarkersButton = Button.builder(Component.empty(), b -> {
                    PalmsElegantConfig.snapToMarkers = !PalmsElegantConfig.snapToMarkers;
                    updateButtons();
                })
                .bounds(widgetX, y, widgetWidth, 20)
                .build();
        this.addRenderableWidget(this.snapToMarkersButton);

        y += rowSpacing;

        addLabel("Snap Delay", labelX, y + 5);
        this.addRenderableWidget(new PalmsElegantSliderButton(
                widgetX, y, widgetWidth, 20,
                PalmsElegantConfig.snapDelay,
                0.0F,
                1.0F,
                "s",
                value -> PalmsElegantConfig.snapDelay = value
        ));

        y += rowSpacing;

        addLabel("Snap Lock Delay", labelX, y + 5);
        this.addRenderableWidget(new PalmsElegantSliderButton(
                widgetX, y, widgetWidth, 20,
                PalmsElegantConfig.snapLockDelay,
                0.0F,
                1.0F,
                "s",
                value -> PalmsElegantConfig.snapLockDelay = value
        ));

        y += rowSpacing;

        addLabel("Snap Distance", labelX, y + 5);
        this.addRenderableWidget(new PalmsElegantSliderButton(
                widgetX, y, widgetWidth, 20,
                PalmsElegantConfig.snapDistance,
                0.0F,
                10.0F,
                "°",
                value -> PalmsElegantConfig.snapDistance = value
        ));

        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), b -> {
                            PalmsElegantConfig.save();
                            this.minecraft.gui.setScreen(this.parent);
                        })
                        .bounds(centerX - 100, this.height - 27, 200, 20)
                        .build()
        );

        updateButtons();
    }

    private void addLabel(String text, int x, int y) {
        StringWidget label = new StringWidget(Component.literal(text), this.font);
        label.setPosition(x, y);
        this.addRenderableWidget(label);
    }

    private void updateButtons() {
        this.showPreciseAngleButton.setMessage(Component.literal(onOff(PalmsElegantConfig.showPreciseAngle)));
        this.snapToMarkersButton.setMessage(Component.literal(onOff(PalmsElegantConfig.snapToMarkers)));
    }

    private String onOff(boolean value) {
        return value ? "ON" : "OFF";
    }
}