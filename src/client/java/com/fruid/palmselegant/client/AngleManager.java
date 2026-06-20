package com.fruid.palmselegant.client;

import java.util.ArrayList;
import java.util.List;

public class AngleManager {

    public static final List<String> SETS = new ArrayList<>();
    public static final List<AngleData> ANGLES = new ArrayList<>();

    public static final int MAX_SET_NAME_LENGTH = 27;

    public static String currentSetName = "Default";

    static {
        SETS.add("Default");
    }

    public static String getCurrentSetName() {
        return currentSetName;
    }

    public static boolean canCreateSet(String setName) {
        if (setName == null) {
            return false;
        }

        String cleaned = setName.trim();

        if (cleaned.isEmpty()) {
            return false;
        }

        if (cleaned.length() > MAX_SET_NAME_LENGTH) {
            return false;
        }

        return !SETS.contains(cleaned);
    }

    public static void createNewSet(String setName) {
        String cleaned = setName.trim();

        if (!canCreateSet(cleaned)) {
            return;
        }

        SETS.add(cleaned);
        setCurrentSet(cleaned);
        saveCurrentSet();
    }

    public static void loadCurrentSet() {
        ANGLES.clear();
        ANGLES.addAll(AngleStorage.loadSet(currentSetName));
    }

    public static void reloadSetsForCurrentWorld() {
        SETS.clear();
        SETS.addAll(AngleStorage.loadSetNames());

        if (!SETS.contains("Default")) {
            SETS.add(0, "Default");
        }

        currentSetName = AngleStorage.loadSelectedSetName();

        if (!SETS.contains(currentSetName)) {
            currentSetName = "Default";
        }

        loadCurrentSet();
    }

    public static void saveCurrentSet() {
        if (PalmsElegantContext.getCurrentContextId().equals("unknown")) {
            return;
        }

        AngleStorage.saveSet(currentSetName, ANGLES);
        AngleStorage.saveSelectedSetName(currentSetName);
    }

    public static void clearCurrentSet() {
        if (currentSetName.equals("Default")) {
            ANGLES.clear();
            saveCurrentSet();
            return;
        }

        String setToDelete = currentSetName;

        AngleStorage.deleteSet(setToDelete);
        SETS.remove(setToDelete);

        currentSetName = "Default";
        loadCurrentSet();
    }

    public static void createNewSet() {
        throw new RuntimeException("OLD createNewSet() CALLED");
    }

    public static void setCurrentSet(String setName) {
        saveCurrentSet();

        currentSetName = setName;

        if (!SETS.contains(setName)) {
            SETS.add(setName);
        }

        loadCurrentSet();
    }

    public static void cycleSet() {
        saveCurrentSet();

        int index = SETS.indexOf(currentSetName);

        if (index == -1) {
            index = 0;
        } else {
            index++;
        }

        if (index >= SETS.size()) {
            index = 0;
        }

        currentSetName = SETS.get(index);
        loadCurrentSet();
    }

    public static void addAngle(AngleData angleData) {
        ANGLES.add(angleData);
        saveCurrentSet();
    }

    public static void removeAngle(AngleData angleData) {
        ANGLES.remove(angleData);
        saveCurrentSet();
    }

    public static void saveEditedAngles() {
        saveCurrentSet();
    }

    public static List<AngleData> createDefaultAngles() {
        return new ArrayList<>();
    }
}