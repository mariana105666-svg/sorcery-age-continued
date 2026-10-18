package radon.jujutsu_kaisen.block.entity;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.block.JJKBlocks;

public class JJKBlockEntities {
   public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "jujutsu_kaisen");
   public static RegistryObject<BlockEntityType<DomainBlockEntity>> DOMAIN = BLOCK_ENTITIES.register(
      "domain",
      () -> Builder.of(
            DomainBlockEntity::new,
            new Block[]{
               (Block)JJKBlocks.DOMAIN.get(),
               (Block)JJKBlocks.DOMAIN_AIR.get(),
               (Block)JJKBlocks.INCOMPLETE_BARRIER.get(),
               (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_ONE.get(),
               (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_TWO.get(),
               (Block)JJKBlocks.COFFIN_OF_THE_IRON_MOUNTAIN_THREE.get(),
               (Block)JJKBlocks.HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL.get(),
               (Block)JJKBlocks.FAKE_WATER_DOMAIN.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FILL.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_FLOOR.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_ONE.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_TWO.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_THREE.get(),
               (Block)JJKBlocks.SHINING_SEA_OF_FLOWERS_DECORATION_FOUR.get(),
               (Block)JJKBlocks.SELF_EMBODIMENT_OF_PERFECTION.get(),
               (Block)JJKBlocks.DOMAIN_FILLER.get(),
               (Block)JJKBlocks.CHIMERA_SHADOW_GARDEN.get()
            }
         )
         .build(null)
   );
   public static RegistryObject<BlockEntityType<FakeSkyBlockEntity>> FAKE_SKY = BLOCK_ENTITIES.register(
      "fake_sky", () -> Builder.of(FakeSkyBlockEntity::new, new Block[]{(Block)JJKBlocks.FAKE_SKY.get()}).build(null)
   );
   public static RegistryObject<BlockEntityType<DomainSkyBlockEntity>> DOMAIN_SKY = BLOCK_ENTITIES.register(
      "domain_sky", () -> Builder.of(DomainSkyBlockEntity::new, new Block[]{(Block)JJKBlocks.DOMAIN_SKY.get()}).build(null)
   );
   public static RegistryObject<BlockEntityType<DisplayCaseBlockEntity>> DISPLAY_CASE = BLOCK_ENTITIES.register(
      "display_case", () -> Builder.of(DisplayCaseBlockEntity::new, new Block[]{(Block)JJKBlocks.DISPLAY_CASE.get()}).build(null)
   );
   public static RegistryObject<BlockEntityType<VeilRodBlockEntity>> VEIL_ROD = BLOCK_ENTITIES.register(
      "veil_rod", () -> Builder.of(VeilRodBlockEntity::new, new Block[]{(Block)JJKBlocks.VEIL_ROD.get()}).build(null)
   );
   public static RegistryObject<BlockEntityType<VeilBlockEntity>> VEIL = BLOCK_ENTITIES.register(
      "veil", () -> Builder.of(VeilBlockEntity::new, new Block[]{(Block)JJKBlocks.VEIL.get()}).build(null)
   );
   public static RegistryObject<BlockEntityType<DurationBlockEntity>> DURATION = BLOCK_ENTITIES.register(
      "duration",
      () -> Builder.of(DurationBlockEntity::new, new Block[]{(Block)JJKBlocks.FAKE_WATER_DURATION.get(), (Block)JJKBlocks.FAKE_WOOD.get()})
         .build(null)
   );
}
