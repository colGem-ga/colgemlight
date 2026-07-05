package net.colgem.colgemlight.mixin;

import net.colgem.colgemlight.ColgemLight;
import net.minecraft.world.chunk.light.ChunkLightProvider;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts ChunkLightProvider.getLightLevel() (the per-type light query)
 * to return the fixed brightness value.
 * Return range must be 0-15 (used as brightness array index).
 */
@Mixin(ChunkLightProvider.class)
public class ChunkLightProviderMixin {

    // method_15543 = getLightLevel(BlockPos) -> int (0-15)
    // defined on ChunkLightingView interface, implemented by ChunkLightProvider subclasses
    @Inject(method = "method_15543", at = @At("HEAD"), cancellable = true)
    private void colgemlight$getLightLevel(BlockPos pos,
                                            CallbackInfoReturnable<Integer> cir) {
        if (ColgemLight.lightingDisabled) {
            cir.setReturnValue(ColgemLight.fixedBrightness);
        }
    }
}
