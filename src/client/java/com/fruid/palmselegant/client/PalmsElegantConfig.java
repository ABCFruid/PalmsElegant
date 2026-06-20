package com.fruid.palmselegant.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class PalmsElegantConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static boolean showPreciseAngle = true;

    public static float markerScale = 0.2F;
    public static float textScale = 0.2F;

    public static boolean snapToMarkers = true;

    public static float snapDelay = 0.0F;
    public static float snapLockDelay = 0.25F;

    public static float snapDistance = 4.04F;

    private static Path getConfigPath() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("palmselegant")
                .resolve("worlds")
                .resolve(PalmsElegantContext.getCurrentContextId())
                .resolve("options.json");
    }

    public static void load() {
        try {
            Path configPath = getConfigPath();

            if (!Files.exists(configPath)) {
                save();
                return;
            }

            FileReader reader = new FileReader(configPath.toFile());
            ConfigData data = GSON.fromJson(reader, ConfigData.class);
            reader.close();

            if (data == null) {
                save();
                return;
            }

            showPreciseAngle = data.showPreciseAngle;
            markerScale = clamp(data.markerScale, 0.0F, 1.0F);
            textScale = clamp(data.textScale, 0.0F, 1.0F);

            snapToMarkers = data.snapToMarkers;
            snapDelay = clamp(data.snapDelay, 0.0F, 1.0F);
            snapLockDelay = clamp(data.snapLockDelay, 0.0F, 1.0F);
            snapDistance = clamp(data.snapDistance, 0.0F, 10.0F);

        } catch (Exception e) {
            e.printStackTrace();
            save();
        }
    }

    public static void save() {
        try {
            Path configPath = getConfigPath();

            Files.createDirectories(configPath.getParent());

            ConfigData data = new ConfigData();

            data.showPreciseAngle = showPreciseAngle;
            data.markerScale = markerScale;
            data.textScale = textScale;
            data.snapToMarkers = snapToMarkers;
            data.snapDelay = snapDelay;
            data.snapLockDelay = snapLockDelay;
            data.snapDistance = snapDistance;

            FileWriter writer = new FileWriter(configPath.toFile());
            GSON.toJson(data, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    private static class ConfigData {
        boolean showPreciseAngle = true;

        float markerScale = 0.2F;
        float textScale = 0.2F;

        boolean snapToMarkers = true;

        float snapDelay = 0.0F;
        float snapLockDelay = 0.25F;

        float snapDistance = 4.04F;
    }
}