package radon.jujutsu_kaisen.capability.data.sorcerer;

import java.util.List;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;

public enum CursedTechnique {
   SHOCKWAVE(null, (Ability)JJKAbilities.SHOCKWAVE.get()),
   SKY_STRIKE(null, (Ability)JJKAbilities.SKY_STRIKE.get()),
   BRAIN_TRANSPLANT(null, (Ability)JJKAbilities.BODY_STEAL.get()),
   ANGEL(
      null,
      (Ability)JJKAbilities.ANGEL_WINGS.get(),
      (Ability)JJKAbilities.JACOBS_LADDER.get(),
      (Ability)JJKAbilities.MAXIMUM_OUTPUT_JACOBS_LADDER.get(),
      (Ability)JJKAbilities.BARRIER_TRAVEL.get()
   ),
   CURSE_MANIPULATION(
      null,
      (Ability)JJKAbilities.CURSE_ABSORPTION.get(),
      (Ability)JJKAbilities.RELEASE_CURSE.get(),
      (Ability)JJKAbilities.RELEASE_CURSES.get(),
      (Ability)JJKAbilities.SUMMON_ALL.get(),
      (Ability)JJKAbilities.ENHANCE_CURSE.get(),
      (Ability)JJKAbilities.MAXIMUM_UZUMAKI.get(),
      (Ability)JJKAbilities.MINI_UZUMAKI.get(),
      (Ability)JJKAbilities.WORM_CURSE_GRAB.get(),
      (Ability)JJKAbilities.FISH_SWARM.get()
   ),
   LIMITLESS(
      (Ability)JJKAbilities.UNLIMITED_VOID.get(),
      (Ability)JJKAbilities.INFINITY.get(),
      (Ability)JJKAbilities.RED.get(),
      (Ability)JJKAbilities.BLUE_STILL.get(),
      (Ability)JJKAbilities.BLUE_MOTION.get(),
      (Ability)JJKAbilities.BLUE_FISTS.get(),
      (Ability)JJKAbilities.HOLLOW_PURPLE.get(),
      (Ability)JJKAbilities.TELEPORT.get(),
      (Ability)JJKAbilities.FLY.get()
   ),
   SHRINE(
      (Ability)JJKAbilities.MALEVOLENT_SHRINE.get(),
      (Ability)JJKAbilities.DISMANTLE.get(),
      (Ability)JJKAbilities.DISMANTLE_NET.get(),
      (Ability)JJKAbilities.DISMANTLE_SKATING.get(),
      (Ability)JJKAbilities.CLEAVE.get(),
      (Ability)JJKAbilities.SPIDERWEB.get(),
      (Ability)JJKAbilities.FIRE_ARROW.get()
   ),
   CURSED_SPEECH(
      null,
      (Ability)JJKAbilities.RETURN.get(),
      (Ability)JJKAbilities.SHUT_IT.get(),
      (Ability)JJKAbilities.BLINK.get(),
      (Ability)JJKAbilities.BURN_UP.get(),
      (Ability)JJKAbilities.RUN_AWAY.get(),
      (Ability)JJKAbilities.DONT_MOVE.get(),
      (Ability)JJKAbilities.GET_CRUSHED.get(),
      (Ability)JJKAbilities.BLAST_AWAY.get(),
      (Ability)JJKAbilities.EXPLODE.get(),
      (Ability)JJKAbilities.DIE.get()
   ),
   MIMICRY(null, (Ability)JJKAbilities.RIKA.get(), (Ability)JJKAbilities.MIMICRY.get(), (Ability)JJKAbilities.COMMAND_PURE_LOVE.get()),
   DISASTER_FLAMES(
      (Ability)JJKAbilities.COFFIN_OF_THE_IRON_MOUNTAIN.get(),
      (Ability)JJKAbilities.EMBER_INSECTS.get(),
      (Ability)JJKAbilities.EMBER_INSECT_FLIGHT.get(),
      (Ability)JJKAbilities.VOLCANO.get(),
      (Ability)JJKAbilities.MAXIMUM_METEOR.get(),
      (Ability)JJKAbilities.DISASTER_FLAMES.get(),
      (Ability)JJKAbilities.FLAMETHROWER.get(),
      (Ability)JJKAbilities.FIREBALL.get(),
      (Ability)JJKAbilities.FIRE_BEAM.get()
   ),
   DISASTER_TIDES(
      (Ability)JJKAbilities.HORIZON_OF_THE_CAPTIVATING_SKANDHA.get(),
      (Ability)JJKAbilities.DISASTER_TIDES.get(),
      (Ability)JJKAbilities.WATER_SHIELD.get(),
      (Ability)JJKAbilities.DEATH_SWARM.get(),
      (Ability)JJKAbilities.FISH_SHIKIGAMI.get(),
      (Ability)JJKAbilities.WATER_TORRENT.get(),
      (Ability)JJKAbilities.EEL_GRAPPLE.get()
   ),
   DISASTER_PLANTS(
      (Ability)JJKAbilities.SHINING_SEA_OF_FLOWERS.get(),
      (Ability)JJKAbilities.FOREST_PLATFORM.get(),
      (Ability)JJKAbilities.FOREST_SPIKES.get(),
      (Ability)JJKAbilities.WOOD_SHIELD.get(),
      (Ability)JJKAbilities.CURSED_BUD.get(),
      (Ability)JJKAbilities.FOREST_WAVE.get(),
      (Ability)JJKAbilities.FOREST_ROOTS.get(),
      (Ability)JJKAbilities.FOREST_DASH.get(),
      (Ability)JJKAbilities.DISASTER_PLANT.get()
   ),
   IDLE_TRANSFIGURATION(
      (Ability)JJKAbilities.SELF_EMBODIMENT_OF_PERFECTION.get(),
      (Ability)JJKAbilities.IDLE_TRANSFIGURATION.get(),
      (Ability)JJKAbilities.SOUL_DECIMATION.get(),
      (Ability)JJKAbilities.SOUL_REINFORCEMENT.get(),
      (Ability)JJKAbilities.SOUL_RESTORATION.get(),
      (Ability)JJKAbilities.ARM_BLADE.get(),
      (Ability)JJKAbilities.GUN.get(),
      (Ability)JJKAbilities.HORSE_LEGS.get(),
      (Ability)JJKAbilities.WINGS.get(),
      (Ability)JJKAbilities.SHRINK.get(),
      (Ability)JJKAbilities.TRANSFIGURED_SOUL_SMALL.get(),
      (Ability)JJKAbilities.TRANSFIGURED_SOUL_NORMAL.get(),
      (Ability)JJKAbilities.TRANSFIGURED_SOUL_LARGE.get(),
      (Ability)JJKAbilities.POLYMORPHIC_SOUl_ISOMER.get(),
      (Ability)JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get(),
      (Ability)JJKAbilities.BODY_REPEL.get(),
      (Ability)JJKAbilities.TRANSFIGURED_SOUL_NORMAL.get(),
      (Ability)JJKAbilities.TRANSFIGURED_SOUL_LARGE.get(),
      (Ability)JJKAbilities.POLYMORPHIC_SOUl_ISOMER.get(),
      (Ability)JJKAbilities.INSTANT_SPIRIT_BODY_OF_DISTORTED_KILLING.get()
   ),
   RATIO(null, (Ability)JJKAbilities.COLLAPSE.get(), (Ability)JJKAbilities.RATIO_RULE.get(), (Ability)JJKAbilities.BISECTION.get()),
   TEN_SHADOWS(
      (Ability)JJKAbilities.CHIMERA_SHADOW_GARDEN.get(),
      (Ability)JJKAbilities.SWITCH_MODE.get(),
      (Ability)JJKAbilities.RELEASE_SHIKIGAMI.get(),
      (Ability)JJKAbilities.SHADOW_STORAGE.get(),
      (Ability)JJKAbilities.SHADOW_TRAVEL.get(),
      (Ability)JJKAbilities.NUE_LIGHTNING.get(),
      (Ability)JJKAbilities.PIERCING_WATER.get(),
      (Ability)JJKAbilities.WHEEL.get(),
      (Ability)JJKAbilities.GREAT_SERPENT_GRAB.get(),
      (Ability)JJKAbilities.DIVINE_DOGS.get(),
      (Ability)JJKAbilities.DIVINE_DOG_TOTALITY.get(),
      (Ability)JJKAbilities.TOAD.get(),
      (Ability)JJKAbilities.TOAD_FUSION.get(),
      (Ability)JJKAbilities.GREAT_SERPENT.get(),
      (Ability)JJKAbilities.NUE.get(),
      (Ability)JJKAbilities.NUE_TOTALITY.get(),
      (Ability)JJKAbilities.MAX_ELEPHANT.get(),
      (Ability)JJKAbilities.RABBIT_ESCAPE.get(),
      (Ability)JJKAbilities.TRANQUIL_DEER.get(),
      (Ability)JJKAbilities.PIERCING_BULL.get(),
      (Ability)JJKAbilities.AGITO.get(),
      (Ability)JJKAbilities.MAHORAGA.get()
   ),
   BOOGIE_WOOGIE(
      null,
      (Ability)JJKAbilities.SWAP_SELF.get(),
      (Ability)JJKAbilities.SWAP_OTHERS.get(),
      (Ability)JJKAbilities.FEINT.get(),
      (Ability)JJKAbilities.CE_THROW.get(),
      (Ability)JJKAbilities.ITEM_SWAP.get(),
      (Ability)JJKAbilities.SHUFFLE.get()
   ),
   MYTHICAL_BEAST_AMBER(
      null, (Ability)JJKAbilities.MYTHICAL_BEAST_AMBER.get(), (Ability)JJKAbilities.EMF_BLAST.get(), (Ability)JJKAbilities.ELECTRIC_BEAM.get()
   ),
   TECHNIQUELESS(null),
   PROJECTION_SORCERY(
      (Ability)JJKAbilities.TIME_CELL_MOON_PALACE.get(),
      (Ability)JJKAbilities.PROJECTION_SORCERY.get(),
      (Ability)JJKAbilities.TWENTY_FOUR_FRAME_RULE.get(),
      (Ability)JJKAbilities.AIR_FRAME.get()
   ),
   CURSED_ENERGY_DISCHARGE(
      (Ability)JJKAbilities.SWEET_BURNING_MATSURI.get(),
      (Ability)JJKAbilities.GRANITE_BLAST.get(),
      (Ability)JJKAbilities.GRANITE_BLAST_VOLLEY.get(),
      (Ability)JJKAbilities.GRANITE_RING.get(),
      (Ability)JJKAbilities.MAXIMUM_GRANITE_BEAM.get()
   );

   @Nullable
   private final Ability domain;
   private final Ability[] abilities;
   public static final List<CursedTechnique> CLAN_TECHNIQUES = List.of(PROJECTION_SORCERY, TEN_SHADOWS);

   private CursedTechnique(@Nullable Ability domain, Ability... abilities) {
      this.domain = domain;
      this.abilities = abilities;
   }

   @Nullable
   public Ability getDomain() {
      return this.domain;
   }

   public Ability[] getAbilities() {
      return this.abilities;
   }

   public Component getName() {
      return Component.translatable(String.format("cursed_technique.%s.%s", "jujutsu_kaisen", this.name().toLowerCase()));
   }
}
