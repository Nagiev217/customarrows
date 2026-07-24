package net.ferid.customarrows.registry;

import net.ferid.customarrows.CustomArrowsMod;
import net.ferid.customarrows.entity.SlimeArrowEntity;
import net.ferid.customarrows.entity.WindArrowEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

/** Registers the custom projectile entity types backing our arrow items. */
public final class ModEntities {

    private ModEntities() {
    }

    public static final EntityType<SlimeArrowEntity> SLIME_ARROW = register(
            "slime_arrow",
            EntityType.Builder.<SlimeArrowEntity>create(SlimeArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
    );

    public static final EntityType<WindArrowEntity> WIND_ARROW = register(
            "wind_arrow",
            EntityType.Builder.<WindArrowEntity>create(WindArrowEntity::new, SpawnGroup.MISC)
                    .dimensions(EntityDimensions.fixed(0.5F, 0.5F))
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
    );

    private static <T extends net.minecraft.entity.Entity> EntityType<T> register(String path, EntityType.Builder<T> builder) {
        RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(CustomArrowsMod.MOD_ID, path));
        return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
    }

    public static void init() {
        // Static initializers above run on class load; this call just forces that to happen eagerly.
    }
}
