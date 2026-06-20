package com.fruid.palmselegant.client.mixin;

import com.fruid.palmselegant.client.PalmsElegantMarkerOverlay;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.gizmos.Gizmos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(
            method = "collectPerFrameGizmos",
            at = @At("RETURN")
    )
    private void palmselegant$addAngleGizmos(
            CallbackInfoReturnable<Gizmos.TemporaryCollection> cir
    ) {
        PalmsElegantMarkerOverlay.renderWorldGizmos();
    }
}