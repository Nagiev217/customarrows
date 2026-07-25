package net.ferid.customarrows.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.ferid.customarrows.registry.ModEntities;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

/** Client-only entrypoint: wires our custom entity types to their renderers. */
public class CustomArrowsModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Render both arrows as their item icon in flight (same approach vanilla uses for
        // snowballs/eggs/ender pearls) - this reuses the item texture pipeline directly instead
        // of the pickier ArrowEntityRenderer texture-override path.
        EntityRendererRegistry.register(ModEntities.SLIME_ARROW, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.WIND_ARROW, FlyingItemEntityRenderer::new);
    }
}
