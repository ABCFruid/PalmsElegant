package com.fruid.palmselegant.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

public class PalmsElegantSnapManager {

    public static AngleData closestAngle = null;
    public static float closestDistance = Float.MAX_VALUE;
    public static int closestIndex = -1;

    private static AngleData delayTarget = null;
    private static long delayStartTime = 0L;

    private static AngleData lockedAngle = null;
    private static long lockEndTime = 0L;

    private static AngleData cooldownTarget = null;

    public static void updateClosest(Minecraft minecraft) {
        closestAngle = null;
        closestDistance = Float.MAX_VALUE;
        closestIndex = -1;

        if (minecraft.player == null) {
            return;
        }

        float playerYaw = minecraft.player.getYRot();
        float playerPitch = minecraft.player.getXRot();

        for (int i = 0; i < AngleManager.ANGLES.size(); i++) {
            AngleData angle = AngleManager.ANGLES.get(i);

            float yawDiff = Mth.wrapDegrees(angle.yaw - playerYaw);
            float pitchDiff = Mth.wrapDegrees(angle.pitch - playerPitch);

            float distance = (float)Math.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);

            if (distance > PalmsElegantConfig.snapDistance) {
                continue;
            }

            if (distance < closestDistance) {
                closestDistance = distance;
                closestAngle = angle;
                closestIndex = i;
            }
        }
    }

    public static void snapMouse(LocalPlayer player) {
        if (!PalmsElegantConfig.showPreciseAngle) return;
        if (!PalmsElegantConfig.snapToMarkers) return;

        long now = System.currentTimeMillis();

        updateClosest(Minecraft.getInstance());

        if (lockedAngle != null) {
            player.setYRot(lockedAngle.yaw);
            player.setXRot(lockedAngle.pitch);

            if (now >= lockEndTime) {
                cooldownTarget = lockedAngle;
                lockedAngle = null;
            }

            return;
        }

        if (closestAngle == null) {
            delayTarget = null;
            delayStartTime = 0L;
            cooldownTarget = null;
            return;
        }

        if (cooldownTarget == closestAngle) {
            return;
        }

        if (delayTarget != closestAngle) {
            delayTarget = closestAngle;
            delayStartTime = now;
        }

        long elapsed = now - delayStartTime;
        long requiredDelay = (long)(PalmsElegantConfig.snapDelay * 1000.0F);

        if (elapsed < requiredDelay) {
            return;
        }

        player.setYRot(closestAngle.yaw);
        player.setXRot(closestAngle.pitch);

        lockedAngle = closestAngle;
        lockEndTime = now + (long)(PalmsElegantConfig.snapLockDelay * 1000.0F);
    }
}