package radon.jujutsu_kaisen.ability;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import radon.jujutsu_kaisen.ability.ai.dino_curse.BlueFire;
import radon.jujutsu_kaisen.ability.ai.max_elephant.Water;
import radon.jujutsu_kaisen.ability.ai.nue_totality.NueTotalityLightning;
import radon.jujutsu_kaisen.ability.ai.rika.ShootPureLove;
import radon.jujutsu_kaisen.ability.ai.scissor.Scissors;
import radon.jujutsu_kaisen.ability.angel.AngelWings;
import radon.jujutsu_kaisen.ability.angel.BarrierTravel;
import radon.jujutsu_kaisen.ability.angel.JacobsLadder;
import radon.jujutsu_kaisen.ability.angel.MaximumOutputJacobsLadder;
import radon.jujutsu_kaisen.ability.barrier.BarrierSizeConfig;
import radon.jujutsu_kaisen.ability.barrier.ShellBalanceConfig;
import radon.jujutsu_kaisen.ability.barrier.SureHitConfig;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.Summon;
import radon.jujutsu_kaisen.ability.body_swap.BodySteal;
import radon.jujutsu_kaisen.ability.boogie_woogie.CEThrow;
import radon.jujutsu_kaisen.ability.boogie_woogie.Feint;
import radon.jujutsu_kaisen.ability.boogie_woogie.ItemSwap;
import radon.jujutsu_kaisen.ability.boogie_woogie.Shuffle;
import radon.jujutsu_kaisen.ability.boogie_woogie.SwapOthers;
import radon.jujutsu_kaisen.ability.boogie_woogie.SwapSelf;
import radon.jujutsu_kaisen.ability.curse_manipulation.CurseAbsorption;
import radon.jujutsu_kaisen.ability.curse_manipulation.EnhanceCurse;
import radon.jujutsu_kaisen.ability.curse_manipulation.FishSwarm;
import radon.jujutsu_kaisen.ability.curse_manipulation.MaximumUzumaki;
import radon.jujutsu_kaisen.ability.curse_manipulation.MiniUzumaki;
import radon.jujutsu_kaisen.ability.curse_manipulation.ReleaseCurse;
import radon.jujutsu_kaisen.ability.curse_manipulation.ReleaseCurses;
import radon.jujutsu_kaisen.ability.curse_manipulation.SummonAll;
import radon.jujutsu_kaisen.ability.curse_manipulation.WormCurseGrab;
import radon.jujutsu_kaisen.ability.cursed_energy_discharge.GraniteBlast;
import radon.jujutsu_kaisen.ability.cursed_energy_discharge.GraniteBlastVolley;
import radon.jujutsu_kaisen.ability.cursed_energy_discharge.GraniteRing;
import radon.jujutsu_kaisen.ability.cursed_energy_discharge.MaximumGraniteBeam;
import radon.jujutsu_kaisen.ability.cursed_energy_discharge.SweetBurningMatsuri;
import radon.jujutsu_kaisen.ability.cursed_speech.BlastAway;
import radon.jujutsu_kaisen.ability.cursed_speech.Blink;
import radon.jujutsu_kaisen.ability.cursed_speech.BurnUp;
import radon.jujutsu_kaisen.ability.cursed_speech.Die;
import radon.jujutsu_kaisen.ability.cursed_speech.DontMove;
import radon.jujutsu_kaisen.ability.cursed_speech.Explode;
import radon.jujutsu_kaisen.ability.cursed_speech.GetCrushed;
import radon.jujutsu_kaisen.ability.cursed_speech.Return;
import radon.jujutsu_kaisen.ability.cursed_speech.RunAway;
import radon.jujutsu_kaisen.ability.cursed_speech.ShutIt;
import radon.jujutsu_kaisen.ability.disaster_flames.CoffinOfTheIronMountain;
import radon.jujutsu_kaisen.ability.disaster_flames.DisasterFlames;
import radon.jujutsu_kaisen.ability.disaster_flames.EmberInsectFlight;
import radon.jujutsu_kaisen.ability.disaster_flames.EmberInsects;
import radon.jujutsu_kaisen.ability.disaster_flames.FireBeam;
import radon.jujutsu_kaisen.ability.disaster_flames.Fireball;
import radon.jujutsu_kaisen.ability.disaster_flames.Flamethrower;
import radon.jujutsu_kaisen.ability.disaster_flames.MaximumMeteor;
import radon.jujutsu_kaisen.ability.disaster_flames.Volcano;
import radon.jujutsu_kaisen.ability.disaster_plants.CursedBud;
import radon.jujutsu_kaisen.ability.disaster_plants.DisasterPlant;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestDash;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestPlatform;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestRoots;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestSpikes;
import radon.jujutsu_kaisen.ability.disaster_plants.ForestWave;
import radon.jujutsu_kaisen.ability.disaster_plants.ShiningSeaOfFlowers;
import radon.jujutsu_kaisen.ability.disaster_plants.WoodShield;
import radon.jujutsu_kaisen.ability.disaster_tides.DeathSwarm;
import radon.jujutsu_kaisen.ability.disaster_tides.DisasterTides;
import radon.jujutsu_kaisen.ability.disaster_tides.EelGrapple;
import radon.jujutsu_kaisen.ability.disaster_tides.FishShikigami;
import radon.jujutsu_kaisen.ability.disaster_tides.HorizonOfTheCaptivatingSkandha;
import radon.jujutsu_kaisen.ability.disaster_tides.WaterShield;
import radon.jujutsu_kaisen.ability.disaster_tides.WaterTorrent;
import radon.jujutsu_kaisen.ability.idle_transfiguration.ArmBlade;
import radon.jujutsu_kaisen.ability.idle_transfiguration.BodyRepel;
import radon.jujutsu_kaisen.ability.idle_transfiguration.Gun;
import radon.jujutsu_kaisen.ability.idle_transfiguration.HorseLegs;
import radon.jujutsu_kaisen.ability.idle_transfiguration.IdleTransfiguration;
import radon.jujutsu_kaisen.ability.idle_transfiguration.InstantSpiritBodyOfDistortedKilling;
import radon.jujutsu_kaisen.ability.idle_transfiguration.PolymorphicSoulIsomer;
import radon.jujutsu_kaisen.ability.idle_transfiguration.SelfEmbodimentOfPerfection;
import radon.jujutsu_kaisen.ability.idle_transfiguration.Shrink;
import radon.jujutsu_kaisen.ability.idle_transfiguration.SoulDecimation;
import radon.jujutsu_kaisen.ability.idle_transfiguration.SoulReinforcement;
import radon.jujutsu_kaisen.ability.idle_transfiguration.SoulRestoration;
import radon.jujutsu_kaisen.ability.idle_transfiguration.TransfiguredSoulLarge;
import radon.jujutsu_kaisen.ability.idle_transfiguration.TransfiguredSoulNormal;
import radon.jujutsu_kaisen.ability.idle_transfiguration.TransfiguredSoulSmall;
import radon.jujutsu_kaisen.ability.idle_transfiguration.Wings;
import radon.jujutsu_kaisen.ability.limitless.BlueFists;
import radon.jujutsu_kaisen.ability.limitless.BlueMotion;
import radon.jujutsu_kaisen.ability.limitless.BlueStill;
import radon.jujutsu_kaisen.ability.limitless.Fly;
import radon.jujutsu_kaisen.ability.limitless.HollowPurple;
import radon.jujutsu_kaisen.ability.limitless.Infinity;
import radon.jujutsu_kaisen.ability.limitless.Red;
import radon.jujutsu_kaisen.ability.limitless.Teleport;
import radon.jujutsu_kaisen.ability.limitless.UnlimitedVoid;
import radon.jujutsu_kaisen.ability.mimicry.CommandPureLove;
import radon.jujutsu_kaisen.ability.mimicry.Mimicry;
import radon.jujutsu_kaisen.ability.mimicry.Rika;
import radon.jujutsu_kaisen.ability.misc.Barrage;
import radon.jujutsu_kaisen.ability.misc.Blitz;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyBlast;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyBomb;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyFlow;
import radon.jujutsu_kaisen.ability.misc.CursedEnergyShield;
import radon.jujutsu_kaisen.ability.misc.Dash;
import radon.jujutsu_kaisen.ability.misc.DispelVeil;
import radon.jujutsu_kaisen.ability.misc.DomainAmplification;
import radon.jujutsu_kaisen.ability.misc.FallingBlossomEmotion;
import radon.jujutsu_kaisen.ability.misc.Heal;
import radon.jujutsu_kaisen.ability.misc.HollowWickerBasket;
import radon.jujutsu_kaisen.ability.misc.IncompleteDomain;
import radon.jujutsu_kaisen.ability.misc.OutputRCT;
import radon.jujutsu_kaisen.ability.misc.Punch;
import radon.jujutsu_kaisen.ability.misc.QuickDash;
import radon.jujutsu_kaisen.ability.misc.QuickDraw;
import radon.jujutsu_kaisen.ability.misc.QuickVeilConfig;
import radon.jujutsu_kaisen.ability.misc.RCT1;
import radon.jujutsu_kaisen.ability.misc.RCT2;
import radon.jujutsu_kaisen.ability.misc.RCT3;
import radon.jujutsu_kaisen.ability.misc.SimpleDomain;
import radon.jujutsu_kaisen.ability.misc.SimpleDomainEnlargement;
import radon.jujutsu_kaisen.ability.misc.Slam;
import radon.jujutsu_kaisen.ability.misc.Switch;
import radon.jujutsu_kaisen.ability.misc.Veil;
import radon.jujutsu_kaisen.ability.misc.ZeroPointTwoSecondDomainExpansion;
import radon.jujutsu_kaisen.ability.misc.lightning.Discharge;
import radon.jujutsu_kaisen.ability.misc.lightning.Lightning;
import radon.jujutsu_kaisen.ability.mythical_beast_amber.EMFBlast;
import radon.jujutsu_kaisen.ability.mythical_beast_amber.ElectricBeam;
import radon.jujutsu_kaisen.ability.mythical_beast_amber.MythicalBeastAmber;
import radon.jujutsu_kaisen.ability.projection_sorcery.AirFrame;
import radon.jujutsu_kaisen.ability.projection_sorcery.ProjectionSorcery;
import radon.jujutsu_kaisen.ability.projection_sorcery.TimeCellMoonPalace;
import radon.jujutsu_kaisen.ability.projection_sorcery.TwentyFourFrameRule;
import radon.jujutsu_kaisen.ability.ratio.Bisection;
import radon.jujutsu_kaisen.ability.ratio.Collapse;
import radon.jujutsu_kaisen.ability.ratio.RatioRule;
import radon.jujutsu_kaisen.ability.shockwave.Shockwave;
import radon.jujutsu_kaisen.ability.shrine.Cleave;
import radon.jujutsu_kaisen.ability.shrine.Dismantle;
import radon.jujutsu_kaisen.ability.shrine.DismantleNet;
import radon.jujutsu_kaisen.ability.shrine.DismantleSkating;
import radon.jujutsu_kaisen.ability.shrine.FireArrow;
import radon.jujutsu_kaisen.ability.shrine.MalevolentShrine;
import radon.jujutsu_kaisen.ability.shrine.Spiderweb;
import radon.jujutsu_kaisen.ability.shrine.WorldSlash;
import radon.jujutsu_kaisen.ability.sky_strike.SkyStrike;
import radon.jujutsu_kaisen.ability.ten_shadows.ChimeraShadowGarden;
import radon.jujutsu_kaisen.ability.ten_shadows.GreatSerpentGrab;
import radon.jujutsu_kaisen.ability.ten_shadows.ShadowStorage;
import radon.jujutsu_kaisen.ability.ten_shadows.ShadowTravel;
import radon.jujutsu_kaisen.ability.ten_shadows.SwitchMode;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.NueLightning;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.PiercingWater;
import radon.jujutsu_kaisen.ability.ten_shadows.ability.Wheel;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.Agito;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.DivineDogTotality;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.DivineDogs;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.GreatSerpent;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.Mahoraga;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.MaxElephant;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.Nue;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.NueTotality;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.PiercingBull;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.RabbitEscape;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.ReleaseShikigami;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.Toad;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.ToadFusion;
import radon.jujutsu_kaisen.ability.ten_shadows.summon.TranquilDeer;
import radon.jujutsu_kaisen.capability.data.sorcerer.AbsorbedCurse;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.capability.data.ten_shadows.ITenShadowsData;
import radon.jujutsu_kaisen.capability.data.ten_shadows.TenShadowsDataHandler;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.entity.JJKEntities;
import radon.jujutsu_kaisen.entity.base.ISorcerer;
import radon.jujutsu_kaisen.entity.curse.AbsorbedPlayerEntity;
import radon.jujutsu_kaisen.entity.curse.JogoatEntity;
import radon.jujutsu_kaisen.entity.curse.base.CursedSpirit;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.util.RotationUtil;

