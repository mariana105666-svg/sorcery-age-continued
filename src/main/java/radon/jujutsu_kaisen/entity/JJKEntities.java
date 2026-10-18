package radon.jujutsu_kaisen.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.entity.curse.BirdCurseEntity;
import radon.jujutsu_kaisen.entity.curse.CyclopsCurseEntity;
import radon.jujutsu_kaisen.entity.curse.DagonEntity;
import radon.jujutsu_kaisen.entity.curse.DinoCurseEntity;
import radon.jujutsu_kaisen.entity.curse.FelineCurseEntity;
import radon.jujutsu_kaisen.entity.curse.FingerBearerEntity;
import radon.jujutsu_kaisen.entity.curse.FishCurseEntity;
import radon.jujutsu_kaisen.entity.curse.FuglyCurseEntity;
import radon.jujutsu_kaisen.entity.curse.HanamiEntity;
import radon.jujutsu_kaisen.entity.curse.JogoEntity;
import radon.jujutsu_kaisen.entity.curse.JogoatEntity;
import radon.jujutsu_kaisen.entity.curse.KuchisakeOnnaEntity;
import radon.jujutsu_kaisen.entity.curse.RainbowDragonEntity;
import radon.jujutsu_kaisen.entity.curse.RikaEntity;
import radon.jujutsu_kaisen.entity.curse.RugbyFieldCurseEntity;
import radon.jujutsu_kaisen.entity.curse.WormCurseEntity;
import radon.jujutsu_kaisen.entity.curse.ZombaCurseEntity;
import radon.jujutsu_kaisen.entity.effect.AirFrameEntity;
import radon.jujutsu_kaisen.entity.effect.BlackFlashEntity;
import radon.jujutsu_kaisen.entity.effect.BodyRepelEntity;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBlastEntity;
import radon.jujutsu_kaisen.entity.effect.CursedEnergyBombEntity;
import radon.jujutsu_kaisen.entity.effect.DisasterPlantEntity;
import radon.jujutsu_kaisen.entity.effect.ElectricBeamEntity;
import radon.jujutsu_kaisen.entity.effect.FireBeamEntity;
import radon.jujutsu_kaisen.entity.effect.ForestDashEntity;
import radon.jujutsu_kaisen.entity.effect.ForestRootsEntity;
import radon.jujutsu_kaisen.entity.effect.ForestSpikeEntity;
import radon.jujutsu_kaisen.entity.effect.ForestWaveEntity;
import radon.jujutsu_kaisen.entity.effect.GraniteBlastEntity;
import radon.jujutsu_kaisen.entity.effect.GraniteBlastVolleyEntity;
import radon.jujutsu_kaisen.entity.effect.GraniteRingEntity;
import radon.jujutsu_kaisen.entity.effect.JacobsLadderEntity;
import radon.jujutsu_kaisen.entity.effect.LightningEntity;
import radon.jujutsu_kaisen.entity.effect.MaximumGraniteBeamEntity;
import radon.jujutsu_kaisen.entity.effect.MaximumOutputJacobsLadderEntity;
import radon.jujutsu_kaisen.entity.effect.MeteorEntity;
import radon.jujutsu_kaisen.entity.effect.PiercingWaterEntity;
import radon.jujutsu_kaisen.entity.effect.ProjectionFrameEntity;
import radon.jujutsu_kaisen.entity.effect.PureLoveBeamEntity;
import radon.jujutsu_kaisen.entity.effect.ScissorEntity;
import radon.jujutsu_kaisen.entity.effect.SkyStrikeEntity;
import radon.jujutsu_kaisen.entity.effect.SpiderwebEntity;
import radon.jujutsu_kaisen.entity.effect.VolcanoEntity;
import radon.jujutsu_kaisen.entity.effect.WaterTorrentEntity;
import radon.jujutsu_kaisen.entity.effect.WaterballEntity;
import radon.jujutsu_kaisen.entity.effect.WoodSegmentEntity;
import radon.jujutsu_kaisen.entity.effect.WoodShieldEntity;
import radon.jujutsu_kaisen.entity.effect.WoodShieldSegmentEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.PolymorphicSoulIsomerEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulLargeEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulNormalEntity;
import radon.jujutsu_kaisen.entity.idle_transfiguration.TransfiguredSoulSmallEntity;
import radon.jujutsu_kaisen.entity.projectile.BlueProjectile;
import radon.jujutsu_kaisen.entity.projectile.CursedBudProjectile;
import radon.jujutsu_kaisen.entity.projectile.CursedEnergyImbuedItemProjectile;
import radon.jujutsu_kaisen.entity.projectile.DismantleProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelGrappleProjectile;
import radon.jujutsu_kaisen.entity.projectile.EelShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.EmberInsectProjectile;
import radon.jujutsu_kaisen.entity.projectile.FilmGaugeProjectile;
import radon.jujutsu_kaisen.entity.projectile.FireArrowProjectile;
import radon.jujutsu_kaisen.entity.projectile.FireballProjectile;
import radon.jujutsu_kaisen.entity.projectile.HollowPurpleProjectile;
import radon.jujutsu_kaisen.entity.projectile.LavaRockProjectile;
import radon.jujutsu_kaisen.entity.projectile.MaximumUzumakiProjectile;
import radon.jujutsu_kaisen.entity.projectile.MiniUzumakiProjectile;
import radon.jujutsu_kaisen.entity.projectile.PiranhaShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.RedProjectile;
import radon.jujutsu_kaisen.entity.projectile.SharkShikigamiProjectile;
import radon.jujutsu_kaisen.entity.projectile.ThrownChainProjectile;
import radon.jujutsu_kaisen.entity.projectile.ToadTongueProjectile;
import radon.jujutsu_kaisen.entity.projectile.TransfiguredSoulProjectile;
import radon.jujutsu_kaisen.entity.projectile.WorldSlashProjectile;
import radon.jujutsu_kaisen.entity.sorcerer.AoiTodoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.HajimeKashimoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.HeianSukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MakiZeninEntity;
import radon.jujutsu_kaisen.entity.sorcerer.MegumiFushiguroEntity;
import radon.jujutsu_kaisen.entity.sorcerer.NaoyaZeninEntity;
import radon.jujutsu_kaisen.entity.sorcerer.RyuIshigoriEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SatoruGojoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SuguruGetoEntity;
import radon.jujutsu_kaisen.entity.sorcerer.SukunaEntity;
import radon.jujutsu_kaisen.entity.sorcerer.TogeInumakiEntity;
import radon.jujutsu_kaisen.entity.sorcerer.TojiFushiguroEntity;
import radon.jujutsu_kaisen.entity.sorcerer.YujiItadoriEntity;
import radon.jujutsu_kaisen.entity.sorcerer.YutaOkkotsuEntity;
import radon.jujutsu_kaisen.entity.sorcerer.base.SorcererEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.AgitoEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogBlackEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogTotalityEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.DivineDogWhiteEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.GreatSerpentEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MahoragaEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.MaxElephantEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.NueEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.NueTotalityEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.PiercingBullEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.RabbitEscapeEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.ToadFusionEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.TranquilDeerEntity;
import radon.jujutsu_kaisen.entity.ten_shadows.WheelEntity;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.MOD
)
public class JJKEntities {
   public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "jujutsu_kaisen");
   public static RegistryObject<EntityType<ClosedDomainExpansionEntity>> CLOSED_DOMAIN_EXPANSION = ENTITIES.register(
      "closed_domain_expansion",
      () -> Builder.<ClosedDomainExpansionEntity>of(ClosedDomainExpansionEntity::new, MobCategory.MISC)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "closed_domain_expansion").toString())
   );
   public static RegistryObject<EntityType<MalevolentShrineClosedEntity>> MALEVOLENT_SHRINE_CLOSED = ENTITIES.register(
      "malevolent_shrine_closed",
      () -> Builder.<MalevolentShrineClosedEntity>of(MalevolentShrineClosedEntity::new, MobCategory.MISC)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "malevolent_shrine_closed").toString())
   );
   public static RegistryObject<EntityType<MalevolentShrineEntity>> MALEVOLENT_SHRINE = ENTITIES.register(
      "malevolent_shrine",
      () -> Builder.<MalevolentShrineEntity>of(MalevolentShrineEntity::new, MobCategory.MISC)
         .sized(8.4F, 9.6F)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "malevolent_shrine").toString())
   );
   public static RegistryObject<EntityType<ChimeraShadowGardenEntity>> CHIMERA_SHADOW_GARDEN = ENTITIES.register(
      "chimera_shadow_garden",
      () -> Builder.<ChimeraShadowGardenEntity>of(ChimeraShadowGardenEntity::new, MobCategory.MISC)
         .sized(3.0F, 4.0F)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "chimera_shadow_garden").toString())
   );
   public static RegistryObject<EntityType<DomainBarrierEntity>> DOMAIN_BARRIER = ENTITIES.register(
      "domain_barrier",
      () -> Builder.<DomainBarrierEntity>of(DomainBarrierEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "domain_barrier").toString())
   );
   public static RegistryObject<EntityType<IncompleteDomainEntity>> INCOMPLETE_DOMAIN = ENTITIES.register(
      "incomplete_domain",
      () -> Builder.<IncompleteDomainEntity>of(IncompleteDomainEntity::new, MobCategory.MISC)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "incomplete_domain").toString())
   );
   public static RegistryObject<EntityType<GenericOpenDomainEntity>> GENERIC_OPEN_DOMAIN = ENTITIES.register(
      "generic_open_domain",
      () -> Builder.<GenericOpenDomainEntity>of(GenericOpenDomainEntity::new, MobCategory.MISC)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "generic_open_domain").toString())
   );
   public static RegistryObject<EntityType<VeilEntity>> VEIL = ENTITIES.register(
      "veil", () -> Builder.<VeilEntity>of(VeilEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "veil").toString())
   );
   public static RegistryObject<EntityType<JogoEntity>> JOGO = ENTITIES.register(
      "jogo",
      () -> Builder.<JogoEntity>of(JogoEntity::new, MobCategory.CREATURE).sized(0.9F, 1.9F).build(new ResourceLocation("jujutsu_kaisen", "jogo").toString())
   );
   public static RegistryObject<EntityType<JogoatEntity>> JOGOAT = ENTITIES.register(
      "jogoat", () -> Builder.<JogoatEntity>of(JogoatEntity::new, MobCategory.CREATURE).build(new ResourceLocation("jujutsu_kaisen", "jogoat").toString())
   );
   public static RegistryObject<EntityType<DagonEntity>> DAGON = ENTITIES.register(
      "dagon",
      () -> Builder.<DagonEntity>of(DagonEntity::new, MobCategory.CREATURE).sized(1.4F, 3.0F).build(new ResourceLocation("jujutsu_kaisen", "dagon").toString())
   );
   public static RegistryObject<EntityType<HanamiEntity>> HANAMI = ENTITIES.register(
      "hanami",
      () -> Builder.<HanamiEntity>of(HanamiEntity::new, MobCategory.CREATURE)
         .sized(1.4F, 3.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "hanami").toString())
   );
   public static RegistryObject<EntityType<RugbyFieldCurseEntity>> RUGBY_FIELD_CURSE = ENTITIES.register(
      "rugby_field_curse",
      () -> Builder.<RugbyFieldCurseEntity>of(RugbyFieldCurseEntity::new, MobCategory.AMBIENT)
         .sized(2.0F, 1.8F)
         .build(new ResourceLocation("jujutsu_kaisen", "rugby_field_curse").toString())
   );
   public static RegistryObject<EntityType<FishCurseEntity>> FISH_CURSE = ENTITIES.register(
      "fish_curse",
      () -> Builder.<FishCurseEntity>of(FishCurseEntity::new, MobCategory.AMBIENT)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "fish_curse").toString())
   );
   public static RegistryObject<EntityType<CyclopsCurseEntity>> CYCLOPS_CURSE = ENTITIES.register(
      "cyclops_curse",
      () -> Builder.<CyclopsCurseEntity>of(CyclopsCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.6F, 6.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "cyclops_curse").toString())
   );
   public static RegistryObject<EntityType<KuchisakeOnnaEntity>> KUCHISAKE_ONNA = ENTITIES.register(
      "kuchisake_onna",
      () -> Builder.<KuchisakeOnnaEntity>of(KuchisakeOnnaEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "kuchisake_onna").toString())
   );
   public static RegistryObject<EntityType<ZombaCurseEntity>> ZOMBA_CURSE = ENTITIES.register(
      "zomba_curse",
      () -> Builder.<ZombaCurseEntity>of(ZombaCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.2F, 2.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "zomba_curse").toString())
   );
   public static RegistryObject<EntityType<WormCurseEntity>> WORM_CURSE = ENTITIES.register(
      "worm_curse",
      () -> Builder.<WormCurseEntity>of(WormCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.1875F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "worm_curse").toString())
   );
   public static RegistryObject<EntityType<FelineCurseEntity>> FELINE_CURSE = ENTITIES.register(
      "feline_curse",
      () -> Builder.<FelineCurseEntity>of(FelineCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.6F, 1.8F)
         .build(new ResourceLocation("jujutsu_kaisen", "feline_curse").toString())
   );
   public static RegistryObject<EntityType<FuglyCurseEntity>> FUGLY_CURSE = ENTITIES.register(
      "fugly_curse",
      () -> Builder.<FuglyCurseEntity>of(FuglyCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.6F, 2.4F)
         .build(new ResourceLocation("jujutsu_kaisen", "fugly_curse").toString())
   );
   public static RegistryObject<EntityType<BirdCurseEntity>> BIRD_CURSE = ENTITIES.register(
      "bird_curse",
      () -> Builder.<BirdCurseEntity>of(BirdCurseEntity::new, MobCategory.AMBIENT)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "bird_curse").toString())
   );
   public static RegistryObject<EntityType<FingerBearerEntity>> FINGER_BEARER = ENTITIES.register(
      "finger_bearer",
      () -> Builder.<FingerBearerEntity>of(FingerBearerEntity::new, MobCategory.AMBIENT)
         .sized(1.4F, 3.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "finger_bearer").toString())
   );
   public static RegistryObject<EntityType<RainbowDragonEntity>> RAINBOW_DRAGON = ENTITIES.register(
      "rainbow_dragon",
      () -> Builder.<RainbowDragonEntity>of(RainbowDragonEntity::new, MobCategory.AMBIENT)
         .sized(1.5F, 1.125F)
         .build(new ResourceLocation("jujutsu_kaisen", "rainbow_dragon").toString())
   );
   public static RegistryObject<EntityType<DinoCurseEntity>> DINO_CURSE = ENTITIES.register(
      "dino_curse",
      () -> Builder.<DinoCurseEntity>of(DinoCurseEntity::new, MobCategory.AMBIENT)
         .sized(4.0F, 3.6F)
         .build(new ResourceLocation("jujutsu_kaisen", "dino_curse").toString())
   );
   public static RegistryObject<EntityType<AbsorbedPlayerEntity>> ABSORBED_PLAYER = ENTITIES.register(
      "absorbed_player",
      () -> Builder.<AbsorbedPlayerEntity>of(AbsorbedPlayerEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "absorbed_player").toString())
   );
   public static RegistryObject<EntityType<LimboCloneEntity>> LIMBO_CLONE = ENTITIES.register(
      "limbo_clone",
      () -> Builder.<LimboCloneEntity>of(LimboCloneEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "limbo_clone").toString())
   );
   public static RegistryObject<EntityType<SukunaEntity>> SUKUNA = ENTITIES.register(
      "sukuna", () -> Builder.<SukunaEntity>of(SukunaEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "sukuna").toString())
   );
   public static RegistryObject<EntityType<HeianSukunaEntity>> HEIAN_SUKUNA = ENTITIES.register(
      "heian_sukuna",
      () -> Builder.<HeianSukunaEntity>of(HeianSukunaEntity::new, MobCategory.MISC)
         .sized(1.0F, 2.9F)
         .build(new ResourceLocation("jujutsu_kaisen", "heian_sukuna").toString())
   );
   public static RegistryObject<EntityType<TojiFushiguroEntity>> TOJI_FUSHIGURO = ENTITIES.register(
      "toji_fushiguro",
      () -> Builder.<TojiFushiguroEntity>of(TojiFushiguroEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "toji_fushiguro").toString())
   );
   public static RegistryObject<EntityType<SatoruGojoEntity>> SATORU_GOJO = ENTITIES.register(
      "satoru_gojo",
      () -> Builder.<SatoruGojoEntity>of(SatoruGojoEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "satoru_gojo").toString())
   );
   public static RegistryObject<EntityType<YutaOkkotsuEntity>> YUTA_OKKOTSU = ENTITIES.register(
      "yuta_okkotsu",
      () -> Builder.<YutaOkkotsuEntity>of(YutaOkkotsuEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "yuta_okkotsu").toString())
   );
   public static RegistryObject<EntityType<MegumiFushiguroEntity>> MEGUMI_FUSHIGURO = ENTITIES.register(
      "megumi_fushiguro",
      () -> Builder.<MegumiFushiguroEntity>of(MegumiFushiguroEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "megumi_fushiguro").toString())
   );
   public static RegistryObject<EntityType<YujiItadoriEntity>> YUJI_ITADORI = ENTITIES.register(
      "yuji_itadori",
      () -> Builder.<YujiItadoriEntity>of(YujiItadoriEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "yuji_itadori").toString())
   );
   public static RegistryObject<EntityType<TogeInumakiEntity>> TOGE_INUMAKI = ENTITIES.register(
      "toge_inumaki",
      () -> Builder.<TogeInumakiEntity>of(TogeInumakiEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "toge_inumaki").toString())
   );
   public static RegistryObject<EntityType<SuguruGetoEntity>> SUGURU_GETO = ENTITIES.register(
      "suguru_geto",
      () -> Builder.<SuguruGetoEntity>of(SuguruGetoEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "suguru_geto").toString())
   );
   public static RegistryObject<EntityType<NaoyaZeninEntity>> NAOYA_ZENIN = ENTITIES.register(
      "naoya_zenin",
      () -> Builder.<NaoyaZeninEntity>of(NaoyaZeninEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "naoya_zenin").toString())
   );
   public static RegistryObject<EntityType<RyuIshigoriEntity>> RYU_ISHIGORI = ENTITIES.register(
      "ryu_ishigori",
      () -> Builder.<RyuIshigoriEntity>of(RyuIshigoriEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "ryu_ishigori").toString())
   );
   public static RegistryObject<EntityType<HajimeKashimoEntity>> HAJIME_KASHIMO = ENTITIES.register(
      "hajime_kashimo",
      () -> Builder.<HajimeKashimoEntity>of(HajimeKashimoEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "hajime_kashimo").toString())
   );
   public static RegistryObject<EntityType<MakiZeninEntity>> MAKI_ZENIN = ENTITIES.register(
      "maki_zenin", () -> Builder.<MakiZeninEntity>of(MakiZeninEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "maki_zenin").toString())
   );
   public static RegistryObject<EntityType<AoiTodoEntity>> AOI_TODO = ENTITIES.register(
      "aoi_todo", () -> Builder.<AoiTodoEntity>of(AoiTodoEntity::new, MobCategory.AMBIENT).build(new ResourceLocation("jujutsu_kaisen", "aoi_todo").toString())
   );
   public static RegistryObject<EntityType<RikaEntity>> RIKA = ENTITIES.register(
      "rika",
      () -> Builder.<RikaEntity>of(RikaEntity::new, MobCategory.MISC).sized(1.4F, 3.875F).build(new ResourceLocation("jujutsu_kaisen", "rika").toString())
   );
   public static RegistryObject<EntityType<MahoragaEntity>> MAHORAGA = ENTITIES.register(
      "mahoraga",
      () -> Builder.<MahoragaEntity>of(MahoragaEntity::new, MobCategory.MISC)
         .sized(1.4F, 5.6F)
         .build(new ResourceLocation("jujutsu_kaisen", "mahoraga").toString())
   );
   public static RegistryObject<EntityType<DivineDogWhiteEntity>> DIVINE_DOG_WHITE = ENTITIES.register(
      "divine_dog_white",
      () -> Builder.<DivineDogWhiteEntity>of(DivineDogWhiteEntity::new, MobCategory.MISC)
         .sized(0.8F, 1.3F)
         .build(new ResourceLocation("jujutsu_kaisen", "divine_dog_white").toString())
   );
   public static RegistryObject<EntityType<DivineDogBlackEntity>> DIVINE_DOG_BLACK = ENTITIES.register(
      "divine_dog_black",
      () -> Builder.<DivineDogBlackEntity>of(DivineDogBlackEntity::new, MobCategory.MISC)
         .sized(0.8F, 1.3F)
         .build(new ResourceLocation("jujutsu_kaisen", "divine_dog_black").toString())
   );
   public static RegistryObject<EntityType<DivineDogTotalityEntity>> DIVINE_DOG_TOTALITY = ENTITIES.register(
      "divine_dog_totality",
      () -> Builder.<DivineDogTotalityEntity>of(DivineDogTotalityEntity::new, MobCategory.MISC)
         .sized(1.6F, 2.6F)
         .build(new ResourceLocation("jujutsu_kaisen", "divine_dog_totality").toString())
   );
   public static RegistryObject<EntityType<ToadEntity>> TOAD = ENTITIES.register(
      "toad",
      () -> Builder.<ToadEntity>of(ToadEntity::new, MobCategory.MISC).sized(0.8F, 1.2F).build(new ResourceLocation("jujutsu_kaisen", "toad").toString())
   );
   public static RegistryObject<EntityType<ToadFusionEntity>> TOAD_FUSION = ENTITIES.register(
      "toad_fusion",
      () -> Builder.<ToadFusionEntity>of(ToadFusionEntity::new, MobCategory.MISC)
         .sized(0.8F, 1.2F)
         .build(new ResourceLocation("jujutsu_kaisen", "toad_fusion").toString())
   );
   public static RegistryObject<EntityType<ToadTongueProjectile>> TOAD_TONGUE = ENTITIES.register(
      "toad_tongue",
      () -> Builder.<ToadTongueProjectile>of(ToadTongueProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "toad_tongue").toString())
   );
   public static RegistryObject<EntityType<RabbitEscapeEntity>> RABBIT_ESCAPE = ENTITIES.register(
      "rabbit_escape",
      () -> Builder.<RabbitEscapeEntity>of(RabbitEscapeEntity::new, MobCategory.MISC)
         .sized(0.4F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "rabbit_escape").toString())
   );
   public static RegistryObject<EntityType<NueEntity>> NUE = ENTITIES.register(
      "nue", () -> Builder.<NueEntity>of(NueEntity::new, MobCategory.MISC).sized(0.9F, 2.6F).build(new ResourceLocation("jujutsu_kaisen", "nue").toString())
   );
   public static RegistryObject<EntityType<NueTotalityEntity>> NUE_TOTALITY = ENTITIES.register(
      "nue_totality",
      () -> Builder.<NueTotalityEntity>of(NueTotalityEntity::new, MobCategory.MISC)
         .sized(4.0F, 10.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "nue_totality").toString())
   );
   public static RegistryObject<EntityType<GreatSerpentEntity>> GREAT_SERPENT = ENTITIES.register(
      "great_serpent",
      () -> Builder.<GreatSerpentEntity>of(GreatSerpentEntity::new, MobCategory.MISC)
         .sized(1.4375F, 0.8125F)
         .build(new ResourceLocation("jujutsu_kaisen", "great_serpent").toString())
   );
   public static RegistryObject<EntityType<MaxElephantEntity>> MAX_ELEPHANT = ENTITIES.register(
      "max_elephant",
      () -> Builder.<MaxElephantEntity>of(MaxElephantEntity::new, MobCategory.MISC)
         .sized(3.8F, 3.6F)
         .build(new ResourceLocation("jujutsu_kaisen", "max_elephant").toString())
   );
   public static RegistryObject<EntityType<TranquilDeerEntity>> TRANQUIL_DEER = ENTITIES.register(
      "tranquil_deer",
      () -> Builder.<TranquilDeerEntity>of(TranquilDeerEntity::new, MobCategory.MISC)
         .sized(3.8F, 3.6F)
         .build(new ResourceLocation("jujutsu_kaisen", "tranquil_deer").toString())
   );
   public static RegistryObject<EntityType<PiercingBullEntity>> PIERCING_BULL = ENTITIES.register(
      "piercing_bull",
      () -> Builder.<PiercingBullEntity>of(PiercingBullEntity::new, MobCategory.MISC)
         .sized(2.0F, 1.8F)
         .build(new ResourceLocation("jujutsu_kaisen", "piercing_bull").toString())
   );
   public static RegistryObject<EntityType<AgitoEntity>> AGITO = ENTITIES.register(
      "agito",
      () -> Builder.<AgitoEntity>of(AgitoEntity::new, MobCategory.MISC).sized(1.6F, 4.0F).build(new ResourceLocation("jujutsu_kaisen", "agito").toString())
   );
   public static RegistryObject<EntityType<TransfiguredSoulSmallEntity>> TRANSFIGURED_SOUL_SMALL = ENTITIES.register(
      "transfigured_soul_small",
      () -> Builder.<TransfiguredSoulSmallEntity>of(TransfiguredSoulSmallEntity::new, MobCategory.MISC)
         .sized(0.6F, 1.2F)
         .build(new ResourceLocation("jujutsu_kaisen", "transfigured_soul_small").toString())
   );
   public static RegistryObject<EntityType<TransfiguredSoulNormalEntity>> TRANSFIGURED_SOUL_NORMAL = ENTITIES.register(
      "transfigured_soul_normal",
      () -> Builder.<TransfiguredSoulNormalEntity>of(TransfiguredSoulNormalEntity::new, MobCategory.MISC)
         .build(new ResourceLocation("jujutsu_kaisen", "transfigured_soul_normal").toString())
   );
   public static RegistryObject<EntityType<TransfiguredSoulLargeEntity>> TRANSFIGURED_SOUL_LARGE = ENTITIES.register(
      "transfigured_soul_large",
      () -> Builder.<TransfiguredSoulLargeEntity>of(TransfiguredSoulLargeEntity::new, MobCategory.MISC)
         .sized(1.4F, 2.7F)
         .build(new ResourceLocation("jujutsu_kaisen", "transfigured_soul_large").toString())
   );
   public static RegistryObject<EntityType<PolymorphicSoulIsomerEntity>> POLYMORPHIC_SOUL_ISOMER = ENTITIES.register(
      "polymorphic_soul_isomer",
      () -> Builder.<PolymorphicSoulIsomerEntity>of(PolymorphicSoulIsomerEntity::new, MobCategory.MISC)
         .build(new ResourceLocation("jujutsu_kaisen", "polymorphic_soul_isomer").toString())
   );
   public static RegistryObject<EntityType<SimpleDomainEntity>> SIMPLE_DOMAIN = ENTITIES.register(
      "simple_domain",
      () -> Builder.<SimpleDomainEntity>of(SimpleDomainEntity::new, MobCategory.MISC)
         .sized(1.5F, 1.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "simple_domain").toString())
   );
   public static RegistryObject<EntityType<HollowWickerBasketEntity>> HOLLOW_WICKER_BASKET = ENTITIES.register(
      "hollow_wicker_basket",
      () -> Builder.<HollowWickerBasketEntity>of(HollowWickerBasketEntity::new, MobCategory.MISC)
         .sized(1.25F, 1.25F)
         .build(new ResourceLocation("jujutsu_kaisen", "hollow_wicker_basket").toString())
   );
   public static RegistryObject<EntityType<RedProjectile>> RED = ENTITIES.register(
      "red",
      () -> Builder.<RedProjectile>of(RedProjectile::new, MobCategory.MISC).sized(0.15F, 0.15F).build(new ResourceLocation("jujutsu_kaisen", "red").toString())
   );
   public static RegistryObject<EntityType<BlueProjectile>> BLUE = ENTITIES.register(
      "blue", () -> Builder.<BlueProjectile>of(BlueProjectile::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "blue").toString())
   );
   public static RegistryObject<EntityType<HollowPurpleProjectile>> HOLLOW_PURPLE = ENTITIES.register(
      "hollow_purple",
      () -> Builder.<HollowPurpleProjectile>of(HollowPurpleProjectile::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "hollow_purple").toString())
   );
   public static RegistryObject<EntityType<HollowPurpleExplosion>> HOLLOW_PURPLE_EXPLOSION = ENTITIES.register(
      "hollow_purple_explosion",
      () -> Builder.<HollowPurpleExplosion>of(HollowPurpleExplosion::new, MobCategory.MISC)
         .build(new ResourceLocation("jujutsu_kaisen", "hollow_purple_explosion").toString())
   );
   public static RegistryObject<EntityType<DismantleProjectile>> DISMANTLE = ENTITIES.register(
      "dismantle",
      () -> Builder.<DismantleProjectile>of(DismantleProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "dismantle").toString())
   );
   public static RegistryObject<EntityType<SpiderwebEntity>> SPIDERWEB = ENTITIES.register(
      "spiderweb", () -> Builder.<SpiderwebEntity>of(SpiderwebEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "spiderweb").toString())
   );
   public static RegistryObject<EntityType<WorldSlashProjectile>> WORLD_SLASH = ENTITIES.register(
      "world_slash",
      () -> Builder.<WorldSlashProjectile>of(WorldSlashProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "world_slash").toString())
   );
   public static RegistryObject<EntityType<FireArrowProjectile>> FIRE_ARROW = ENTITIES.register(
      "fire_arrow",
      () -> Builder.<FireArrowProjectile>of(FireArrowProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "fire_arrow").toString())
   );
   public static RegistryObject<EntityType<PureLoveBeamEntity>> PURE_LOVE = ENTITIES.register(
      "pure_love",
      () -> Builder.<PureLoveBeamEntity>of(PureLoveBeamEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "pure_love").toString())
   );
   public static RegistryObject<EntityType<EmberInsectProjectile>> EMBER_INSECT = ENTITIES.register(
      "ember_insect",
      () -> Builder.<EmberInsectProjectile>of(EmberInsectProjectile::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "ember_insect").toString())
   );
   public static RegistryObject<EntityType<EmberInsectFlightEntity>> EMBER_INSECT_FLIGHT = ENTITIES.register(
      "ember_insect_flight",
      () -> Builder.<EmberInsectFlightEntity>of(EmberInsectFlightEntity::new, MobCategory.MISC)
         .sized(1.0F, 0.2F)
         .build(new ResourceLocation("jujutsu_kaisen", "ember_insect_flight").toString())
   );
   public static RegistryObject<EntityType<VolcanoEntity>> VOLCANO = ENTITIES.register(
      "volcano",
      () -> Builder.<VolcanoEntity>of(VolcanoEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).build(new ResourceLocation("jujutsu_kaisen", "volcano").toString())
   );
   public static RegistryObject<EntityType<MeteorEntity>> METEOR = ENTITIES.register(
      "meteor", () -> Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "meteor").toString())
   );
   public static RegistryObject<EntityType<ThrownChainProjectile>> THROWN_CHAIN = ENTITIES.register(
      "throw_chain",
      () -> Builder.<ThrownChainProjectile>of(ThrownChainProjectile::new, MobCategory.MISC)
         .sized(1.5F, 1.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "throw_chain").toString())
   );
   public static RegistryObject<EntityType<ScissorEntity>> SCISSOR = ENTITIES.register(
      "scissor",
      () -> Builder.<ScissorEntity>of(ScissorEntity::new, MobCategory.MISC).sized(1.0F, 1.0F).build(new ResourceLocation("jujutsu_kaisen", "scissor").toString())
   );
   public static RegistryObject<EntityType<FireballProjectile>> FIREBALL = ENTITIES.register(
      "fireball",
      () -> Builder.<FireballProjectile>of(FireballProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "fireball").toString())
   );
   public static RegistryObject<EntityType<PiercingWaterEntity>> PIERCING_WATER = ENTITIES.register(
      "piercing_water",
      () -> Builder.<PiercingWaterEntity>of(PiercingWaterEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "piercing_water").toString())
   );
   public static RegistryObject<EntityType<JujutsuLightningEntity>> JUJUTSU_LIGHTNING = ENTITIES.register(
      "jujutsu_lightning",
      () -> Builder.<JujutsuLightningEntity>of(JujutsuLightningEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "jujutsu_lightning").toString())
   );
   public static RegistryObject<EntityType<SkyStrikeEntity>> SKY_STRIKE = ENTITIES.register(
      "sky_strike",
      () -> Builder.<SkyStrikeEntity>of(SkyStrikeEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "sky_strike").toString())
   );
   public static RegistryObject<EntityType<JacobsLadderEntity>> JACOBS_LADDER = ENTITIES.register(
      "jacobs_ladder",
      () -> Builder.<JacobsLadderEntity>of(JacobsLadderEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "jacobs_ladder").toString())
   );
   public static RegistryObject<EntityType<MaximumOutputJacobsLadderEntity>> MAXIMUM_OUTPUT_JACOBS_LADDER = ENTITIES.register(
      "maximum_output_jacobs_ladder",
      () -> Builder.<MaximumOutputJacobsLadderEntity>of(MaximumOutputJacobsLadderEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "maximum_output_jacobs_ladder").toString())
   );
   public static RegistryObject<EntityType<MaximumUzumakiProjectile>> MAXIMUM_UZUMAKI = ENTITIES.register(
      "maximum_uzumaki",
      () -> Builder.<MaximumUzumakiProjectile>of(MaximumUzumakiProjectile::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "maximum_uzumaki").toString())
   );
   public static RegistryObject<EntityType<MiniUzumakiProjectile>> MINI_UZUMAKI = ENTITIES.register(
      "mini_uzumaki",
      () -> Builder.<MiniUzumakiProjectile>of(MiniUzumakiProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "mini_uzumaki").toString())
   );
   public static RegistryObject<EntityType<WaterballEntity>> WATERBALL = ENTITIES.register(
      "waterball",
      () -> Builder.<WaterballEntity>of(WaterballEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "waterball").toString())
   );
   public static RegistryObject<EntityType<EelShikigamiProjectile>> EEL_SHIKIGAMI = ENTITIES.register(
      "eel_shikigami",
      () -> Builder.<EelShikigamiProjectile>of(EelShikigamiProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "eel_shikigami").toString())
   );
   public static RegistryObject<EntityType<PiranhaShikigamiProjectile>> PIRANHA_SHIKIGAMI = ENTITIES.register(
      "piranha_shikigami",
      () -> Builder.<PiranhaShikigamiProjectile>of(PiranhaShikigamiProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "piranha_shikigami").toString())
   );
   public static RegistryObject<EntityType<SharkShikigamiProjectile>> SHARK_SHIKIGAMI = ENTITIES.register(
      "shark_shikigami",
      () -> Builder.<SharkShikigamiProjectile>of(SharkShikigamiProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "shark_shikigami").toString())
   );
   public static RegistryObject<EntityType<WaterTorrentEntity>> WATER_TORRENT = ENTITIES.register(
      "water_torrent",
      () -> Builder.<WaterTorrentEntity>of(WaterTorrentEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "water_torrent").toString())
   );
   public static RegistryObject<EntityType<ForestSpikeEntity>> FOREST_SPIKE = ENTITIES.register(
      "forest_spike",
      () -> Builder.<ForestSpikeEntity>of(ForestSpikeEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "forest_spike").toString())
   );
   public static RegistryObject<EntityType<WoodSegmentEntity>> WOOD_SEGMENT = ENTITIES.register(
      "wood_segment",
      () -> Builder.<WoodSegmentEntity>of(WoodSegmentEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "wood_segment").toString())
   );
   public static RegistryObject<EntityType<WoodShieldSegmentEntity>> WOOD_SHIELD_SEGMENT = ENTITIES.register(
      "wood_shield_segment",
      () -> Builder.<WoodShieldSegmentEntity>of(WoodShieldSegmentEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "wood_shield_segment").toString())
   );
   public static RegistryObject<EntityType<WoodShieldEntity>> WOOD_SHIELD = ENTITIES.register(
      "wood_shield", () -> Builder.<WoodShieldEntity>of(WoodShieldEntity::new, MobCategory.MISC).build(new ResourceLocation("jujutsu_kaisen", "wood_shield").toString())
   );
   public static RegistryObject<EntityType<CursedBudProjectile>> CURSED_BUD = ENTITIES.register(
      "cursed_bud",
      () -> Builder.<CursedBudProjectile>of(CursedBudProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "cursed_bud").toString())
   );
   public static RegistryObject<EntityType<ForestWaveEntity>> FOREST_WAVE = ENTITIES.register(
      "forest_wave",
      () -> Builder.<ForestWaveEntity>of(ForestWaveEntity::new, MobCategory.MISC)
         .sized(0.75F, 0.75F)
         .build(new ResourceLocation("jujutsu_kaisen", "forest_wave").toString())
   );
   public static RegistryObject<EntityType<LavaRockProjectile>> LAVA_ROCK = ENTITIES.register(
      "lava_rock",
      () -> Builder.<LavaRockProjectile>of(LavaRockProjectile::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "lava_rock").toString())
   );
   public static RegistryObject<EntityType<LightningEntity>> LIGHTNING = ENTITIES.register(
      "lightning",
      () -> Builder.<LightningEntity>of(LightningEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "lightning").toString())
   );
   public static RegistryObject<EntityType<ProjectionFrameEntity>> PROJECTION_FRAME = ENTITIES.register(
      "projection_frame",
      () -> Builder.<ProjectionFrameEntity>of(ProjectionFrameEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "projection_frame").toString())
   );
   public static RegistryObject<EntityType<AirFrameEntity>> AIR_FRAME = ENTITIES.register(
      "air_frame",
      () -> Builder.<AirFrameEntity>of(AirFrameEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "air_frame").toString())
   );
   public static RegistryObject<EntityType<BlackFlashEntity>> BLACk_FLASH = ENTITIES.register(
      "black_flash",
      () -> Builder.<BlackFlashEntity>of(BlackFlashEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "black_flash").toString())
   );
   public static RegistryObject<EntityType<ForestRootsEntity>> FOREST_ROOTS = ENTITIES.register(
      "forest_roots",
      () -> Builder.<ForestRootsEntity>of(ForestRootsEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "forest_roots").toString())
   );
   public static RegistryObject<EntityType<FilmGaugeProjectile>> FILM_GAUGE = ENTITIES.register(
      "film_gauge",
      () -> Builder.<FilmGaugeProjectile>of(FilmGaugeProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "film_gauge").toString())
   );
   public static RegistryObject<EntityType<TimeCellMoonPalaceEntity>> TIME_CELL_MOON_PALACE = ENTITIES.register(
      "time_cell_moon_palace",
      () -> Builder.<TimeCellMoonPalaceEntity>of(TimeCellMoonPalaceEntity::new, MobCategory.MISC)
         .sized(1.9F, 2.4F)
         .build(new ResourceLocation("jujutsu_kaisen", "time_cell_moon_palace").toString())
   );
   public static RegistryObject<EntityType<SelfEmbodimentOfPerfectionEntity>> SELF_EMBODIMENT_OF_PERFECTION = ENTITIES.register(
      "self_embodiment_of_perfection",
      () -> Builder.<SelfEmbodimentOfPerfectionEntity>of(SelfEmbodimentOfPerfectionEntity::new, MobCategory.MISC)
         .sized(1.4F, 2.4F)
         .build(new ResourceLocation("jujutsu_kaisen", "self_embodiment_of_perfection").toString())
   );
   public static RegistryObject<EntityType<DisasterPlantEntity>> DISASTER_PLANT = ENTITIES.register(
      "disaster_plant",
      () -> Builder.<DisasterPlantEntity>of(DisasterPlantEntity::new, MobCategory.MISC)
         .sized(3.0F, 5.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "disaster_plant").toString())
   );
   public static RegistryObject<EntityType<WheelEntity>> WHEEL = ENTITIES.register(
      "wheel",
      () -> Builder.<WheelEntity>of(WheelEntity::new, MobCategory.MISC).sized(1.0F, 0.5F).build(new ResourceLocation("jujutsu_kaisen", "wheel").toString())
   );
   public static RegistryObject<EntityType<NyoiStaffEntity>> NYOI_STAFF = ENTITIES.register(
      "nyoi_staff",
      () -> Builder.<NyoiStaffEntity>of(NyoiStaffEntity::new, MobCategory.MISC)
         .sized(0.5F, 1.8F)
         .build(new ResourceLocation("jujutsu_kaisen", "nyoi_staff").toString())
   );
   public static RegistryObject<EntityType<FireBeamEntity>> FIRE_BEAM = ENTITIES.register(
      "fire_beam",
      () -> Builder.<FireBeamEntity>of(FireBeamEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .build(new ResourceLocation("jujutsu_kaisen", "fire_beam").toString())
   );
   public static RegistryObject<EntityType<ForestDashEntity>> FOREST_DASH = ENTITIES.register(
      "forest_dash",
      () -> Builder.<ForestDashEntity>of(ForestDashEntity::new, MobCategory.MISC)
         .sized(2.0F, 2.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "forest_dash").toString())
   );
   public static RegistryObject<EntityType<CursedEnergyImbuedItemProjectile>> CURSED_ENERGY_IMBUED_ITEM = ENTITIES.register(
      "cursed_energy_imbued_item",
      () -> Builder.<CursedEnergyImbuedItemProjectile>of(CursedEnergyImbuedItemProjectile::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .build(new ResourceLocation("jujutsu_kaisen", "cursed_energy_imbued_item").toString())
   );
   public static RegistryObject<EntityType<CursedEnergyBombEntity>> CURSED_ENERGY_BOMB = ENTITIES.register(
      "cursed_energy_bomb",
      () -> Builder.<CursedEnergyBombEntity>of(CursedEnergyBombEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "cursed_energy_bomb").toString())
   );
   public static RegistryObject<EntityType<CursedEnergyBlastEntity>> CURSED_ENERGY_BLAST = ENTITIES.register(
      "cursed_energy_blast",
      () -> Builder.<CursedEnergyBlastEntity>of(CursedEnergyBlastEntity::new, MobCategory.MISC)
         .sized(1.0F, 1.0F)
         .build(new ResourceLocation("jujutsu_kaisen", "cursed_energy_blast").toString())
   );
   public static RegistryObject<EntityType<GraniteRingEntity>> GRANITE_RING = ENTITIES.register(
      "granite_ring",
      () -> Builder.<GraniteRingEntity>of(GraniteRingEntity::new, MobCategory.MISC)
         .sized(0.1F, 0.1F)
         .fireImmune()
         .build(new ResourceLocation("jujutsu_kaisen", "granite_ring").toString())
   );
   public static RegistryObject<EntityType<GraniteBlastVolleyEntity>> GRANITE_BLAST_VOLLEY = ENTITIES.register(
      "granite_blast_volley",
      () -> Builder.<GraniteBlastVolleyEntity>of(GraniteBlastVolleyEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "granite_blast_volley").toString())
   );
   public static RegistryObject<EntityType<MaximumGraniteBeamEntity>> MAXIMUM_GRANITE_BEAM = ENTITIES.register(
      "maximum_granite_beam",
      () -> Builder.<MaximumGraniteBeamEntity>of(MaximumGraniteBeamEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "maximum_granite_beam").toString())
   );
   public static RegistryObject<EntityType<GraniteBlastEntity>> GRANITE_BLAST = ENTITIES.register(
      "granite_blast",
      () -> Builder.<GraniteBlastEntity>of(GraniteBlastEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "granite_blast").toString())
   );
   public static RegistryObject<EntityType<ElectricBeamEntity>> ELECTRIC_BEAM = ENTITIES.register(
      "electric_beam",
      () -> Builder.<ElectricBeamEntity>of(ElectricBeamEntity::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "electric_beam").toString())
   );
   public static RegistryObject<EntityType<EelGrappleProjectile>> EEL_GRAPPLE = ENTITIES.register(
      "eel_grapple",
      () -> Builder.<EelGrappleProjectile>of(EelGrappleProjectile::new, MobCategory.MISC)
         .sized(0.5F, 0.5F)
         .build(new ResourceLocation("jujutsu_kaisen", "eel_grapple").toString())
   );
   public static RegistryObject<EntityType<TransfiguredSoulProjectile>> TRANSFIGURED_SOUL = ENTITIES.register(
      "transfigured_soul",
      () -> Builder.<TransfiguredSoulProjectile>of(TransfiguredSoulProjectile::new, MobCategory.MISC)
         .sized(0.25F, 0.25F)
         .build(new ResourceLocation("jujutsu_kaisen", "transfigured_soul").toString())
   );
   public static RegistryObject<EntityType<BodyRepelEntity>> BODY_REPEL = ENTITIES.register(
      "body_repel",
      () -> Builder.<BodyRepelEntity>of(BodyRepelEntity::new, MobCategory.MISC)
         .sized(1.3125F, 1.375F)
         .build(new ResourceLocation("jujutsu_kaisen", "body_repel").toString())
   );

   @SubscribeEvent
   public static void onCreateEntityAttributes(EntityAttributeCreationEvent event) {
      event.put((EntityType)TOJI_FUSHIGURO.get(), MakiZeninEntity.createAttributes().build());
      event.put((EntityType)SUKUNA.get(), SukunaEntity.createAttributes().build());
      event.put((EntityType)SATORU_GOJO.get(), SatoruGojoEntity.createAttributes().build());
      event.put((EntityType)MEGUMI_FUSHIGURO.get(), MegumiFushiguroEntity.createAttributes().build());
      event.put((EntityType)YUTA_OKKOTSU.get(), YutaOkkotsuEntity.createAttributes().build());
      event.put((EntityType)YUJI_ITADORI.get(), YujiItadoriEntity.createAttributes().build());
      event.put((EntityType)TOGE_INUMAKI.get(), TogeInumakiEntity.createAttributes().build());
      event.put((EntityType)SUGURU_GETO.get(), SuguruGetoEntity.createAttributes().build());
      event.put((EntityType)HEIAN_SUKUNA.get(), HeianSukunaEntity.createAttributes().build());
      event.put((EntityType)NAOYA_ZENIN.get(), NaoyaZeninEntity.createAttributes().build());
      event.put((EntityType)HAJIME_KASHIMO.get(), HajimeKashimoEntity.createAttributes().build());
      event.put((EntityType)RYU_ISHIGORI.get(), RyuIshigoriEntity.createAttributes().build());
      event.put((EntityType)MAKI_ZENIN.get(), MakiZeninEntity.createAttributes().build());
      event.put((EntityType)AOI_TODO.get(), AoiTodoEntity.createAttributes().build());
      event.put((EntityType)RIKA.get(), RikaEntity.createAttributes().build());
      event.put((EntityType)MAHORAGA.get(), MahoragaEntity.createAttributes().build());
      event.put((EntityType)DIVINE_DOG_WHITE.get(), DivineDogEntity.createAttributes().build());
      event.put((EntityType)DIVINE_DOG_BLACK.get(), DivineDogEntity.createAttributes().build());
      event.put((EntityType)DIVINE_DOG_TOTALITY.get(), DivineDogTotalityEntity.createAttributes().build());
      event.put((EntityType)TOAD.get(), ToadEntity.createAttributes().build());
      event.put((EntityType)TOAD_FUSION.get(), ToadEntity.createAttributes().build());
      event.put((EntityType)RABBIT_ESCAPE.get(), RabbitEscapeEntity.createAttributes().build());
      event.put((EntityType)NUE.get(), NueEntity.createAttributes().build());
      event.put((EntityType)NUE_TOTALITY.get(), NueTotalityEntity.createAttributes().build());
      event.put((EntityType)GREAT_SERPENT.get(), GreatSerpentEntity.createAttributes().build());
      event.put((EntityType)MAX_ELEPHANT.get(), MaxElephantEntity.createAttributes().build());
      event.put((EntityType)TRANQUIL_DEER.get(), TranquilDeerEntity.createAttributes().build());
      event.put((EntityType)PIERCING_BULL.get(), PiercingBullEntity.createAttributes().build());
      event.put((EntityType)AGITO.get(), AgitoEntity.createAttributes().build());
      event.put((EntityType)TRANSFIGURED_SOUL_SMALL.get(), TransfiguredSoulSmallEntity.createAttributes().build());
      event.put((EntityType)TRANSFIGURED_SOUL_NORMAL.get(), SorcererEntity.createAttributes().build());
      event.put((EntityType)TRANSFIGURED_SOUL_LARGE.get(), TransfiguredSoulLargeEntity.createAttributes().build());
      event.put((EntityType)POLYMORPHIC_SOUL_ISOMER.get(), PolymorphicSoulIsomerEntity.createAttributes().build());
      event.put((EntityType)JOGO.get(), JogoEntity.createAttributes().build());
      event.put((EntityType)JOGOAT.get(), SorcererEntity.createAttributes().build());
      event.put((EntityType)DAGON.get(), DagonEntity.createAttributes().build());
      event.put((EntityType)HANAMI.get(), HanamiEntity.createAttributes().build());
      event.put((EntityType)RUGBY_FIELD_CURSE.get(), RugbyFieldCurseEntity.createAttributes().build());
      event.put((EntityType)FISH_CURSE.get(), FishCurseEntity.createAttributes().build());
      event.put((EntityType)CYCLOPS_CURSE.get(), CyclopsCurseEntity.createAttributes().build());
      event.put((EntityType)KUCHISAKE_ONNA.get(), KuchisakeOnnaEntity.createAttributes().build());
      event.put((EntityType)ZOMBA_CURSE.get(), ZombaCurseEntity.createAttributes().build());
      event.put((EntityType)WORM_CURSE.get(), WormCurseEntity.createAttributes().build());
      event.put((EntityType)FELINE_CURSE.get(), FelineCurseEntity.createAttributes().build());
      event.put((EntityType)FUGLY_CURSE.get(), FuglyCurseEntity.createAttributes().build());
      event.put((EntityType)BIRD_CURSE.get(), BirdCurseEntity.createAttributes().build());
      event.put((EntityType)FINGER_BEARER.get(), FingerBearerEntity.createAttributes().build());
      event.put((EntityType)RAINBOW_DRAGON.get(), RainbowDragonEntity.createAttributes().build());
      event.put((EntityType)DINO_CURSE.get(), DinoCurseEntity.createAttributes().build());
      event.put((EntityType)ABSORBED_PLAYER.get(), SorcererEntity.createAttributes().build());
      event.put((EntityType)LIMBO_CLONE.get(), SorcererEntity.createAttributes().build());
      event.put((EntityType)WOOD_SHIELD.get(), Mob.createMobAttributes().build());
   }
}
