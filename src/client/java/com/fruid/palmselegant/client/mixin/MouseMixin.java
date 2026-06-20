package com.fruid.palmselegant.client.mixin;

import com.fruid.palmselegant.client.PalmsElegantSnapManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public abstract class MouseMixin {

    @WrapOperation(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
            )
    )
    private void palmselegant$snapToMarker(
            LocalPlayer player,
            double cursorDeltaX,
            double cursorDeltaY,
            Operation<Void> original
    ) {
        original.call(player, cursorDeltaX, cursorDeltaY);
        PalmsElegantSnapManager.snapMouse(player);
    }
}