public class JJKAbilities {
   public static DeferredRegister<Ability> ABILITIES = DeferredRegister.create(new ResourceLocation("jujutsu_kaisen", "ability"), "jujutsu_kaisen");
   public static Supplier<IForgeRegistry<Ability>> ABILITY_REGISTRY = ABILITIES.makeRegistry(RegistryBuilder::new);
   public static RegistryObject<Ability> SHOCKWAVE = ABILITIES.register("shockwave", Shockwave::new);
   public static RegistryObject<Ability> SKY_STRIKE = ABILITIES.register("sky_strike", SkyStrike::new);
   public static RegistryObject<Ability> BODY_STEAL = ABILITIES.register("body_steal", BodySteal::new);
   public static RegistryObject<Ability> MAXIMUM_OUTPUT_JACOBS_LADDER = ABILITIES.register("maximum_output_jacobs_ladder", MaximumOutputJacobsLadder::new);
   public static RegistryObject<Ability> JACOBS_LADDER = ABILITIES.register("jacobs_ladder", JacobsLadder::new);
   public static RegistryObject<Ability> ANGEL_WINGS = ABILITIES.register("angel_wings", AngelWings::new);
   public static RegistryObject<Ability> BARRIER_TRAVEL = ABILITIES.register("barrier_travel", BarrierTravel::new);
   public static RegistryObject<Ability> QUICKDASH = ABILITIES.register("quickdash", QuickDash::new);
   public static RegistryObject<Ability> DASH = ABILITIES.register("dash", Dash::new);
   public static RegistryObject<Ability> PUNCH = ABILITIES.register("punch", Punch::new);
   public static RegistryObject<Ability> SLAM = ABILITIES.register("slam", Slam::new);
   public static RegistryObject<Ability> BARRAGE = ABILITIES.register("barrage", Barrage::new);
   public static RegistryObject<Ability> BLITZ = ABILITIES.register("blitz", Blitz::new);
   public static RegistryObject<Ability> HEAL = ABILITIES.register("heal", Heal::new);
   public static RegistryObject<Ability> INCOMPLETE_DOMAIN = ABILITIES.register("incomplete_domain", IncompleteDomain::new);
   public static RegistryObject<Ability> VEIL = ABILITIES.register("veil", Veil::new);
   public static RegistryObject<Ability> DISPEL_VEIL = ABILITIES.register("dispel_veil", DispelVeil::new);
   public static RegistryObject<Ability> QUICK_VEIL_CONFIG = ABILITIES.register("quick_veil_config", QuickVeilConfig::new);
   public static RegistryObject<Ability> DOMAIN_AMPLIFICATION = ABILITIES.register("domain_amplification", DomainAmplification::new);
   public static RegistryObject<Ability> SIMPLE_DOMAIN = ABILITIES.register("simple_domain", SimpleDomain::new);
   public static RegistryObject<Ability> SIMPLE_DOMAIN_ENLARGEMENT = ABILITIES.register("simple_domain_enlargement", SimpleDomainEnlargement::new);
   public static RegistryObject<Ability> HOLLOW_WICKER_BASKET = ABILITIES.register("hollow_wicker_basket", HollowWickerBasket::new);
   public static RegistryObject<Ability> QUICK_DRAW = ABILITIES.register("quick_draw", QuickDraw::new);
   public static RegistryObject<Ability> FALLING_BLOSSOM_EMOTION = ABILITIES.register("falling_blossom_emotion", FallingBlossomEmotion::new);
   public static RegistryObject<Ability> CURSED_ENERGY_FLOW = ABILITIES.register("cursed_energy_flow", CursedEnergyFlow::new);
   public static RegistryObject<Ability> CURSED_ENERGY_SHIELD = ABILITIES.register("cursed_energy_shield", CursedEnergyShield::new);
   public static RegistryObject<Ability> LIGHTNING = ABILITIES.register("lightning", Lightning::new);
   public static RegistryObject<Ability> DISCHARGE = ABILITIES.register("discharge", Discharge::new);
   public static RegistryObject<Ability> ZERO_POINT_TWO_SECOND_DOMAIN_EXPANSION = ABILITIES.register(
      "zero_point_two_second_domain_expansion", ZeroPointTwoSecondDomainExpansion::new
   );
   public static RegistryObject<Ability> SWITCH = ABILITIES.register("switch", Switch::new);
   public static RegistryObject<Ability> CURSED_ENERGY_BOMB = ABILITIES.register("cursed_energy_bomb", CursedEnergyBomb::new);
   public static RegistryObject<Ability> CURSED_ENERGY_BLAST = ABILITIES.register("cursed_energy_blast", CursedEnergyBlast::new);
   public static RegistryObject<Ability> BARRIER_SIZE_CONFIG = ABILITIES.register("barrier_size_config", BarrierSizeConfig::new);
   public static RegistryObject<Ability> SHELL_BALANCE_CONFIG = ABILITIES.register("shell_balance_config", ShellBalanceConfig::new);
   public static RegistryObject<Ability> SURE_HIT_CONFIG = ABILITIES.register("sure_hit_config", SureHitConfig::new);
   public static RegistryObject<RCT1> RCT1 = ABILITIES.register("rct1", RCT1::new);
   public static RegistryObject<RCT1> RCT2 = ABILITIES.register("rct2", RCT2::new);
   public static RegistryObject<RCT1> RCT3 = ABILITIES.register("rct3", RCT3::new);
   public static RegistryObject<Ability> OUTPUT_RCT = ABILITIES.register("output_rct", OutputRCT::new);
   public static RegistryObject<Ability> SHOOT_PURE_LOVE = ABILITIES.register("shoot_pure_love", ShootPureLove::new);
   public static RegistryObject<Ability> WATER = ABILITIES.register("water", Water::new);
   public static RegistryObject<Ability> SCISSORS = ABILITIES.register("scissors", Scissors::new);
   public static RegistryObject<Ability> BLUE_FIRE = ABILITIES.register("blue_fire", BlueFire::new);
   public static RegistryObject<Ability> INFINITY = ABILITIES.register("infinity", Infinity::new);
   public static RegistryObject<Ability> RED = ABILITIES.register("red", Red::new);
   public static RegistryObject<Ability> BLUE_STILL = ABILITIES.register("blue_still", BlueStill::new);
   public static RegistryObject<Ability> BLUE_MOTION = ABILITIES.register("blue_motion", BlueMotion::new);
   public static RegistryObject<Ability> BLUE_FISTS = ABILITIES.register("blue_fists", BlueFists::new);
   public static RegistryObject<Ability> HOLLOW_PURPLE = ABILITIES.register("hollow_purple", HollowPurple::new);
   public static RegistryObject<Ability> TELEPORT = ABILITIES.register("teleport", Teleport::new);
   public static RegistryObject<Ability> FLY = ABILITIES.register("fly", Fly::new);
   public static RegistryObject<Ability> UNLIMITED_VOID = ABILITIES.register("unlimited_void", UnlimitedVoid::new);
   public static RegistryObject<Ability> DISMANTLE = ABILITIES.register("dismantle", Dismantle::new);
   public static RegistryObject<Ability> WORLD_SLASH = ABILITIES.register("world_slash", WorldSlash::new);
   public static RegistryObject<Ability> DISMANTLE_NET = ABILITIES.register("dismantle_net", DismantleNet::new);
   public static RegistryObject<Ability> DISMANTLE_SKATING = ABILITIES.register("dismantle_skating", DismantleSkating::new);
   public static RegistryObject<Ability> CLEAVE = ABILITIES.register("cleave", Cleave::new);
   public static RegistryObject<Ability> SPIDERWEB = ABILITIES.register("spiderweb", Spiderweb::new);
   public static RegistryObject<Ability> FIRE_ARROW = ABILITIES.register("fire_arrow", FireArrow::new);
   public static RegistryObject<Ability> MALEVOLENT_SHRINE = ABILITIES.register("malevolent_shrine", MalevolentShrine::new);
   public static RegistryObject<Summon<?>> RIKA = ABILITIES.register("rika", Rika::new);
   public static RegistryObject<Ability> MIMICRY = ABILITIES.register("mimicry", Mimicry::new);
   public static RegistryObject<Ability> COMMAND_PURE_LOVE = ABILITIES.register("command_pure_love", CommandPureLove::new);
   public static RegistryObject<Ability> EMBER_INSECTS = ABILITIES.register("ember_insects", EmberInsects::new);
   public static RegistryObject<Ability> EMBER_INSECT_FLIGHT = ABILITIES.register("ember_insect_flight", EmberInsectFlight::new);
   public static RegistryObject<Ability> VOLCANO = ABILITIES.register("volcano", Volcano::new);
   public static RegistryObject<Ability> MAXIMUM_METEOR = ABILITIES.register("maximum_meteor", MaximumMeteor::new);
   public static RegistryObject<Ability> DISASTER_FLAMES = ABILITIES.register("disaster_flames", DisasterFlames::new);
   public static RegistryObject<Ability> FLAMETHROWER = ABILITIES.register("flamethrower", Flamethrower::new);
   public static RegistryObject<Ability> FIREBALL = ABILITIES.register("fireball", Fireball::new);
   public static RegistryObject<Ability> FIRE_BEAM = ABILITIES.register("fire_beam", FireBeam::new);
   public static RegistryObject<Ability> COFFIN_OF_THE_IRON_MOUNTAIN = ABILITIES.register("coffin_of_the_iron_mountain", CoffinOfTheIronMountain::new);
   public static RegistryObject<Ability> HORIZON_OF_THE_CAPTIVATING_SKANDHA = ABILITIES.register(
      "horizon_of_the_captivating_skandha", HorizonOfTheCaptivatingSkandha::new
   );
   public static RegistryObject<Ability> DISASTER_TIDES = ABILITIES.register("disaster_tides", DisasterTides::new);
   public static RegistryObject<Ability> WATER_SHIELD = ABILITIES.register("water_shield", WaterShield::new);
   public static RegistryObject<Ability> DEATH_SWARM = ABILITIES.register("death_swarm", DeathSwarm::new);
   public static RegistryObject<Ability> FISH_SHIKIGAMI = ABILITIES.register("fish_shikigami", FishShikigami::new);
   public static RegistryObject<Ability> WATER_TORRENT = ABILITIES.register("water_torrent", WaterTorrent::new);
   public static RegistryObject<Ability> EEL_GRAPPLE = ABILITIES.register("eel_grapple", EelGrapple::new);
   public static RegistryObject<Ability> FOREST_PLATFORM = ABILITIES.register("forest_platform", ForestPlatform::new);
   public static RegistryObject<Ability> FOREST_SPIKES = ABILITIES.register("forest_spikes", ForestSpikes::new);
   public static RegistryObject<Ability> WOOD_SHIELD = ABILITIES.register("wood_shield", WoodShield::new);
   public static RegistryObject<Ability> CURSED_BUD = ABILITIES.register("cursed_bud", CursedBud::new);
   public static RegistryObject<Ability> FOREST_WAVE = ABILITIES.register("forest_wave", ForestWave::new);
   public static RegistryObject<Ability> FOREST_ROOTS = ABILITIES.register("forest_roots", ForestRoots::new);
   public static RegistryObject<Ability> FOREST_DASH = ABILITIES.register("forest_dash", ForestDash::new);
   public static RegistryObject<Ability> DISASTER_PLANT = ABILITIES.register("disaster_plant", DisasterPlant::new);
   public static RegistryObject<Ability> SHINING_SEA_OF_FLOWERS = ABILITIES.register("shining_sea_of_flowers", ShiningSeaOfFlowers::new);
   public static RegistryObject<Ability> IDLE_TRANSFIGURATION = ABILITIES.register("idle_transfiguration", IdleTransfiguration::new);
   public static RegistryObject<Ability> SOUL_DECIMATION = ABILITIES.register("soul_decimation", SoulDecimation::new);
   public static RegistryObject<Ability> BODY_REPEL = ABILITIES.register("body_repel", BodyRepel::new);
   public static RegistryObject<Ability> SOUL_REINFORCEMENT = ABILITIES.register("soul_reinforcement", SoulReinforcement::new);
   public static RegistryObject<Ability> SOUL_RESTORATION = ABILITIES.register("soul_restoration", SoulRestoration::new);
   public static RegistryObject<Ability> ARM_BLADE = ABILITIES.register("arm_blade", ArmBlade::new);
   public static RegistryObject<Ability> GUN = ABILITIES.register("gun", Gun::new);
   public static RegistryObject<Ability> SHRINK = ABILITIES.register("shrink", Shrink::new);
   public static RegistryObject<Ability> HORSE_LEGS = ABILITIES.register("horse_legs", HorseLegs::new);
   public static RegistryObject<Ability> WINGS = ABILITIES.register("wings", Wings::new);
   public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_SMALL = ABILITIES.register("transfigured_soul_small", TransfiguredSoulSmall::new);
   public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_NORMAL = ABILITIES.register("transfigured_soul_normal", TransfiguredSoulNormal::new);
   public static RegistryObject<Summon<?>> TRANSFIGURED_SOUL_LARGE = ABILITIES.register("transfigured_soul_large", TransfiguredSoulLarge::new);
   public static RegistryObject<Summon<?>> POLYMORPHIC_SOUl_ISOMER = ABILITIES.register("polymorphic_soul_isomer", PolymorphicSoulIsomer::new);
   public static RegistryObject<Ability> INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING = ABILITIES.register(
      "instant_spirit_body_of_distorted_killing", InstantSpiritBodyOfDistortedKilling::new
   );
   public static RegistryObject<Ability> SELF_EMBODIMENT_OF_PERFECTION = ABILITIES.register("self_embodiment_of_perfection", SelfEmbodimentOfPerfection::new);
   public static RegistryObject<Ability> MYTHICAL_BEAST_AMBER = ABILITIES.register("mythical_beast_amber", MythicalBeastAmber::new);
   public static RegistryObject<Ability> EMF_BLAST = ABILITIES.register("emf_blast", EMFBlast::new);
   public static RegistryObject<Ability> ELECTRIC_BEAM = ABILITIES.register("electric_beam", ElectricBeam::new);
   public static RegistryObject<Ability> GRANITE_BLAST = ABILITIES.register("granite_blast", GraniteBlast::new);
   public static RegistryObject<Ability> GRANITE_BLAST_VOLLEY = ABILITIES.register("granite_blast_volley", GraniteBlastVolley::new);
   public static RegistryObject<Ability> MAXIMUM_GRANITE_BEAM = ABILITIES.register("maximum_granite_beam", MaximumGraniteBeam::new);
   public static RegistryObject<Ability> GRANITE_RING = ABILITIES.register("granite_ring", GraniteRing::new);
   public static RegistryObject<Ability> SWEET_BURNING_MATSURI = ABILITIES.register("sweet_burning_matsuri", SweetBurningMatsuri::new);
   public static RegistryObject<Ability> SWITCH_MODE = ABILITIES.register("switch_mode", SwitchMode::new);
   public static RegistryObject<Ability> RELEASE_SHIKIGAMI = ABILITIES.register("release_shikigami", ReleaseShikigami::new);
   public static RegistryObject<Ability> SHADOW_STORAGE = ABILITIES.register("shadow_storage", ShadowStorage::new);
   public static RegistryObject<Ability> SHADOW_TRAVEL = ABILITIES.register("shadow_travel", ShadowTravel::new);
   public static RegistryObject<Ability> NUE_LIGHTNING = ABILITIES.register("nue_lightning", NueLightning::new);
   public static RegistryObject<Ability> NUE_TOTALITY_LIGHTNING = ABILITIES.register("nue_totality_lightning", NueTotalityLightning::new);
   public static RegistryObject<Ability> PIERCING_WATER = ABILITIES.register("piercing_water", PiercingWater::new);
   public static RegistryObject<Summon<?>> WHEEL = ABILITIES.register("wheel", Wheel::new);
   public static RegistryObject<Ability> GREAT_SERPENT_GRAB = ABILITIES.register("great_serpent_grab", GreatSerpentGrab::new);
   public static RegistryObject<Summon<?>> MAHORAGA = ABILITIES.register("mahoraga", Mahoraga::new);
   public static RegistryObject<Summon<?>> DIVINE_DOGS = ABILITIES.register("divine_dogs", DivineDogs::new);
   public static RegistryObject<Summon<?>> DIVINE_DOG_TOTALITY = ABILITIES.register("divine_dog_totality", DivineDogTotality::new);
   public static RegistryObject<Summon<?>> TOAD = ABILITIES.register("toad", Toad::new);
   public static RegistryObject<Summon<?>> TOAD_FUSION = ABILITIES.register("toad_fusion", ToadFusion::new);
   public static RegistryObject<Summon<?>> RABBIT_ESCAPE = ABILITIES.register("rabbit_escape", RabbitEscape::new);
   public static RegistryObject<Summon<?>> NUE = ABILITIES.register("nue", Nue::new);
   public static RegistryObject<Summon<?>> NUE_TOTALITY = ABILITIES.register("nue_totality", NueTotality::new);
   public static RegistryObject<Summon<?>> GREAT_SERPENT = ABILITIES.register("great_serpent", GreatSerpent::new);
   public static RegistryObject<Summon<?>> MAX_ELEPHANT = ABILITIES.register("max_elephant", MaxElephant::new);
   public static RegistryObject<Summon<?>> TRANQUIL_DEER = ABILITIES.register("tranquil_deer", TranquilDeer::new);
   public static RegistryObject<Summon<?>> PIERCING_BULL = ABILITIES.register("piercing_bull", PiercingBull::new);
   public static RegistryObject<Summon<?>> AGITO = ABILITIES.register("agito", Agito::new);
   public static RegistryObject<Ability> CHIMERA_SHADOW_GARDEN = ABILITIES.register("chimera_shadow_garden", ChimeraShadowGarden::new);
   public static RegistryObject<Ability> CURSE_ABSORPTION = ABILITIES.register("curse_absorption", CurseAbsorption::new);
   public static RegistryObject<Ability> RELEASE_CURSE = ABILITIES.register("release_curse", ReleaseCurse::new);
   public static RegistryObject<Ability> RELEASE_CURSES = ABILITIES.register("release_curses", ReleaseCurses::new);
   public static RegistryObject<Ability> SUMMON_ALL = ABILITIES.register("summon_all", SummonAll::new);
   public static RegistryObject<Ability> ENHANCE_CURSE = ABILITIES.register("enhance_curse", EnhanceCurse::new);
   public static RegistryObject<Ability> MAXIMUM_UZUMAKI = ABILITIES.register("maximum_uzumaki", MaximumUzumaki::new);
   public static RegistryObject<Ability> MINI_UZUMAKI = ABILITIES.register("mini_uzumaki", MiniUzumaki::new);
   public static RegistryObject<Ability> WORM_CURSE_GRAB = ABILITIES.register("worm_curse_grab", WormCurseGrab::new);
   public static RegistryObject<Ability> FISH_SWARM = ABILITIES.register("fish_swarm", FishSwarm::new);
   public static RegistryObject<Ability> RETURN = ABILITIES.register("return", Return::new);
   public static RegistryObject<Ability> SHUT_IT = ABILITIES.register("shut_it", ShutIt::new);
   public static RegistryObject<Ability> BLINK = ABILITIES.register("blink", Blink::new);
   public static RegistryObject<Ability> RUN_AWAY = ABILITIES.register("run_away", RunAway::new);
   public static RegistryObject<Ability> BURN_UP = ABILITIES.register("burn_up", BurnUp::new);
   public static RegistryObject<Ability> DONT_MOVE = ABILITIES.register("dont_move", DontMove::new);
   public static RegistryObject<Ability> GET_CRUSHED = ABILITIES.register("get_crushed", GetCrushed::new);
   public static RegistryObject<Ability> BLAST_AWAY = ABILITIES.register("blast_away", BlastAway::new);
   public static RegistryObject<Ability> EXPLODE = ABILITIES.register("explode", Explode::new);
   public static RegistryObject<Ability> DIE = ABILITIES.register("die", Die::new);
   public static RegistryObject<Ability> SWAP_SELF = ABILITIES.register("swap_self", SwapSelf::new);
   public static RegistryObject<Ability> SWAP_OTHERS = ABILITIES.register("swap_others", SwapOthers::new);
   public static RegistryObject<Ability> FEINT = ABILITIES.register("feint", Feint::new);
   public static RegistryObject<Ability> CE_THROW = ABILITIES.register("ce_throw", CEThrow::new);
   public static RegistryObject<Ability> ITEM_SWAP = ABILITIES.register("item_swap", ItemSwap::new);
   public static RegistryObject<Ability> SHUFFLE = ABILITIES.register("shuffle", Shuffle::new);
   public static RegistryObject<Ability> PROJECTION_SORCERY = ABILITIES.register("projection_sorcery", ProjectionSorcery::new);
   public static RegistryObject<Ability> TWENTY_FOUR_FRAME_RULE = ABILITIES.register("twenty_four_frame_rule", TwentyFourFrameRule::new);
   public static RegistryObject<Ability> AIR_FRAME = ABILITIES.register("air_frame", AirFrame::new);
   public static RegistryObject<Ability> TIME_CELL_MOON_PALACE = ABILITIES.register("time_cell_moon_palace", TimeCellMoonPalace::new);
   public static RegistryObject<Ability> COLLAPSE = ABILITIES.register("collapse", Collapse::new);
   public static RegistryObject<Ability> RATIO_RULE = ABILITIES.register("ratio_rule", RatioRule::new);
   public static RegistryObject<Ability> BISECTION = ABILITIES.register("bisection", Bisection::new);

