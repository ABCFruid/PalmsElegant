package com.fruid.palmselegant.client;

public class PalmsElegantContext {

    private static String currentContextId = "unknown";

    public static String getCurrentContextId() {
        return currentContextId;
    }

    public static void setSingleplayerWorld(String worldName) {
        currentContextId = "singleplayer_" + sanitize(worldName);
    }

    public static void setMultiplayerServer(String serverAddress) {
        currentContextId = "multiplayer_" + sanitize(serverAddress);
    }

    public static void clear() {
        currentContextId = "unknown";
    }

    private static String sanitize(String text) {
        if (text == null || text.isBlank()) {
            return "unknown";
        }

        return text.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}