package net.colgem.colgemlight.mixin;

import net.colgem.colgemlight.ColgemLight;
import net.minecraft.client.world.ClientChunkManager;
import net.minecraft.world.LightType;
import net.minecraft.util.math.ChunkSectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts ClientChunkManager.onLightUpdate() to prevent chunk re-renders
 * being scheduled due to light changes when lighting is disabled.
 */
@Mixin(ClientChunkManager.class)
public class ClientChunkManagerMixin {

    // method_12247 = onLightUpdate(LightType, ChunkSectionPos) -> void
    @Inject(method = "method_12247", at = @At("HEAD"), cancellable = true)
    private void colgemlight$onLightUpdate(LightType type, ChunkSectionPos pos, CallbackInfo ci) {
        if (ColgemLight.lightingDisabled) {
            ci.cancel();
        }
    }
}
