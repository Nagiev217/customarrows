package net.ferid.customarrows.entity;

import net.ferid.customarrows.CustomArrowsMod;
import net.ferid.customarrows.registry.ModEntities;
import net.ferid.customarrows.registry.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * An arrow that ricochets off solid blocks instead of sticking in them.
 * <p>
 * Every bounce reflects the velocity across the hit surface's normal (like a rubber
 * ball) and removes 15-20% of its speed. After {@link #MAX_BOUNCES} bounces the arrow
 * falls back to vanilla behaviour and sticks in the next block it hits. Hitting a living
 * entity always deals normal arrow damage and consumes the arrow immediately.
 */
public class SlimeArrowEntity extends ArrowEntity {

    private static final int MAX_BOUNCES = 5;
    private static final double MIN_SPEED_LOSS = 0.15;
    private static final double MAX_SPEED_LOSS = 0.20;

    /** How many times this arrow has already bounced; persisted so it survives chunk reloads. */
    private int bounceCount = 0;

    private boolean loggedFirstTick = false;

    @Override
    public void tick() {
        super.tick();
        if (!this.loggedFirstTick) {
            this.loggedFirstTick = true;
            CustomArrowsMod.LOGGER.info("[DIAG] SlimeArrowEntity ticking, client={}, pos={}, vel={}, discarded={}",
                    this.getEntityWorld().isClient(), this.getEntityPos(), this.getVelocity(), this.isRemoved());
        }
    }

    public SlimeArrowEntity(EntityType<? extends SlimeArrowEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeArrowEntity(World world, LivingEntity owner, ItemStack stack, ItemStack shotFrom) {
        this(ModEntities.SLIME_ARROW, world);
        this.setOwner(owner);
        this.setStack(stack);
        // The bow/crossbow firing code repositions and launches the arrow via setVelocity(...)
        // right after createArrow() returns, so no manual position/rotation setup is needed here.
    }

    /**
     * Builds a bare arrow at a position with no owner set. Bows/crossbows/dispensers all
     * spawn projectiles through {@link net.minecraft.item.ProjectileItem#createEntity}, which
     * only gives a position (no shooter) - see {@link net.ferid.customarrows.registry.ModItems}.
     */
    public static SlimeArrowEntity create(World world, Position pos, ItemStack stack) {
        SlimeArrowEntity arrow = new SlimeArrowEntity(ModEntities.SLIME_ARROW, world);
        arrow.setPosition(pos.getX(), pos.getY(), pos.getZ());
        arrow.setStack(stack);
        return arrow;
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return new ItemStack(ModItems.SLIME_ARROW);
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        // Client side just mirrors what the server decides; the server owns bounce state.
        if (this.getEntityWorld().isClient() || this.bounceCount >= MAX_BOUNCES) {
            super.onBlockHit(blockHitResult);
            return;
        }

        this.bounceCount++;
        bounceOffSurface(blockHitResult);
    }

    /**
     * Reflects the arrow's velocity across the hit surface's normal and reduces its
     * speed, then repositions it just off the surface so it doesn't immediately
     * re-trigger a collision with the same block.
     */
    private void bounceOffSurface(BlockHitResult blockHitResult) {
        Direction hitSide = blockHitResult.getSide();
        Vec3d normal = Vec3d.of(hitSide.getVector());
        Vec3d velocity = this.getVelocity();

        Vec3d reflected = velocity.subtract(normal.multiply(2 * velocity.dotProduct(normal)));
        double speedLoss = MIN_SPEED_LOSS + this.random.nextDouble() * (MAX_SPEED_LOSS - MIN_SPEED_LOSS);
        reflected = reflected.multiply(1.0 - speedLoss);

        this.setPosition(blockHitResult.getPos().add(normal.multiply(0.05)));
        this.setVelocity(reflected);
        // Arrow rotation is recomputed from velocity every tick, so no manual yaw/pitch update is needed.

        this.playSound(SoundEvents.ENTITY_SLIME_SQUISH_SMALL, 0.6F, 1.0F + this.random.nextFloat() * 0.4F);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        view.putInt("BounceCount", this.bounceCount);
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);
        this.bounceCount = view.getInt("BounceCount", 0);
    }
}
