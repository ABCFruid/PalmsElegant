package com.fruid.palmselegant.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.resources.Identifier;

public class PalmsElegantClient implements ClientModInitializer {

	private static KeyMapping menuKey;

	@Override
	public void onInitializeClient() {

		HudElementRegistry.addLast(
				Identifier.fromNamespaceAndPath("palmselegant", "marker_overlay"),
				PalmsElegantMarkerOverlay::render
		);

		menuKey = KeyMappingHelper.registerKeyMapping(
				new KeyMapping(
						"key.palmselegant.menu",
						InputConstants.Type.KEYSYM,
						InputConstants.UNKNOWN.getValue(),
						KeyMapping.Category.register(
								Identifier.fromNamespaceAndPath("palmselegant", "menu")
						)
				)
		);

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {

			if (client.hasSingleplayerServer() && client.getSingleplayerServer() != null) {
				String worldName = client.getSingleplayerServer()
						.getWorldData()
						.getLevelName();

				PalmsElegantContext.setSingleplayerWorld(worldName);
			} else {
				ServerData serverData = client.getCurrentServer();

				if (serverData != null) {
					PalmsElegantContext.setMultiplayerServer(serverData.ip);
				} else {
					PalmsElegantContext.clear();
				}
			}

			PalmsElegantConfig.load();
			AngleManager.reloadSetsForCurrentWorld();
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			AngleManager.saveCurrentSet();
			PalmsElegantConfig.save();
			PalmsElegantContext.clear();
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (menuKey.consumeClick()) {
				Minecraft.getInstance().setScreen(new PalmsElegantScreen());
			}
		});

		System.out.println("Palms Elegant loaded!");
	}
}