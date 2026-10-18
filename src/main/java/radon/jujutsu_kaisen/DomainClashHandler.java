package radon.jujutsu_kaisen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.entity.IncompleteDomainEntity;
import radon.jujutsu_kaisen.entity.OpenDomainExpansionEntity;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.DomainClashS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE
)
public class DomainClashHandler {
   private static final float BASE_DRAIN = 0.5F;
   private static final float REGEN = 0.5F;
   private static final float TAKEOVER_SHARE = 0.5F;
   private static final int SYNC_INTERVAL = 2;
   private static final float MIN_STRENGTH = 0.01F;
   private static final float SHELL_ADVANTAGE = 1.5F;
   private static final float SHELL_PENALTY = 0.6666667F;
   private static final Set<UUID> reported = new HashSet<>();

   @SubscribeEvent
   public static void onLevelTick(LevelTickEvent event) {
      if (event.phase != Phase.START && event.side != LogicalSide.CLIENT) {
         if (event.level instanceof ServerLevel level) {
            List<DomainExpansionEntity> domains = collect(level);
            if (!domains.isEmpty()) {
               Set<UUID> live = new HashSet<>();

               for (DomainExpansionEntity domain : domains) {
                  live.add(domain.getUUID());
               }

               reported.retainAll(live);
               Set<DomainExpansionEntity> drained = new HashSet<>();
               Set<DomainExpansionEntity> clashing = new HashSet<>();

               for (List<DomainExpansionEntity> group : group(domains)) {
                  if (group.size() >= 2) {
                     markClashing(group, clashing);
                     tickClash(level, group, drained);
                  }
               }

               for (DomainExpansionEntity domain : domains) {
                  domain.setClashing(clashing.contains(domain));
                  if (!drained.contains(domain)) {
                     domain.setClashHealth(domain.getClashHealth() + 0.5F);
                  }
               }
            }
         }
      }
   }

   private static List<DomainExpansionEntity> collect(ServerLevel level) {
      Set<DomainExpansionEntity> result = new LinkedHashSet<>();

      for (IDomainBarrier barrier : VeilHandler.getDomainBarriers(level)) {
         for (DomainExpansionEntity domain : barrier.getClashers()) {
            if (domain != null && !domain.isRemoved() && domain.isAlive() && !domain.isInstant() && domain.getBounds() != null) {
               result.add(domain);
            }
         }
      }

      return new ArrayList<>(result);
   }

   private static List<List<DomainExpansionEntity>> group(List<DomainExpansionEntity> domains) {
      List<List<DomainExpansionEntity>> groups = new ArrayList<>();
      Set<DomainExpansionEntity> assigned = new HashSet<>();

      for (DomainExpansionEntity domain : domains) {
         if (assigned.add(domain)) {
            List<DomainExpansionEntity> group = new ArrayList<>();
            group.add(domain);

            for (int i = 0; i < group.size(); i++) {
               AABB bounds = group.get(i).getBounds();

               for (DomainExpansionEntity other : domains) {
                  if (!assigned.contains(other) && bounds.intersects(other.getBounds())) {
                     assigned.add(other);
                     group.add(other);
                  }
               }
            }

            groups.add(group);
         }
      }

      return groups;
   }

