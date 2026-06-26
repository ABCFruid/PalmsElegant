package com.fruid.palmselegant.client;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class PalmsElegantMarkerOverlay {

    public static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        // Old HUD marker disabled.
        // World gizmo marker is now rendered through LevelRendererMixin.
    }

    public static void renderWorldGizmos() {
        Minecraft minecraft = Minecraft.getInstance();

        if (!PalmsElegantConfig.showPreciseAngle) return;
        if (minecraft.player == null) return;
        if (minecraft.gui.hud.isHidden()) return;

        Camera camera = minecraft.gameRenderer.mainCamera();

        PalmsElegantSnapManager.updateClosest(minecraft);

        for (int i = 0; i < AngleManager.ANGLES.size(); i++) {
            AngleData angle = AngleManager.ANGLES.get(i);

            Vec3 position = Vec3.directionFromRotation(
                    Mth.wrapDegrees(angle.pitch),
                    Mth.wrapDegrees(angle.yaw)
            ).scale(16.0D).add(camera.position());

            int color = 0xFF000000 | angle.color;

            float markerRadius = Math.max(
                    0.09F,
                    PalmsElegantConfig.markerScale * 0.60F
            );

            float targetRadius = markerRadius + 0.14F;

            float markerLineWidth = Math.max(
                    1.5F,
                    markerRadius * 10.0F
            );

            float targetLineWidth = markerLineWidth + 1.0F;

            if (PalmsElegantConfig.snapToMarkers && i == PalmsElegantSnapManager.closestIndex) {
                drawBillboardRing(
                        camera,
                        position,
                        targetRadius,
                        0xFFFFFFFF,
                        targetLineWidth
                );
            }

            drawBillboardRing(
                    camera,
                    position,
                    markerRadius,
                    color,
                    markerLineWidth
            );

            if (angle.name != null && !angle.name.isEmpty()) {
                float textScale = Math.max(
                        0.35F,
                        PalmsElegantConfig.textScale * 2.0F
                );

                double textOffset = markerRadius + 0.18D + (textScale * 0.50D);

                Gizmos.billboardText(
                        angle.name,
                        position.add(0.0D, textOffset, 0.0D),
                        TextGizmo.Style.forColorAndCentered(color)
                                .withScale(textScale)
                ).setAlwaysOnTop();
            }
        }
    }

    private static void drawBillboardRing(
            Camera camera,
            Vec3 center,
            float radius,
            int color,
            float width
    ) {
        Vector3f leftVector = new Vector3f(1.0F, 0.0F, 0.0F);
        Vector3f upVector = new Vector3f(0.0F, 1.0F, 0.0F);

        camera.rotation().transform(leftVector);
        camera.rotation().transform(upVector);

        Vec3 right = new Vec3(
                -leftVector.x(),
                -leftVector.y(),
                -leftVector.z()
        );

        Vec3 up = new Vec3(
                upVector.x(),
                upVector.y(),
                upVector.z()
        );

        int segments = 48;

        for (int i = 0; i < segments; i++) {
            double angleA = (Math.PI * 2.0D * i) / segments;
            double angleB = (Math.PI * 2.0D * (i + 1)) / segments;

            Vec3 pointA = center
                    .add(right.scale(Math.cos(angleA) * radius))
                    .add(up.scale(Math.sin(angleA) * radius));

            Vec3 pointB = center
                    .add(right.scale(Math.cos(angleB) * radius))
                    .add(up.scale(Math.sin(angleB) * radius));

            Gizmos.line(
                    pointA,
                    pointB,
                    color,
                    width
            ).setAlwaysOnTop();
        }
    }
}