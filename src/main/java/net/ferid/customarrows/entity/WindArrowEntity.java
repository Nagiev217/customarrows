package net.ferid.customarrows.entity;

import net.ferid.customarrows.registry.ModEntities;
import net.ferid.customarrows.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An arrow that detonates a Wind Charge-style burst on impact.
 * <p>
 * The explosion pushes and launches nearby entities without damaging blocks (same as
 * vanilla Wind Charges/the Breeze), and plays the vanilla wind burst particles/sound.
 * The arrow is consumed immediately after the burst.
 */
public class WindArrowEntity extends ArrowEntity {

    /** Matches the explosion power of vanilla Wind Charges. */
    private static final float EXPLOSION_POWER = 1.2F;

    public WindArrowEntity(EntityType<? extends WindArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public WindArrowEntity(World world, LivingEntity owner, ItemStack stack, ItemStack shotFrom) {
        this(ModEntities.WIND_ARROW, world);
        this.setOwner(owner);
        this.setStack(stack);
        // The bow/crossbow firing code repositions and launches the arrow via setVelocity(...)
        // right after createArrow() returns, so no manual position/rotation setup is needed here.
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.WIND_ARROW);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        // Let vanilla resolve normal arrow behaviour first (entity damage, hit sound, etc.).
        super.onCollision(hitResult);

        if (this.getEntityWorld() instanceof ServerWorld serverWorld) {
            triggerWindBurst(serverWorld, hitResult.getPos());
            this.discard();
        }
    }

    /**
     * Detonates a knockback-only explosion (no block damage, matching Wind Charges) and
     * plays the vanilla wind burst particles/sound on top of it.
     */
    private void triggerWindBurst(ServerWorld world, Vec3d pos) {
        world.createExplosion(
                this,
                null,
                null,
                pos.x, pos.y, pos.z,
                EXPLOSION_POWER,
                false,
                World.ExplosionSourceType.TRIGGER
        );

        world.spawnParticles(ParticleTypes.GUST_EMITTER_LARGE, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
        world.playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_WIND_CHARGE_BURST, SoundCategory.NEUTRAL, 1.0F, 1.0F);
    }
}
