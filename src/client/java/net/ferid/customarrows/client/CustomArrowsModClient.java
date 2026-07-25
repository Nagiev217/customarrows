package net.ferid.customarrows.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ferid.customarrows.registry.ModEntities;

/** Client-only entrypoint: wires our custom entity types to their renderers. */
public class CustomArrowsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(ModEntities.SLIME_ARROW, SlimeArrowEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.WIND_ARROW, WindArrowEntityRenderer::new);
    }
}