   public static String getName(Ability ability) {
      return getKey(ability).getPath();
   }

   public static ResourceLocation getKey(Ability ability) {
      return ABILITY_REGISTRY.get().getKey(ability);
   }

   public static Ability getValue(ResourceLocation key) {
      return (Ability)ABILITY_REGISTRY.get().getValue(key);
   }

   public static boolean hasToggled(LivingEntity owner, Ability ability) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.hasToggled(ability);
      }
   }

   @Nullable
   public static CursedSpirit createCurse(LivingEntity owner, AbsorbedCurse curse) {
      CursedSpirit entity = curse.getType() == EntityType.PLAYER
         ? (CursedSpirit)((EntityType)JJKEntities.ABSORBED_PLAYER.get()).create(owner.level())
         : (CursedSpirit)curse.getType().create(owner.level());
      if (entity == null) {
         return null;
      } else {
         entity.setTame(true);
         entity.setOwner(owner);
         GameProfile profile = curse.getProfile();
         if (profile != null && entity instanceof AbsorbedPlayerEntity absorbed) {
            absorbed.setPlayer(profile);
         }

         Vec3 direction = RotationUtil.calculateViewVector(0.0F, owner.getYRot());
         Vec3 pos = owner.position().subtract(direction.multiply(entity.getBbWidth(), 0.0, entity.getBbWidth()));
         entity.moveTo(pos.x, pos.y, pos.z, owner.getYRot(), owner.getXRot());
         return entity;
      }
   }

   public static float getCurseExperience(AbsorbedCurse curse) {
      ISorcererData data = new SorcererData();
      data.deserializeNBT(curse.getData());
      return data.getExperience();
   }

   public static float getCurseCost(AbsorbedCurse curse) {
      return Math.max(1.0F, getCurseExperience(curse) * 0.1F);
   }

   @Nullable
   public static Entity summonCurse(LivingEntity owner, AbsorbedCurse curse, boolean charge) {
      ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
      List<AbsorbedCurse> curses = cap.getCurses();
      return !curses.contains(curse) ? null : summonCurse(owner, curses.indexOf(curse), charge);
   }

   @Nullable
   public static Entity summonCurse(LivingEntity owner, int index, boolean charge) {
      if (!owner.hasEffect((MobEffect)JJKEffects.UNLIMITED_VOID.get()) && !hasToggled(owner, (Ability)DOMAIN_AMPLIFICATION.get())) {
         ISorcererData ownerCap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         List<AbsorbedCurse> curses = ownerCap.getCurses();
         if (index >= curses.size()) {
            return null;
         } else {
            AbsorbedCurse curse = curses.get(index);
            if (charge) {
               float cost = getCurseCost(curse);
               if (!(owner instanceof Player player && player.getAbilities().instabuild)) {
                  if (ownerCap.getEnergy() < cost) {
                     return null;
                  }

                  ownerCap.useEnergy(cost);
               }
            }

            CursedSpirit entity = createCurse(owner, curse);
            if (entity == null) {
               return null;
            } else {
               owner.level().addFreshEntity(entity);
               ISorcererData curseCap = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
               curseCap.deserializeNBT(curse.getData());
               ownerCap.addSummon(entity);
               ownerCap.removeCurse(curse);
               if (owner instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(ownerCap.serializeNBT()), player);
               }

               return entity;
            }
         }
      } else {
         return null;
      }
   }

   public static Set<CursedTechnique> getTechniques(LivingEntity owner) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return Set.of();
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getTechniques();
      }
   }

   @Nullable
   public static CursedTechnique getTechnique(Ability ability) {
      for (CursedTechnique technique : CursedTechnique.values()) {
         if (List.of(technique.getAbilities()).contains(ability)) {
            return technique;
         }
      }

      return null;
   }

   @Nullable
   public static JujutsuType getType(LivingEntity owner) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return null;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getType();
      }
   }

   public static Set<Ability> getToggled(LivingEntity owner) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return Set.of();
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.getToggled();
      }
   }

   public static boolean hasTamed(LivingEntity owner, EntityType<?> type) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         for (RegistryObject<Ability> ability : ABILITIES.getEntries()) {
            if (ability.get() instanceof Summon<?> summon && summon.getTypes().contains(type)) {
               return summon.isTamed(owner);
            }
         }

         return false;
      }
   }

   public static boolean isDead(LivingEntity owner, EntityType<?> type) {
      if (!owner.getCapability(TenShadowsDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ITenShadowsData cap = (ITenShadowsData)owner.getCapability(TenShadowsDataHandler.INSTANCE).resolve().orElseThrow();
         Registry<EntityType<?>> registry = owner.level().registryAccess().registryOrThrow(Registries.ENTITY_TYPE);
         return cap.isDead(registry, type);
      }
   }

   public static boolean isChanneling(LivingEntity owner, Ability ability) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.isChanneling(ability);
      }
   }

   public static boolean hasTrait(LivingEntity owner, Trait trait) {
      if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return false;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         return cap.hasTrait(trait);
      }
   }

   public static List<Ability> getAbilities() {
      return ABILITIES.getEntries().stream().<Ability>map(RegistryObject::get).collect(Collectors.toList());
   }

   public static List<Ability> getAbilities(LivingEntity owner) {
      Set<Ability> abilities = new LinkedHashSet<>(List.of((Ability)HEAL.get(), (Ability)RCT1.get(), (Ability)RCT2.get(), (Ability)RCT3.get()));
      if (owner instanceof JogoatEntity) {
         for (CursedTechnique technique : CursedTechnique.values()) {
            abilities.addAll(Arrays.asList(technique.getAbilities()));
            Ability domain = technique.getDomain();
            if (domain != null) {
               abilities.add(domain);
            }
         }

         return new ArrayList<>(abilities);
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (owner instanceof ISorcerer sorcerer) {
            abilities.addAll(sorcerer.getCustom());
         }

         for (RegistryObject<Ability> entry : ABILITIES.getEntries()) {
            Ability ability = (Ability)entry.get();
            if ((!ability.isTechnique() || ability.isTechnique() && ability.specialObtainment())
               && (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION) || ability.getCost(owner) == 0.0F)) {
               abilities.add(ability);
            }
         }

         if (!cap.hasTrait(Trait.HEAVENLY_RESTRICTION)) {
            for (CursedTechnique techniquex : cap.getTechniques()) {
               abilities.addAll(Arrays.asList(techniquex.getAbilities()));
            }

            CursedTechnique techniquex = cap.getTechnique();
            if (techniquex != null && techniquex.getDomain() != null) {
               abilities.add(techniquex.getDomain());
            }

            if (cap.hasTechnique(CursedTechnique.BRAIN_TRANSPLANT)) {
               CursedTechnique stolen = cap.getLastStolen();
               if (stolen != null && stolen.getDomain() != null) {
                  abilities.add(stolen.getDomain());
               }
            }
         }

         abilities.addAll(cap.getBorrowed());
         abilities.removeIf(abilityx -> !abilityx.isValid(owner) && !(owner instanceof ISorcerer sorcerer && sorcerer.getCustom().contains(abilityx)));
         return new ArrayList<>(abilities);
      }
   }
}
