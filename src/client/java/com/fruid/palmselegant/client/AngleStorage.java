package com.fruid.palmselegant.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AngleStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path getWorldFolder() {
        return FabricLoader.getInstance()
                .getConfigDir()
                .resolve("palmselegant")
                .resolve("worlds")
                .resolve(PalmsElegantContext.getCurrentContextId());
    }

    private static Path getSetsFolder() {
        return getWorldFolder().resolve("sets");
    }

    private static Path getSelectedSetFile() {
        return getWorldFolder().resolve("selected_set.json");
    }

    private static Path getSetFile(String setName) {
        String safeName = sanitizeFileName(setName);
        return getSetsFolder().resolve(safeName + ".json");
    }

    public static String loadSelectedSetName() {
        try {
            Path file = getSelectedSetFile();

            if (!Files.exists(file)) {
                return "Default";
            }

            FileReader reader = new FileReader(file.toFile());
            SelectedSetData data = GSON.fromJson(reader, SelectedSetData.class);
            reader.close();

            if (data == null || data.selectedSetName == null || data.selectedSetName.isBlank()) {
                return "Default";
            }

            return data.selectedSetName;

        } catch (Exception e) {
            e.printStackTrace();
            return "Default";
        }
    }

    public static void saveSelectedSetName(String setName) {
        try {
            if (PalmsElegantContext.getCurrentContextId().equals("unknown")) {
                return;
            }

            Path file = getSelectedSetFile();
            Files.createDirectories(file.getParent());

            SelectedSetData data = new SelectedSetData();
            data.selectedSetName = setName;

            FileWriter writer = new FileWriter(file.toFile());
            GSON.toJson(data, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<AngleData> loadSet(String setName) {
        try {
            Path file = getSetFile(setName);

            if (!Files.exists(file)) {
                List<AngleData> startingAngles;

                if (setName.equals("Default")) {
                    startingAngles = AngleManager.createDefaultAngles();
                } else {
                    startingAngles = new java.util.ArrayList<>();
                }

                saveSet(setName, startingAngles);
                return startingAngles;
            }

            FileReader reader = new FileReader(file.toFile());

            Type listType = new TypeToken<List<AngleData>>() {}.getType();
            List<AngleData> loaded = GSON.fromJson(reader, listType);

            reader.close();

            if (loaded == null) {
                return AngleManager.createDefaultAngles();
            }

            return loaded;

        } catch (Exception e) {
            e.printStackTrace();
            return AngleManager.createDefaultAngles();
        }
    }

    public static void saveSet(String setName, List<AngleData> angles) {
        try {
            if (PalmsElegantContext.getCurrentContextId().equals("unknown")) {
                return;
            }

            Path file = getSetFile(setName);

            Files.createDirectories(file.getParent());

            FileWriter writer = new FileWriter(file.toFile());
            GSON.toJson(angles, writer);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSet(String setName) {
        try {
            Path file = getSetFile(setName);
            Files.deleteIfExists(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> loadSetNames() {
        List<String> names = new java.util.ArrayList<>();

        try {
            Path folder = getSetsFolder();

            Files.createDirectories(folder);

            if (!names.contains("Default")) {
                names.add("Default");
            }

            try (java.util.stream.Stream<Path> stream = Files.list(folder)) {
                stream
                        .filter(path -> path.toString().endsWith(".json"))
                        .forEach(path -> {
                            String fileName = path.getFileName().toString();
                            String setName = fileName.substring(0, fileName.length() - 5).replace("_", " ");

                            if (!names.contains(setName)) {
                                names.add(setName);
                            }
                        });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return names;
    }

    private static String sanitizeFileName(String text) {
        if (text == null || text.isBlank()) {
            return "Default";
        }

        return text.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private static class SelectedSetData {
        String selectedSetName = "Default";
    }
}