   private static void report(ServerLevel level, List<DomainExpansionEntity> group) {
      List<String> out = new ArrayList<>();
      out.add("[JJK-CLASH] tick " + level.getGameTime() + ", " + group.size() + " domains in contact");

      for (DomainExpansionEntity domain : group) {
         LivingEntity owner = domain.getOwner();
         String who = owner == null ? "<no owner>" : owner.getName().getString();
         String what = domain.getAbility() == null ? "<no technique>" : JJKAbilities.getKey(domain.getAbility()).getPath();
         String kind = domain instanceof OpenDomainExpansionEntity ? "open" : (isIncomplete(domain) ? "incomplete" : "closed");
         out.add("  " + who + "  -  " + what + "  (" + kind + ")");
         out.add(String.format("      health       %.1f / %.1f", domain.getClashHealth(), domain.getMaxClashHealth()));
         out.add(String.format("      raw power    %.3f   <- the caster's cursed energy experience", domain.getClashStrength()));
         out.add(String.format("      shell        %s, factor %.2f", domain.getShellBalance() ? "outer" : "inner", shellFactor(domain)));
         out.add(String.format("      contest str  %.3f   <- raw power x shell factor", clashStrength(domain)));
         float clashing = 0.0F;
         float siege = 0.0F;

         for (DomainExpansionEntity other : group) {
            if (other != domain && drains(other, domain)) {
               LivingEntity attacker = other.getOwner();
               String name = attacker == null ? "<no owner>" : attacker.getName().getString();
               if (isSiege(other, domain)) {
                  float amount = 0.5F * (strength(other) / strength(domain)) * shellFactor(domain);
                  siege += amount;
                  out.add(String.format("      besieged by %s for %.4f/tick", name, amount));
               } else {
                  clashing += clashStrength(other);
                  out.add(String.format("      contested by %s at %.3f", name, clashStrength(other)));
               }
            }
         }

         float fromClash = clashing > 0.0F ? 0.5F * (clashing / clashStrength(domain)) : 0.0F;
         float total = siege + fromClash;
         if (total > 0.0F) {
            out.add(
               String.format(
                  "      losing       %.4f/tick  (siege %.4f + contest %.4f), empty in %ds",
                  total,
                  siege,
                  fromClash,
                  Math.round(domain.getClashHealth() / total / 20.0F)
               )
            );
         } else {
            out.add("      losing       nothing, regenerating");
         }
      }

      System.out.println(String.join(System.lineSeparator(), out));
   }

   private static void tickClash(ServerLevel level, List<DomainExpansionEntity> group, Set<DomainExpansionEntity> drained) {
      boolean fresh = false;

      for (DomainExpansionEntity domain : group) {
         if (reported.add(domain.getUUID())) {
            fresh = true;
         }
      }

      if (fresh) {
         report(level, group);
      }

      List<DomainExpansionEntity> losers = new ArrayList<>();
      boolean fighting = false;

      for (DomainExpansionEntity domainx : group) {
         float clashing = 0.0F;
         float siege = 0.0F;

         for (DomainExpansionEntity other : group) {
            if (other != domainx && drains(other, domainx)) {
               if (isSiege(other, domainx)) {
                  siege += 0.5F * (strength(other) / strength(domainx)) * shellFactor(domainx);
               } else {
                  clashing += clashStrength(other);
               }
            }
         }

         float drain = siege + (clashing > 0.0F ? 0.5F * (clashing / clashStrength(domainx)) : 0.0F);
         if (!(drain <= 0.0F)) {
            fighting = true;
            drained.add(domainx);
            domainx.setClashHealth(domainx.getClashHealth() - drain);
            if (domainx.getClashHealth() <= 0.0F) {
               losers.add(domainx);
            }
         }
      }

      if (fighting) {
         if (!losers.isEmpty()) {
            resolve(group, losers);
         } else {
            if (level.getGameTime() % 2L == 0L) {
               broadcast(level, group);
            }
         }
      }
   }

   private static boolean drains(DomainExpansionEntity attacker, DomainExpansionEntity target) {
      boolean attackerOpen = attacker instanceof OpenDomainExpansionEntity;
      boolean targetOpen = target instanceof OpenDomainExpansionEntity;
      if (attackerOpen == targetOpen) {
         return true;
      } else {
         DomainExpansionEntity open = attackerOpen ? attacker : target;
         DomainExpansionEntity closed = attackerOpen ? target : attacker;
         return isCaught(open, closed) ? true : attackerOpen;
      }
   }

