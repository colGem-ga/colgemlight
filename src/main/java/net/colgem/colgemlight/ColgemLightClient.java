package net.colgem.colgemlight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

@Environment(EnvType.CLIENT)
public class ColgemLightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("colgemlight")
                // /colgemlight <on|off|...>
                .then(ClientCommandManager.argument("state", StringArgumentType.word())
                    .executes(ctx -> {
                        String state = StringArgumentType.getString(ctx, "state").toLowerCase();
                        FabricClientCommandSource src = ctx.getSource();
                        if (isOn(state)) {
                            ColgemLight.lightingDisabled = true;
                            reloadChunks();
                            src.sendFeedback(new LiteralText(
                                "\u00a7a[ColgemLight]\u00a7r Lighting DISABLED. Fixed brightness: \u00a7e"
                                + ColgemLight.fixedBrightness));
                            return 1;
                        } else if (isOff(state)) {
                            ColgemLight.lightingDisabled = false;
                            reloadChunks();
                            src.sendFeedback(new LiteralText(
                                "\u00a7a[ColgemLight]\u00a7r Lighting ENABLED (normal)."));
                            return 1;
                        } else {
                            src.sendError(new LiteralText(
                                "[ColgemLight] Unknown value '" + state
                                + "'. Use: on/off/enable/disable/yes/no/y/n/true/false/1/0"));
                            return 0;
                        }
                    })
                )
                // /colgemlight set <0-15>
                .then(ClientCommandManager.literal("set")
                    .then(ClientCommandManager.argument("brightness", IntegerArgumentType.integer(0, 15))
                        .executes(ctx -> {
                            int val = IntegerArgumentType.getInteger(ctx, "brightness");
                            ColgemLight.fixedBrightness = val;
                            if (ColgemLight.lightingDisabled) {
                                reloadChunks();
                            }
                            ctx.getSource().sendFeedback(new LiteralText(
                                "\u00a7a[ColgemLight]\u00a7r Fixed brightness set to \u00a7e" + val
                                + "\u00a7r" + (ColgemLight.lightingDisabled
                                    ? " (chunks reloaded)."
                                    : " (applies when turned ON).")));
                            return 1;
                        })
                    )
                )
                // /colgemlight refresh
                .then(ClientCommandManager.literal("refresh")
                    .executes(ctx -> {
                        reloadChunks();
                        ctx.getSource().sendFeedback(new LiteralText(
                            "\u00a7a[ColgemLight]\u00a7r Chunk lighting refreshed."));
                        return 1;
                    })
                )
                // /colgemlight (no args - show status)
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(new LiteralText(
                        "\u00a7a[ColgemLight]\u00a7r Status: "
                        + (ColgemLight.lightingDisabled
                            ? "\u00a7cON\u00a7r (lighting disabled)"
                            : "\u00a72OFF\u00a7r (normal lighting)")
                        + " | Fixed brightness: \u00a7e" + ColgemLight.fixedBrightness
                        + "\n\u00a77Commands: on/off/set <0-15>/refresh"));
                    return 1;
                })
        );
    }

    /**
     * Forces all chunks to be re-rendered so the new light values take effect immediately.
     * Tries Sodium's SodiumWorldRenderer.reload() first (bypasses vanilla renderer),
     * then falls back to vanilla WorldRenderer.reload().
     */
    public static void reloadChunks() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.worldRenderer == null) return;

        // Try Sodium first via reflection (Sodium replaces the chunk renderer)
        boolean sodiumReloaded = false;
        try {
            Class<?> sodiumRendererClass = Class.forName(
                "me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer");
            Object sodiumRenderer = sodiumRendererClass.getMethod("getInstance")
                .invoke(null);
            if (sodiumRenderer != null) {
                sodiumRendererClass.getMethod("reload").invoke(sodiumRenderer);
                sodiumReloaded = true;
            }
        } catch (Exception ignored) {
            // Sodium not present or method changed - fall through to vanilla
        }

        // Always also reload vanilla WorldRenderer (handles entity rendering etc.)
        mc.worldRenderer.reload();
    }

    private static boolean isOn(String s) {
        return s.equals("on") || s.equals("enable") || s.equals("yes")
            || s.equals("y") || s.equals("true") || s.equals("1");
    }

    private static boolean isOff(String s) {
        return s.equals("off") || s.equals("disable") || s.equals("no")
            || s.equals("n") || s.equals("false") || s.equals("0");
    }
}
