package net.ferid.customarrows;

import net.fabricmc.api.ModInitializer;
import net.ferid.customarrows.registry.ModEntities;
import net.ferid.customarrows.registry.ModItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main mod entrypoint. Registers items and entity types.
 * <p>
 * To add another custom arrow: create an entity class next to
 * {@code SlimeArrowEntity}/{@code WindArrowEntity}, register its
 * {@code EntityType} in {@link ModEntities}, register an {@code ArrowItem}
 * for it in {@link ModItems}, and (client-side) register a renderer in
 * {@code CustomArrowsModClient}.
 */
public class CustomArrowsMod implements ModInitializer {

    public static final String MOD_ID = "customarrows";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.init();
        ModItems.init();

        LOGGER.info("Custom Arrows initialized: Slime Arrow and Wind Arrow are ready.");
    }
}
