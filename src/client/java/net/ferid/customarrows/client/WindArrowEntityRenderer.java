package net.ferid.customarrows.client;

import net.ferid.customarrows.CustomArrowsMod;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;

/** Renders the Wind Arrow using its own texture instead of the vanilla arrow texture. */
public class WindArrowEntityRenderer extends ArrowEntityRenderer {

    private static final Identifier TEXTURE =
            Identifier.of(CustomArrowsMod.MOD_ID, "textures/entity/projectiles/wind_arrow.png");

    public WindArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected Identifier getTexture(ArrowEntityRenderState state) {
        return TEXTURE;
    }
}
