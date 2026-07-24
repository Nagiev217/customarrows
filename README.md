# Custom Arrows (Fabric, MC 1.21.11)

Adds two custom arrows:

- **Slime Arrow** — bounces (reflects) off solid blocks up to 5 times, losing 15-20%
  speed per bounce, then behaves like a normal arrow. Deals normal damage and is
  consumed on hitting an entity.
- **Wind Arrow** — on hitting a block or entity, detonates the same wind-burst
  explosion vanilla Wind Charges / the Breeze use (knockback only, no block damage),
  then disappears.

Both craft via standard shaped recipes, work with bows and crossbows, and appear in
the Combat creative tab.

## Project layout

```
src/main/java/net/ferid/customarrows/
  CustomArrowsMod.java          - main entrypoint, registers items/entities
  entity/SlimeArrowEntity.java  - bounce physics
  entity/WindArrowEntity.java   - wind-burst explosion
  registry/ModItems.java        - ArrowItem registration + creative tab
  registry/ModEntities.java     - EntityType registration
src/client/java/net/ferid/customarrows/client/
  CustomArrowsModClient.java    - renderer registration
  SlimeArrowEntityRenderer.java
  WindArrowEntityRenderer.java
src/main/resources/
  fabric.mod.json
  data/customarrows/recipe/*.json
  assets/customarrows/{items,models/item,textures,lang}/...
```

## Building

Requires JDK 21.

The Gradle wrapper's `gradle-wrapper.jar` binary isn't included in this checkout. If
you don't already have Gradle installed, either:

- open the project in IntelliJ IDEA (with the Fabric/Gradle plugins) and let it
  bootstrap the wrapper on import, or
- run `gradle wrapper` once with any local Gradle 8.x install to generate it.

Then:

```
./gradlew build
```

Run a client for testing:

```
./gradlew runClient
```

## Extending with a new arrow

1. Add an entity class next to `SlimeArrowEntity`/`WindArrowEntity` extending
   `net.minecraft.entity.projectile.ArrowEntity`, overriding whatever collision
   behaviour you need (`onBlockHit`, `onEntityHit`, or `onCollision`).
2. Register its `EntityType` in `ModEntities`.
3. Register an `ArrowItem` subclass overriding `createArrow(...)` to return your new
   entity, in `ModItems`.
4. Add a renderer (subclass `ArrowEntityRenderer`, override `getTexture`) and register
   it in `CustomArrowsModClient`.
5. Add textures, an item model + item definition, a recipe JSON, and a lang entry.

## Important: verify against your exact 1.21.11 mappings

This mod was written against the well-established 1.20.5-1.21.8 Yarn API shape,
since 1.21.11 mappings/build numbers weren't available to look up directly. Before
building, double-check these in `gradle.properties` (get exact numbers from
https://fabricmc.net/develop):

- `yarn_mappings`
- `loader_version`
- `fabric_version`

If the project fails to compile, the most likely culprits (things Mojang/Fabric
occasionally rename between minor releases) are:

- `PersistentProjectileEntity`/`ArrowEntity` constructor signatures
- `writeCustomData(WriteView)` / `readCustomData(ReadView)` (the NBT serialization
  API was replaced by this "View" system in late 1.21.x — used in
  `SlimeArrowEntity`)
- The exact `World#createExplosion(...)` overload used in `WindArrowEntity` — its
  parameter order/count has shifted before. Your IDE's autocomplete on
  `getWorld().createExplosion(` will show the current overloads.
- `WindChargeEntity.EXPLOSION_BEHAVIOR` (confirmed public/static as of 1.21.8; if
  it's been made private in 1.21.11, copy an equivalent `ExplosionBehavior` that
  returns `false` from block-destruction checks instead).

## Art

The included textures (`textures/item/*.png`, `textures/entity/projectiles/*.png`,
`assets/customarrows/icon.png`) are simple placeholder pixel art generated
programmatically so the mod has *something* unique to render. Replace them with real
art (e.g. via Blockbench or Aseprite) before shipping — item textures are 16x16,
entity textures are 32x32 to match vanilla arrow UV proportions.
