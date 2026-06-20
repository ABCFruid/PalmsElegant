package com.fruid.palmselegant.client;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class PalmsElegantSliderButton extends AbstractSliderButton {

    private final float min;
    private final float max;
    private final String suffix;
    private final Consumer<Float> setter;

    public PalmsElegantSliderButton(
            int x,
            int y,
            int width,
            int height,
            float currentValue,
            float min,
            float max,
            String suffix,
            Consumer<Float> setter
    ) {
        super(x, y, width, height, Component.empty(), normalize(currentValue, min, max));

        this.min = min;
        this.max = max;
        this.suffix = suffix;
        this.setter = setter;

        this.setter.accept(getActualValue());
        updateMessage();
    }

    private static double normalize(float value, float min, float max) {
        if (max <= min) {
            return 0.0D;
        }

        return Math.max(0.0D, Math.min(1.0D, (value - min) / (max - min)));
    }

    private float getActualValue() {
        float actual = this.min + (float) this.value * (this.max - this.min);
        return Math.round(actual * 100.0F) / 100.0F;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(formatValue(getActualValue())));
    }

    @Override
    protected void applyValue() {
        this.setter.accept(getActualValue());
        updateMessage();
    }

    private String formatValue(float value) {
        if (this.suffix.equals("x")) {
            return String.format("%.1fx", value);
        }

        if (this.suffix.equals("s")) {
            if (value == 0.0F) {
                return "Instant";
            }

            return String.format("%.2fs", value);
        }

        if (this.suffix.equals("°")) {
            return String.format("%.2f°", value);
        }

        return String.valueOf(value);
    }
}