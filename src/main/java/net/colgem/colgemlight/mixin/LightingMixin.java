package net.colgem.colgemlight.mixin;

import net.colgem.colgemlight.ColgemLight;
import net.minecraft.world.chunk.light.LightingProvider;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts LightingProvider to:
 *  1. Return a fixed light level (0-15) from getLight().
 *  2. Skip checkBlock() so block-change light updates are not enqueued.
 *  3. Skip doLightUpdates() so queued updates are never processed.
 *
 * NOTE: getLight() must return a plain 0-15 value — it is used as an array
 * index into DimensionType's brightness table (length 16). Returning anything
 * outside 0-15 causes ArrayIndexOutOfBoundsException.
 */
@Mixin(LightingProvider.class)
public class LightingMixin {

    // method_22363 = getLight(BlockPos, int ambientDarkness) -> int (0-15)
    @Inject(method = "method_22363", at = @At("HEAD"), cancellable = true)
    private void colgemlight$getLight(BlockPos pos, int ambientDarkness,
                                      CallbackInfoReturnable<Integer> cir) {
        if (ColgemLight.lightingDisabled) {
            // Must be clamped 0-15: used as index into DimensionType.brightnessByLightLevel[]
            cir.setReturnValue(ColgemLight.fixedBrightness);
        }
    }

    // method_15559 = checkBlock(BlockPos) -> void  (enqueues a position for light recalc)
    @Inject(method = "method_15559", at = @At("HEAD"), cancellable = true)
    private void colgemlight$checkBlock(BlockPos pos, CallbackInfo ci) {
        if (ColgemLight.lightingDisabled) {
            ci.cancel();
        }
    }

    // method_15563 = doLightUpdates(int, boolean, boolean) -> int  (processes queued updates)
    @Inject(method = "method_15563", at = @At("HEAD"), cancellable = true)
    private void colgemlight$doLightUpdates(int maxUpdateCount, boolean doSkylight,
                                             boolean skipEdgeLightPropagation,
                                             CallbackInfoReturnable<Integer> cir) {
        if (ColgemLight.lightingDisabled) {
            cir.setReturnValue(0);
        }
    }
}
