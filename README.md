# Colgem Light

A Minecraft 1.16.5 Fabric mod that completely disables lighting calculations to save CPU resources, and allows setting a uniform fixed brightness for all blocks.

## Features

- Disable all lighting engine calculations (sky light, block light, chunk re-renders)
- Set any fixed brightness level (0–15) for all blocks
- Works with **Sodium** (0.2.0)
- Client-side only — commands work in both singleplayer and multiplayer servers

## Commands

| Command | Description |
|---------|-------------|
| `/colgemlight on` | Disable lighting calculations (use fixed brightness) |
| `/colgemlight off` | Re-enable normal lighting |
| `/colgemlight set <0-15>` | Set the fixed brightness value (default: 15) |
| `/colgemlight refresh` | Force-reload all chunks to apply new light values |
| `/colgemlight` | Show current status |

**Aliases for `on`:** `enable`, `yes`, `y`, `true`, `1`  
**Aliases for `off`:** `disable`, `no`, `n`, `false`, `0`

> `on`/`off`/`set` automatically trigger a chunk refresh.

## Requirements

- Minecraft 1.16.5
- Fabric Loader ≥ 0.11.3
- Fabric API

## Download

See the [`releases/`](releases/) folder for the prebuilt jar.

## How It Works

Three layers of interception:

1. **`LightingProvider.getLight()`** — returns `fixedBrightness` (0–15) for all block positions
2. **`LightingProvider.checkBlock()` / `doLightUpdates()`** — skipped entirely; no CPU used on light recalculation
3. **`ClientChunkManager.onLightUpdate()`** — cancelled; prevents chunk dirty-marking from light events
4. **`ClonedChunkSection.getLightLevel()` (Sodium)** — Sodium bypasses vanilla light queries entirely; this mixin intercepts Sodium's own per-section light sampling

`/colgemlight refresh` calls `SodiumWorldRenderer.reload()` (if Sodium is present) and `WorldRenderer.reload()` to force a full chunk re-render with the new light values.
