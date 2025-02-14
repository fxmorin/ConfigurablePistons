package ca.fxco.pistonlib;

import ca.fxco.pistonlib.base.ModBlockEntities;
import ca.fxco.pistonlib.helpers.PistonLibBehaviorManager;
import ca.fxco.pistonlib.renderers.MergeBlockEntityRenderer;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class PistonLibClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registering Other Block Entities
        BlockEntityRenderers.register(ModBlockEntities.MERGE_BLOCK_ENTITY, MergeBlockEntityRenderer::new);

        // Reset config when leaving server
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            PistonLib.getConfigManager().resetAllToDefault();
            PistonLibBehaviorManager.initOverrides(true);
        });
    }
}
