package net.ferid.customarrows.entity;

import net.ferid.customarrows.registry.ModEntities;
import net.ferid.customarrows.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.WindChargeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An arrow that detonates a vanilla Wind Charge burst on impact.
 * <p>
 * The explosion reuses {@link WindChargeEntity#EXPLOSION_BEHAVIOR}, the exact behaviour
 * vanilla uses for thrown Wind Charges and the Breeze's wind charge attack: it launches
 * and pushes nearby entities, never damages blocks, and uses the vanilla wind burst
 * particles/sound. The arrow is consumed immediately after the burst.
 */
public class WindArrowEntity extends ArrowEntity {

    /** Matches the explosion power of vanilla Wind Charges. */
    private static final float EXPLOSION_POWER = 1.2F;

    public WindArrowEntity(EntityType<? extends WindArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindArrowEntity(World world, double x, double y, double z, ItemStack stack, ItemStack shotFrom) {
        super(ModEntities.WIND_ARROW, x, y, z, world, stack, shotFrom);
    }

    public WindArrowEntity(World world, LivingEntity owner, ItemStack stack, ItemStack shotFrom) {
        super(ModEntities.WIND_ARROW, owner, world, stack, shotFrom);
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.WIND_ARROW);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        // Let vanilla resolve normal arrow behaviour first (entity damage, hit sound, etc.).
        super.onCollision(hitResult);

        if (!this.getWorld().isClient) {
            triggerWindBurst(hitResult.getPos());
            this.discard();
        }
    }

    /** Detonates the same wind burst explosion vanilla Wind Charges and Breezes use. */
    private void triggerWindBurst(Vec3d pos) {
        this.getWorld().createExplosion(
                this,
                null,
                WindChargeEntity.EXPLOSION_BEHAVIOR,
                pos.x, pos.y, pos.z,
                EXPLOSION_POWER,
                false,
                World.ExplosionSourceType.TRIGGER,
                ParticleTypes.GUST_EMITTER_SMALL,
                ParticleTypes.GUST_EMITTER_LARGE,
                SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST
        );
    }
}
