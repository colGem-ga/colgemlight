package net.colgem.colgemlight.mixin;

import net.colgem.colgemlight.ColgemLight;
import net.minecraft.world.LightType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Intercepts Sodium's ClonedChunkSection.getLightLevel() which bypasses
 * the vanilla LightingProvider entirely. Sodium clones ChunkNibbleArrays at
 * render time and reads them directly — our LightingProvider mixin has no effect
 * on Sodium-rendered chunks. This mixin fixes that.
 *
 * The target class is loaded conditionally (required=false) so the mod
 * works fine even without Sodium installed.
 */
@Mixin(targets = "me.jellysquid.mods.sodium.client.world.cloned.ClonedChunkSection",
       remap = false)
public class SodiumClonedChunkMixin {

    @Inject(method = "getLightLevel",
            at = @At("HEAD"),
            cancellable = true,
            remap = false)
    private void colgemlight$getLightLevel(LightType lightType, int x, int y, int z,
                                            CallbackInfoReturnable<Integer> cir) {
        if (ColgemLight.lightingDisabled) {
            cir.setReturnValue(ColgemLight.fixedBrightness);
        }
    }
}