   private static boolean isSiege(DomainExpansionEntity attacker, DomainExpansionEntity target) {
      if (!(attacker instanceof OpenDomainExpansionEntity)) {
         return false;
      } else {
         return target instanceof OpenDomainExpansionEntity ? false : !isCaught(attacker, target);
      }
   }

   private static boolean isCaught(DomainExpansionEntity open, DomainExpansionEntity closed) {
      LivingEntity owner = open.getOwner();
      return owner != null && closed.isInsideBarrier(owner.blockPosition());
   }

   private static void markClashing(List<DomainExpansionEntity> group, Set<DomainExpansionEntity> clashing) {
      for (DomainExpansionEntity domain : group) {
         for (DomainExpansionEntity other : group) {
            if (other != domain && drains(other, domain) && drains(domain, other) && (!isIncomplete(other) || isIncomplete(domain))) {
               clashing.add(domain);
               break;
            }
         }
      }
   }

   private static boolean isIncomplete(DomainExpansionEntity domain) {
      return domain instanceof IncompleteDomainEntity incomplete && incomplete.isIncomplete();
   }

   private static float strength(DomainExpansionEntity domain) {
      return Math.max(0.01F, domain.getClashStrength());
   }

   private static float clashStrength(DomainExpansionEntity domain) {
      return Math.max(0.01F, strength(domain) * shellFactor(domain));
   }

   private static float shellFactor(DomainExpansionEntity domain) {
      if (domain instanceof OpenDomainExpansionEntity) {
         return 1.0F;
      } else {
         return domain.getShellBalance() ? 0.6666667F : 1.5F;
      }
   }

   private static void resolve(List<DomainExpansionEntity> group, List<DomainExpansionEntity> losers) {
      List<DomainExpansionEntity> survivors = new ArrayList<>(group);
      survivors.removeAll(losers);

      for (DomainExpansionEntity loser : losers) {
         loser.discard();
      }

      for (DomainExpansionEntity survivor : survivors) {
         if (!payTakeover(survivor)) {
            survivor.discard();
         } else if (survivor instanceof OpenDomainExpansionEntity open) {
            open.reexpand();
         }
      }
   }

   private static boolean payTakeover(DomainExpansionEntity domain) {
      LivingEntity owner = domain.getOwner();
      if (owner == null) {
         return false;
      } else if (owner instanceof Player player && player.getAbilities().instabuild) {
         return true;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         if (cap == null) {
            return false;
         } else {
            float cost = (domain.getAbility() == null ? 1000.0F : domain.getAbility().getRealCost(owner, 1000.0F)) * 0.5F;
            if (cap.getEnergy() < cost) {
               return false;
            } else {
               cap.useEnergy(cost);
               if (owner instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(cap.serializeNBT()), player);
               }

               return true;
            }
         }
      }
   }

   private static void broadcast(ServerLevel level, List<DomainExpansionEntity> group) {
      List<DomainExpansionEntity> ordered = new ArrayList<>(group);
      ordered.sort(Comparator.comparing(Entity::getUUID));
      Set<ServerPlayer> viewers = new HashSet<>();

      for (ServerPlayer player : level.players()) {
         for (DomainExpansionEntity domain : group) {
            if (domain.isInsideBarrier(player.blockPosition())) {
               viewers.add(player);
               break;
            }
         }
      }

      if (!viewers.isEmpty()) {
         for (ServerPlayer viewer : viewers) {
            List<DomainClashS2CPacket.Entry> entries = new ArrayList<>();

            for (DomainExpansionEntity domainx : ordered) {
               LivingEntity owner = domainx.getOwner();
               entries.add(new DomainClashS2CPacket.Entry(owner == null ? "?" : owner.getName().getString(), domainx.getClashFraction(), owner == viewer));
            }

            entries.sort((a, b) -> Boolean.compare(b.self(), a.self()));
            PacketHandler.sendToClient(new DomainClashS2CPacket(entries), viewer);
         }
      }
   }
}
