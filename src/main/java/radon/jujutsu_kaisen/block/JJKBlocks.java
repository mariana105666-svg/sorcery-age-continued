package radon.jujutsu_kaisen.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.block.domain.ChimeraShadowGardenBlock;
import radon.jujutsu_kaisen.block.domain.DomainAirBlock;
import radon.jujutsu_kaisen.block.domain.DomainBlock;
import radon.jujutsu_kaisen.block.domain.DomainSkyBlock;
import radon.jujutsu_kaisen.block.domain.FakeSkyBlock;
import radon.jujutsu_kaisen.block.domain.FakeWaterDomainBlock;
import radon.jujutsu_kaisen.block.fluid.JJKFluids;

public class JJKBlocks {
   public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "jujutsu_kaisen");
   public static final RegistryObject<Block> METEOR = BLOCKS.register(
      "meteor",
      () -> new MagmaBlock(
         Properties.of()
            .mapColor(MapColor.NETHER)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .lightLevel(pState -> 3)
            .hasPostProcess(JJKBlocks::always)
            .emissiveRendering(JJKBlocks::always)
      )
   );
   public static RegistryObject<DomainBlock> DOMAIN = BLOCKS.register(
      "domain", () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> INCOMPLETE_BARRIER = BLOCKS.register(
      "incomplete_barrier", () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 4).noLootTable())
   );
   public static RegistryObject<DomainAirBlock> DOMAIN_AIR = BLOCKS.register(
      "domain_air", () -> new DomainAirBlock(Properties.of().noCollission().noLootTable().air())
   );
   public static RegistryObject<DomainBlock> FAKE_SKY = BLOCKS.register(
      "fake_sky", () -> new FakeSkyBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_ONE = BLOCKS.register(
      "coffin_of_the_iron_mountain_one",
      () -> new DomainBlock(
         Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).noLootTable().lightLevel(pState -> 14).emissiveRendering((pState, pLevel, pPos) -> true)
      )
   );
   public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_TWO = BLOCKS.register(
      "coffin_of_the_iron_mountain_two", () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).noLootTable())
   );
   public static RegistryObject<DomainBlock> COFFIN_OF_THE_IRON_MOUNTAIN_THREE = BLOCKS.register(
      "coffin_of_the_iron_mountain_three", () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).noLootTable())
   );
   public static RegistryObject<DomainBlock> HORIZON_OF_THE_CAPTIVATING_SKANDHA_FILL = BLOCKS.register(
      "horizon_of_the_captivating_skandha_fill",
      () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_FILL = BLOCKS.register(
      "shining_sea_of_flowers_fill",
      () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_FLOOR = BLOCKS.register(
      "shining_sea_of_flowers_floor",
      () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_DECORATION_ONE = BLOCKS.register(
      "shining_sea_of_flowers_decoration_one",
      () -> new DomainBlock(
         Properties.of()
            .noCollission()
            .strength(-1.0F, 8.0F)
            .isSuffocating(JJKBlocks::never)
            .noLootTable()
            .lightLevel(pState -> 14)
            .emissiveRendering((pState, pLevel, pPos) -> true)
      )
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_DECORATION_TWO = BLOCKS.register(
      "shining_sea_of_flowers_decoration_two",
      () -> new DomainBlock(
         Properties.of()
            .noCollission()
            .strength(-1.0F, 8.0F)
            .isSuffocating(JJKBlocks::never)
            .noLootTable()
            .lightLevel(pState -> 14)
            .emissiveRendering((pState, pLevel, pPos) -> true)
      )
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_DECORATION_THREE = BLOCKS.register(
      "shining_sea_of_flowers_decoration_three",
      () -> new DomainBlock(
         Properties.of()
            .noCollission()
            .strength(-1.0F, 8.0F)
            .isSuffocating(JJKBlocks::never)
            .noLootTable()
            .lightLevel(pState -> 14)
            .emissiveRendering((pState, pLevel, pPos) -> true)
      )
   );
   public static RegistryObject<DomainBlock> SHINING_SEA_OF_FLOWERS_DECORATION_FOUR = BLOCKS.register(
      "shining_sea_of_flowers_decoration_four",
      () -> new DomainBlock(
         Properties.of()
            .noCollission()
            .strength(-1.0F, 8.0F)
            .isSuffocating(JJKBlocks::never)
            .noLootTable()
            .lightLevel(pState -> 14)
            .emissiveRendering((pState, pLevel, pPos) -> true)
      )
   );
   public static RegistryObject<DomainBlock> SELF_EMBODIMENT_OF_PERFECTION = BLOCKS.register(
      "self_embodiment_of_perfection",
      () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<DomainBlock> DOMAIN_FILLER = BLOCKS.register(
      "domain_filler", () -> new DomainBlock(Properties.of().strength(-1.0F, 8.0F).isSuffocating(JJKBlocks::never).lightLevel(pState -> 14).noLootTable())
   );
   public static RegistryObject<ChimeraShadowGardenBlock> CHIMERA_SHADOW_GARDEN = BLOCKS.register(
      "chimera_shadow_garden", () -> new ChimeraShadowGardenBlock(JJKFluids.CHIMERA_SHADOW_GARDEN_SOURCE, Properties.copy(Blocks.WATER))
   );
   public static RegistryObject<Block> DISPLAY_CASE = BLOCKS.register(
      "display_case", () -> new DisplayCaseBlock(Properties.of().mapColor(MapColor.STONE))
   );
   public static RegistryObject<Block> ALTAR = BLOCKS.register(
      "altar", () -> new AltarBlock(Properties.of().mapColor(MapColor.METAL).requiresCorrectToolForDrops().strength(5.0F, 1200.0F).sound(SoundType.ANVIL))
   );
   public static RegistryObject<Block> VEIL_ROD = BLOCKS.register(
      "veil_rod",
      () -> new VeilRodBlock(Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(3.0F, 6.0F).sound(SoundType.COPPER).noOcclusion())
   );
   public static RegistryObject<Block> VEIL = BLOCKS.register(
      "veil",
      () -> new VeilBlock(
         Properties.of().mapColor(MapColor.STONE).strength(-1.0F, 3600000.0F).isViewBlocking(JJKBlocks::never).isSuffocating(JJKBlocks::never).noOcclusion()
      )
   );
   public static RegistryObject<Block> DOMAIN_SKY = BLOCKS.register(
      "domain_sky",
      () -> new DomainSkyBlock(
         Properties.of()
            .mapColor(MapColor.STONE)
            .strength(-1.0F, 12.0F)
            .isSuffocating(JJKBlocks::never)
            .lightLevel(pState -> 14)
            .noLootTable()
            .sound(SoundType.GLASS)
      )
   );
   public static RegistryObject<FakeWaterDurationBlock> FAKE_WATER_DURATION = BLOCKS.register(
      "fake_water_duration", () -> new FakeWaterDurationBlock(JJKFluids.FAKE_WATER_SOURCE, Properties.copy(Blocks.WATER))
   );
   public static RegistryObject<FakeWaterDomainBlock> FAKE_WATER_DOMAIN = BLOCKS.register(
      "fake_water_domain", () -> new FakeWaterDomainBlock(JJKFluids.FAKE_WATER_SOURCE, Properties.copy(Blocks.WATER))
   );
   public static RegistryObject<FakeWoodBlock> FAKE_WOOD = BLOCKS.register(
      "fake_wood",
      () -> new FakeWoodBlock(
         Properties.of().instrument(NoteBlockInstrument.BASS).strength(2.0F).sound(SoundType.WOOD).isSuffocating(JJKBlocks::never).ignitedByLava()
      )
   );
   public static RegistryObject<Block> LIMBO = BLOCKS.register(
      "limbo", () -> new Block(Properties.copy(Blocks.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(1000.0F, 6.0F))
   );

   private static boolean always(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return false;
   }

   private static boolean never(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return false;
   }

   @Nullable
   public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
      BlockEntityType<A> pServerType, BlockEntityType<E> pClientType, BlockEntityTicker<? super E> pTicker
   ) {
      return pClientType == pServerType ? (BlockEntityTicker<A>)pTicker : null;
   }
}
