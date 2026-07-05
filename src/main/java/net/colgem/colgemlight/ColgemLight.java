package net.colgem.colgemlight;

import net.fabricmc.api.ModInitializer;

public class ColgemLight implements ModInitializer {

    public static final String MOD_ID = "colgemlight";

    /** Whether lighting calculations are disabled (on = disabled). */
    public static volatile boolean lightingDisabled = false;

    /** Fixed brightness level (0-15) used when lighting is disabled. */
    public static volatile int fixedBrightness = 15;

    @Override
    public void onInitialize() {
        // Nothing to do server-side; commands are registered client-side in ColgemLightClient
    }
}
