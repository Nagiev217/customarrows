package net.ferid.customarrows.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.ferid.customarrows.CustomArrowsMod;
import net.ferid.customarrows.entity.SlimeArrowEntity;
import net.ferid.customarrows.entity.WindArrowEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

/** Registers the two custom arrow items and hooks them into the vanilla Combat creative tab. */
public final class ModItems {

    private ModItems() {
    }

    public static final Item SLIME_ARROW = register("slime_arrow", settings ->
            new ArrowItem(settings) {
                @Override
                public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, ItemStack shotFrom) {
                    return new SlimeArrowEntity(world, shooter, stack, shotFrom);
                }
            });

    public static final Item WIND_ARROW = register("wind_arrow", settings ->
            new ArrowItem(settings) {
                @Override
                public PersistentProjectileEntity createArrow(World world, ItemStack stack, LivingEntity shooter, ItemStack shotFrom) {
                    return new WindArrowEntity(world, shooter, stack, shotFrom);
                }
            });

    private static Item register(String path, java.util.function.Function<Item.Settings, Item> factory) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(CustomArrowsMod.MOD_ID, path));
        Item item = factory.apply(new Item.Settings().registryKey(key));
        return Registry.register(Registries.ITEM, key, item);
    }

    /** Registers items and wires them into the Combat item group. Call once from the mod initializer. */
    public static void init() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(SLIME_ARROW);
            entries.add(WIND_ARROW);
        });
    }
}
