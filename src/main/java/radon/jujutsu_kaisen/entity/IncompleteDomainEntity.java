package radon.jujutsu_kaisen.entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import radon.jujutsu_kaisen.VeilHandler;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.MenuType;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.DomainExpansion;
import radon.jujutsu_kaisen.ability.cursed_speech.CursedSpeech;
import radon.jujutsu_kaisen.ability.misc.IncompleteDomain;
import radon.jujutsu_kaisen.block.JJKBlocks;
import radon.jujutsu_kaisen.block.base.ITemporaryBlockEntity;
import radon.jujutsu_kaisen.block.entity.DomainBlockEntity;
import radon.jujutsu_kaisen.block.entity.IDomainBarrier;
import radon.jujutsu_kaisen.block.entity.IVeil;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.particle.CursedEnergyParticle;
import radon.jujutsu_kaisen.entity.base.DomainExpansionEntity;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.s2c.EquipAbilityS2CPacket;
import radon.jujutsu_kaisen.network.packet.s2c.SyncSorcererDataS2CPacket;
import radon.jujutsu_kaisen.sound.JJKSounds;
import radon.jujutsu_kaisen.util.RotationUtil;

public class IncompleteDomainEntity extends DomainExpansionEntity implements IDomainBarrier {
   private static final EntityDataAccessor<BlockPos> DATA_MIN = SynchedEntityData.defineId(IncompleteDomainEntity.class, EntityDataSerializers.BLOCK_POS);
   private static final EntityDataAccessor<BlockPos> DATA_MAX = SynchedEntityData.defineId(IncompleteDomainEntity.class, EntityDataSerializers.BLOCK_POS);
   private static final int BLOCKS_PER_TICK = 256;
   private static final float INCOMPLETE_CLASH_SHARE = 0.5F;
   private static final int BREACH_REACH = 2;
   private static final int PULL_REACH = 2;
   private static final int SHELTER_HEIGHT = 3;
   private static final int DISABLE_DURATION = 60;
   private static final int DISABLE_REFRESH = 20;
   private static final int STEAL_INTERVAL = 200;
   private static final int MARKER_COUNT = 5;
   private static final int MARKER_INTERVAL = 5;
   private static final double MARKER_REACH = 2.5;
   private static final double SWAP_RANGE = 50.0;
   private static final Vector3f MARKER_COLOR = new Vector3f(0.45F, 0.8F, 1.0F);
   private static final int SPEAK_INTERVAL = 300;
   private final List<DomainExpansionEntity> clashers = new ArrayList<>();
   private final List<BlockPos> replacedPositions = new ArrayList<>();
   private final List<BlockPos> breachPositions = new ArrayList<>();
   private boolean incomplete = true;
   private List<BlockPos> pending;
   private Set<BlockPos> floors;
   private Set<BlockPos> decorations;
   private int placed;
   private boolean formed;
   @Nullable
   private UUID claimedVeil;
   private final List<BlockPos> markers = new ArrayList<>();
   @Nullable
   private UUID carried;
   private int spoken = -1;
   private int command;
   private int stole = -1;
   private static final int WIDEST = 16;
   private static final int NECK = 2;

   public IncompleteDomainEntity(EntityType<?> pType, Level pLevel) {
      super(pType, pLevel);
   }

   public IncompleteDomainEntity(LivingEntity owner, DomainExpansion ability, IncompleteDomain.Room room) {
      this(owner, ability, room, true, true);
   }

   public IncompleteDomainEntity(LivingEntity owner, DomainExpansion ability, IncompleteDomain.Room room, boolean incomplete) {
      this(owner, ability, room, incomplete, true);
   }

   public IncompleteDomainEntity(LivingEntity owner, DomainExpansion ability, IncompleteDomain.Room room, boolean incomplete, boolean seals) {
      super((EntityType<?>)JJKEntities.INCOMPLETE_DOMAIN.get(), owner, ability);
      this.incomplete = incomplete;
      this.setClashHealth(this.getMaxClashHealth());
      this.formed = !seals;
      this.moveTo(owner.getX(), owner.getY(), owner.getZ(), owner.getYRot(), owner.getXRot());
      this.entityData.set(DATA_MIN, room.min());
      this.entityData.set(DATA_MAX, room.max());
      this.clashers.add(this);
      VeilEntity veil = VeilHandler.getVeilAt(owner.level(), owner.blockPosition());
      if (veil != null) {
         this.claimedVeil = veil.getUUID();
      }
   }

   @Nullable
   private VeilEntity claimed() {
      if (this.claimedVeil != null && this.level() instanceof ServerLevel level) {
         return level.getEntity(this.claimedVeil) instanceof VeilEntity veil && !veil.isRemoved() ? veil : null;
      } else {
         return null;
      }
   }

   public static boolean isClaimed(ServerLevel level, VeilEntity veil) {
      for (IncompleteDomainEntity domain : level.getEntitiesOfClass(IncompleteDomainEntity.class, veil.getBoundingBox().inflate(veil.getRadius()))) {
         if (!domain.isRemoved() && veil.getUUID().equals(domain.claimedVeil)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_MIN, BlockPos.ZERO);
      this.entityData.define(DATA_MAX, BlockPos.ZERO);
   }

   public BlockPos getMin() {
      return (BlockPos)this.entityData.get(DATA_MIN);
   }

   public BlockPos getMax() {
      return (BlockPos)this.entityData.get(DATA_MAX);
   }

   public void onAddedToWorld() {
      super.onAddedToWorld();
      if (!this.level().isClientSide) {
         VeilHandler.barrier(this.level().dimension(), this.getUUID());
      }
   }

   @Override
   public List<DomainExpansionEntity> getClashers() {
      return this.clashers;
   }

   @Override
   public int getRadius() {
      BlockPos min = this.getMin();
      BlockPos max = this.getMax();
      int longest = Math.max(max.getX() - min.getX(), Math.max(max.getY() - min.getY(), max.getZ() - min.getZ()));
      return Math.max(1, longest / 2);
   }

   @Override
   public AABB getBounds() {
      return new AABB(this.getMin(), this.getMax().offset(1, 1, 1));
   }

   @Override
   public boolean isBarrier(BlockPos pos) {
      BlockPos min = this.getMin();
      BlockPos max = this.getMax();
      if (pos.getX() < min.getX() || pos.getX() > max.getX()) {
         return false;
      } else if (pos.getY() < min.getY() || pos.getY() > max.getY()) {
         return false;
      } else {
         return pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ()
            ? pos.getX() == min.getX()
               || pos.getX() == max.getX()
               || pos.getY() == min.getY()
               || pos.getY() == max.getY()
               || pos.getZ() == min.getZ()
               || pos.getZ() == max.getZ()
            : false;
      }
   }

   @Override
   public boolean isInsideBarrier(BlockPos pos) {
      VeilEntity veil = this.claimed();
      if (veil != null) {
         return veil.isInsideBarrier(pos);
      } else {
         BlockPos min = this.getMin();
         BlockPos max = this.getMax();
         return pos.getX() > min.getX()
            && pos.getX() < max.getX()
            && pos.getY() > min.getY()
            && pos.getY() < max.getY()
            && pos.getZ() > min.getZ()
            && pos.getZ() < max.getZ();
      }
   }

   @Override
   public boolean isBarrierOrInside(BlockPos pos) {
      return this.isBarrier(pos) || this.isInsideBarrier(pos);
   }

   public boolean isCompleted() {
      return this.formed;
   }

   public boolean isIncomplete() {
      return this.incomplete;
   }

   @Override
   public float getMaxClashHealth() {
      return this.incomplete ? 50.0F : 100.0F;
   }

   @Override
   public DomainExpansionEntity checkSureHitEffect() {
      if (!this.getSureHitToggled()) {
         return null;
      } else if (this.isClashing()) {
         return null;
      } else {
         return !this.isCompleted() ? null : this;
      }
   }

   @Override
   public DomainExpansionEntity sureHitTarget(LivingEntity target) {
      DomainExpansionEntity surehit = this.checkSureHitEffect();
      return surehit != null && surehit.isAffected(target, false) ? surehit : null;
   }

   private boolean isProtectedByForeignVeil(BlockPos pos) {
      if (this.level() instanceof ServerLevel level) {
         VeilEntity var7 = this.claimed();

         for (IVeil veil : VeilHandler.getVeils(level, pos)) {
            if (veil != var7) {
               int radius = veil.getRadius();
               if (pos.distSqr(veil.getVeil().getCenter()) < (double)radius * radius) {
                  return true;
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean isAffected(BlockPos pos) {
      if (this.isProtectedByForeignVeil(pos)) {
         return false;
      } else {
         for (IDomainBarrier barrier : VeilHandler.getDomainBarriers((ServerLevel)this.level(), pos)) {
            if (barrier != this) {
               return false;
            }
         }

         return super.isAffected(pos);
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (!this.level().isClientSide) {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            this.tickBreach();
            if (!this.formed) {
               this.sealSurfaces();
            } else {
               if (this.checkSureHitEffect() != null) {
                  this.doSureHitEffect();
               }
            }
         }
      }
   }

   private void tickBreach() {
      if (this.incomplete) {
         DomainExpansionEntity opponent = this.getCompleteOpponent();
         if (opponent == null) {
            this.closeBreach();
         } else {
            this.openBreach();
            this.pullOutsidersIn(opponent);
         }
      }
   }

   @Nullable
   private DomainExpansionEntity getCompleteOpponent() {
      for (IDomainBarrier barrier : VeilHandler.getDomainBarriers((ServerLevel)this.level(), this.getBounds())) {
         for (DomainExpansionEntity domain : barrier.getClashers()) {
            if (domain != this
               && !domain.isRemoved()
               && !(domain instanceof IncompleteDomainEntity other && other.isIncomplete())
               && !(domain instanceof OpenDomainExpansionEntity)) {
               return domain;
            }
         }
      }

      return null;
   }

   private void openBreach() {
      if (this.breachPositions.isEmpty()) {
         ServerLevel level = (ServerLevel)this.level();
         BlockPos centre = this.blockPosition().below();

         for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
               BlockPos pos = centre.offset(dx, 0, dz);
               if (this.replace(pos, level, (Block)JJKBlocks.INCOMPLETE_BARRIER.get(), true)) {
                  this.breachPositions.add(pos.immutable());
               }
            }
         }
      }
   }

   private void closeBreach() {
      if (!this.breachPositions.isEmpty()) {
         if (!this.level().isClientSide) {
            ServerLevel level = (ServerLevel)this.level();

            for (BlockPos pos : this.breachPositions) {
               if (level.getBlockEntity(pos) instanceof DomainBlockEntity dbe) {
                  UUID identifier = dbe.getIdentifier();
                  if (identifier != null && identifier.equals(this.getUUID())) {
                     dbe.destroy();
                     this.clearOrphanedBlock(level, pos);
                  }
               }
            }
         }

         this.breachPositions.clear();
      }
   }

   private void clearOrphanedBlock(ServerLevel level, BlockPos pos) {
      if (level.getBlockEntity(pos) instanceof DomainBlockEntity restored) {
         UUID identifier = restored.getIdentifier();
         if (identifier == null || !(level.getEntity(identifier) instanceof DomainExpansionEntity domain && !domain.isRemoved() && domain.isAlive())) {
            restored.destroy();
         }
      }
   }

   @Override
   public Ability getToggledAbility() {
      return (Ability)JJKAbilities.INCOMPLETE_DOMAIN.get();
   }

   @Override
   public void doSureHitEffect() {
      if (this.ability != null) {
         super.doSureHitEffect();
      } else {
         LivingEntity owner = this.getOwner();
         if (owner != null) {
            ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            if (cap != null && cap.getTechnique() != null) {
               switch (cap.getTechnique()) {
                  case ANGEL:
                     this.disableTechniques();
                     break;
                  case MIMICRY:
                     this.stealAbility(owner, cap);
                     break;
                  case BOOGIE_WOOGIE:
                     this.markFloor();
                     break;
                  case CURSED_SPEECH:
                     this.speak(owner);
               }
            }
         }
      }
   }

   private void markFloor() {
      if (this.markers.isEmpty()) {
         this.scatterMarkers();
      }

      if (this.getTime() % 5 == 0) {
         this.showMarkers();
      }
   }

   private void scatterMarkers() {
      List<BlockPos> candidates = this.markerCandidates();
      this.markers.clear();

      for (int i = 0; i < 20 && this.markers.size() < 5; i++) {
         BlockPos pos = this.pickMarker(candidates);
         if (pos != null && !this.markers.contains(pos)) {
            this.markers.add(pos);
         }
      }
   }

   private List<BlockPos> markerCandidates() {
      List<BlockPos> candidates = new ArrayList<>();
      if (this.floors != null) {
         for (BlockPos floor : this.floors) {
            candidates.add(floor.above());
         }
      }

      if (candidates.isEmpty() && this.decorations != null) {
         candidates.addAll(this.decorations);
      }

      return candidates;
   }

   @Nullable
   private BlockPos pickMarker(List<BlockPos> candidates) {
      if (!candidates.isEmpty()) {
         return candidates.get(this.random.nextInt(candidates.size()));
      } else {
         BlockPos min = this.getMin();
         BlockPos max = this.getMax();
         int width = max.getX() - min.getX();
         int depth = max.getZ() - min.getZ();
         return width > 1 && depth > 1
            ? new BlockPos(
               min.getX() + 1 + this.random.nextInt(width - 1), min.getY() + 1, min.getZ() + 1 + this.random.nextInt(depth - 1)
            )
            : null;
      }
   }

   private void showMarkers() {
      if (this.level() instanceof ServerLevel level) {
         for (BlockPos pos : this.markers) {
            level.sendParticles(
               new CursedEnergyParticle.CursedEnergyParticleOptions(MARKER_COLOR, 0.6F, 0.8F, 12),
               pos.getX() + 0.5,
               pos.getY() + 0.4,
               pos.getZ() + 0.5,
               6,
               0.3,
               0.4,
               0.3,
               0.0
            );
         }
      }
   }

   @Nullable
   public static IncompleteDomainEntity boogieWoogie(LivingEntity owner) {
      if (owner.level() instanceof ServerLevel level) {
         for (IncompleteDomainEntity domain : level.getEntitiesOfClass(IncompleteDomainEntity.class, owner.getBoundingBox().inflate(50.0))) {
            if (!domain.isRemoved() && domain.isCompleted() && !domain.markers.isEmpty() && domain.getOwner() == owner) {
               return domain;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean swap(LivingEntity owner) {
      if (this.level() instanceof ServerLevel level) {
         LivingEntity looked = RotationUtil.getExpandedLookAt(owner, 50.0);
         if (looked != null && looked != owner && this.isInsideBarrier(looked.blockPosition())) {
            this.carried = looked.getUUID();
            level.playSound(null, looked.getX(), looked.getY(), looked.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 1.0F, 1.4F);
            return true;
         } else {
            BlockPos mark = this.lookedAtMarker(owner);
            if (mark == null) {
               return false;
            } else if (this.carried == null) {
               return true;
            } else if (level.getEntity(this.carried) instanceof LivingEntity held && !held.isRemoved()) {
               level.playSound(null, held.getX(), held.getY(), held.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
               held.teleportTo(mark.getX() + 0.5, mark.getY(), mark.getZ() + 0.5);
               level.playSound(null, mark.getX(), mark.getY(), mark.getZ(), (SoundEvent)JJKSounds.CLAP.get(), SoundSource.MASTER, 2.0F, 1.0F);
               this.markers.remove(mark);
               BlockPos replacement = this.pickMarker(this.markerCandidates());
               if (replacement != null) {
                  this.markers.add(replacement);
               }

               return true;
            } else {
               this.carried = null;
               return true;
            }
         }
      } else {
         return false;
      }
   }

   @Nullable
   private BlockPos lookedAtMarker(LivingEntity owner) {
      Vec3 eye = owner.getEyePosition();
      Vec3 look = RotationUtil.getTargetAdjustedLookAngle(owner);
      BlockPos best = null;
      double closest = 6.25;

      for (BlockPos pos : this.markers) {
         Vec3 offset = pos.getCenter().subtract(eye);
         double along = offset.dot(look);
         if (!(along <= 0.0) && !(along > 50.0)) {
            double across = offset.subtract(look.scale(along)).lengthSqr();
            if (!(across > closest)) {
               best = pos;
               closest = across;
            }
         }
      }

      return best;
   }

   private void speak(LivingEntity owner) {
      if (this.spoken < 0 || this.getTime() - this.spoken >= 300) {
         this.spoken = this.getTime();
         List<CursedSpeech> vocabulary = new ArrayList<>();

         for (Ability ability : CursedTechnique.CURSED_SPEECH.getAbilities()) {
            if (ability instanceof CursedSpeech speech) {
               vocabulary.add(speech);
            }
         }

         if (!vocabulary.isEmpty()) {
            List<Entity> audience = new ArrayList<>(this.getAffected());
            if (!audience.isEmpty()) {
               CursedSpeech next = vocabulary.get(this.command % vocabulary.size());
               this.command++;
               CursedSpeech.speak(owner, next, audience);
               if (owner instanceof ServerPlayer player) {
                  player.sendSystemMessage(Component.translatable(String.format("chat.%s.domain_speech", "jujutsu_kaisen"), new Object[]{next.getName()}));
               }
            }
         }
      }
   }

   private void returnBorrowed() {
      LivingEntity owner = this.getOwner();
      if (owner != null) {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
         if (cap != null && !cap.getBorrowed().isEmpty()) {
            cap.clearBorrowed();
            if (owner instanceof ServerPlayer player) {
               PacketHandler.sendToClient(new EquipAbilityS2CPacket(EquipAbilityS2CPacket.NOTHING), player);
            }
         }
      }
   }

   private void stealAbility(LivingEntity owner, ISorcererData cap) {
      if (this.stole < 0 || this.getTime() - this.stole >= 200) {
         this.stole = this.getTime();
         List<LivingEntity> victims = new ArrayList<>();

         for (LivingEntity entity : this.getAffected()) {
            ISorcererData data = (ISorcererData)entity.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            if (data != null && data.getTechnique() != null) {
               victims.add(entity);
            }
         }

         if (!victims.isEmpty()) {
            LivingEntity victim = victims.get(this.random.nextInt(victims.size()));
            ISorcererData victimCap = (ISorcererData)victim.getCapability(SorcererDataHandler.INSTANCE).resolve().orElse(null);
            if (victimCap != null && victimCap.getTechnique() != null) {
               List<Ability> stealable = new ArrayList<>();

               for (Ability ability : victimCap.getTechnique().getAbilities()) {
                  if ((ability.getMenuType() == MenuType.MELEE || ability.getMenuType() == MenuType.J2TSU) && !cap.getBorrowed().contains(ability)) {
                     stealable.add(ability);
                  }
               }

               if (!stealable.isEmpty()) {
                  Ability stolen = stealable.get(this.random.nextInt(stealable.size()));
                  cap.borrow(stolen);
                  if (owner instanceof ServerPlayer player) {
                     ResourceLocation key = JJKAbilities.getKey(stolen);
                     if (key != null) {
                        PacketHandler.sendToClient(new EquipAbilityS2CPacket(key), player);
                     }

                     player.sendSystemMessage(Component.translatable(String.format("chat.%s.mimicry_ability", "jujutsu_kaisen"), new Object[]{stolen.getName()}));
                  }
               }
            }
         }
      }
   }

   private void disableTechniques() {
      if (this.getTime() % 20 == 0) {
         for (LivingEntity victim : this.getAffected()) {
            victim.getCapability(SorcererDataHandler.INSTANCE).ifPresent(victimCap -> {
               victimCap.setDisable(60);
               if (victim instanceof ServerPlayer player) {
                  PacketHandler.sendToClient(new SyncSorcererDataS2CPacket(victimCap.serializeNBT()), player);
               }
            });
         }
      }
   }

   public boolean isSheltered(BlockPos pos) {
      if (this.breachPositions.isEmpty()) {
         return false;
      } else {
         BlockPos centre = this.blockPosition().below();
         if (Math.abs(pos.getX() - centre.getX()) > 2) {
            return false;
         } else if (Math.abs(pos.getZ() - centre.getZ()) > 2) {
            return false;
         } else {
            int above = pos.getY() - centre.getY();
            return above >= 0 && above <= 3;
         }
      }
   }

   public static boolean isShelteredFromSureHit(Level level, BlockPos pos) {
      if (!(level instanceof ServerLevel serverLevel)) {
         return false;
      } else {
         for (IDomainBarrier barrier : VeilHandler.getDomainBarriers(serverLevel)) {
            for (DomainExpansionEntity domain : barrier.getClashers()) {
               if (domain instanceof IncompleteDomainEntity incomplete && !incomplete.isRemoved() && incomplete.isIncomplete() && incomplete.isSheltered(pos)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private void pullOutsidersIn(DomainExpansionEntity complete) {
      AABB bounds = complete.getBounds();
      if (bounds != null) {
         for (Player player : this.level().getEntitiesOfClass(Player.class, bounds.inflate(2.0))) {
            if (!complete.isInsideBarrier(player.blockPosition()) && isTouchingBarrier(complete, player.blockPosition())) {
               BlockPos centre = this.blockPosition();
               player.teleportTo(centre.getX() + 0.5, centre.getY(), centre.getZ() + 0.5);
            }
         }
      }
   }

   private static boolean isTouchingBarrier(DomainExpansionEntity complete, BlockPos pos) {
      for (BlockPos nearby : BlockPos.betweenClosed(pos.offset(-2, -2, -2), pos.offset(2, 2, 2))) {
         if (complete.isBarrier(nearby)) {
            return true;
         }
      }

      return false;
   }

   private void sealSurfaces() {
      ServerLevel level = (ServerLevel)this.level();
      if (this.pending == null) {
         this.pending = this.collectSurfaces();
      }

      int budget = 256;

      while (this.placed < this.pending.size() && budget-- > 0) {
         this.seal(this.pending.get(this.placed++), level);
      }

      if (this.placed >= this.pending.size()) {
         this.formed = true;
         this.pending = null;
      }
   }

   private List<BlockPos> collectSurfaces() {
      BlockPos min = this.getMin();
      BlockPos max = this.getMax();
      BlockPos centre = new BlockPos((min.getX() + max.getX()) / 2, (min.getY() + max.getY()) / 2, (min.getZ() + max.getZ()) / 2);
      int radius = Math.max(1, (max.getX() - min.getX()) / 2);
      Set<BlockPos> interior = this.floodInterior(centre, radius);
      this.floors = new HashSet<>();
      this.decorations = new HashSet<>();
      Set<BlockPos> shell = new HashSet<>();

      for (BlockPos pos : interior) {
         for (Direction direction : Direction.values()) {
            BlockPos next = pos.relative(direction);
            if (!interior.contains(next)) {
               shell.add(next);
               if (direction == Direction.DOWN) {
                  this.floors.add(next);
               }
            }
         }
      }

      if (this.ability != null && !this.ability.getDecorationBlocks().isEmpty()) {
         for (BlockPos floor : this.floors) {
            BlockPos above = floor.above();
            if (interior.contains(above)) {
               this.decorations.add(above);
            }
         }
      }

      List<BlockPos> result = new ArrayList<>(shell);
      result.addAll(this.decorations);
      result.sort(Comparator.comparingDouble(pos -> pos.distSqr(centre)));
      return result;
   }

   private static boolean wall(ServerLevel level, BlockPos pos, Set<BlockPos> own) {
      return IncompleteDomain.isSolid(level, pos) && !own.contains(pos);
   }

   private static int width(ServerLevel level, BlockPos pos, Set<BlockPos> own, MutableBlockPos cursor) {
      int narrowest = 16;

      for (Axis axis : new Axis[]{Axis.X, Axis.Z}) {
         int span = 1;

         for (Direction direction : new Direction[]{Direction.get(AxisDirection.NEGATIVE, axis), Direction.get(AxisDirection.POSITIVE, axis)}) {
            cursor.set(pos);

            for (int step = 0; step < 16; step++) {
               cursor.move(direction);
               if (wall(level, cursor, own)) {
                  break;
               }

               span++;
            }
         }

         narrowest = Math.min(narrowest, span);
      }

      return narrowest;
   }

   private Set<BlockPos> floodInterior(BlockPos centre, int radius) {
      Set<BlockPos> visited = new HashSet<>();
      Map<BlockPos, Integer> widths = new HashMap<>();
      Deque<BlockPos> queue = new ArrayDeque<>();
      ServerLevel level = (ServerLevel)this.level();
      Set<BlockPos> own = new HashSet<>(this.replacedPositions);
      MutableBlockPos cursor = new MutableBlockPos();
      visited.add(centre);
      widths.put(centre, width(level, centre, own, cursor));
      queue.add(centre);
      int limit = radius * radius;

      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         int here = widths.get(current);

         for (Direction direction : Direction.values()) {
            BlockPos next = current.relative(direction);
            if (!visited.contains(next) && !(next.distSqr(centre) > limit) && (!IncompleteDomain.isSolid(level, next) || own.contains(next))) {
               int beyond = width(level, next, own, cursor);
               if (beyond * 2 > here) {
                  visited.add(next);
                  widths.put(next, Math.max(beyond, here));
                  queue.add(next);
               }
            }
         }
      }

      return visited;
   }

   private void seal(BlockPos pos, ServerLevel level) {
      Block block = this.blockFor(pos);
      if (block != null) {
         if (this.replace(pos, level, block, false)) {
            this.replacedPositions.add(pos.immutable());
         }
      }
   }

   @Nullable
   private Block blockFor(BlockPos pos) {
      if (this.ability == null) {
         return (Block)JJKBlocks.DOMAIN.get();
      } else if (this.floors != null && this.floors.contains(pos)) {
         List<Block> bottom = this.ability.getBottomFloorBlocks();
         if (!bottom.isEmpty()) {
            return this.pick(bottom);
         } else {
            List<Block> floor = this.ability.getFloorBlocks();
            if (!floor.isEmpty()) {
               return this.pick(floor);
            } else {
               List<Block> fill = this.ability.getFillBlocks();
               return !fill.isEmpty() ? this.pick(fill) : (Block)JJKBlocks.DOMAIN.get();
            }
         }
      } else if (this.decorations != null && this.decorations.contains(pos)) {
         List<Block> decoration = this.ability.getDecorationBlocks();
         return decoration.isEmpty() ? null : this.pick(decoration);
      } else {
         List<Block> shell = this.ability.getBlocks();
         return shell.isEmpty() ? (Block)JJKBlocks.DOMAIN.get() : this.pick(shell);
      }
   }

   private Block pick(List<Block> blocks) {
      return blocks.get(this.random.nextInt(blocks.size()));
   }

   private boolean replace(BlockPos pos, ServerLevel level, Block block, boolean snapshot) {
      if (this.isRemoved()) {
         return false;
      } else if (!level.isInWorldBounds(pos)) {
         return false;
      } else {
         BlockState current = level.getBlockState(pos);
         if (current.is(Blocks.BEDROCK)) {
            return false;
         } else if (VeilHandler.isProtected(level, pos)) {
            return false;
         } else {
            BlockEntity existing = level.getBlockEntity(pos);
            if (existing instanceof DomainBlockEntity dbe) {
               UUID identifier = dbe.getIdentifier();
               if (identifier != null && identifier.equals(this.getUUID()) && current.is(block)) {
                  return false;
               }
            }

            CompoundTag saved = null;
            if (!snapshot && existing instanceof ITemporaryBlockEntity tmp) {
               current = tmp.getOriginal();
               saved = tmp.getSaved();
            } else if (existing != null) {
               saved = existing.saveWithFullMetadata();
            }

            if (current == null) {
               current = Blocks.AIR.defaultBlockState();
            }

            if (existing != null) {
               level.removeBlockEntity(pos);
            }

            level.setBlock(pos, block.defaultBlockState(), 50);
            if (level.getBlockEntity(pos) instanceof DomainBlockEntity dbex) {
               dbex.create(this.getUUID(), 0, current, saved, this.getUUID());
               return true;
            } else {
               return false;
            }
         }
      }
   }

   @Override
   public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.addAdditionalSaveData(pCompound);
      if (this.claimedVeil != null) {
         pCompound.putUUID("claimed_veil", this.claimedVeil);
      }

      pCompound.put("min", NbtUtils.writeBlockPos(this.getMin()));
      pCompound.put("max", NbtUtils.writeBlockPos(this.getMax()));
      pCompound.putBoolean("formed", this.formed);
      pCompound.putBoolean("incomplete", this.incomplete);
      pCompound.putInt("placed", this.placed);
      ListTag positionsList = new ListTag();

      for (BlockPos pos : this.replacedPositions) {
         positionsList.add(NbtUtils.writeBlockPos(pos));
      }

      pCompound.put("replaced_positions", positionsList);
      ListTag breachList = new ListTag();

      for (BlockPos pos : this.breachPositions) {
         breachList.add(NbtUtils.writeBlockPos(pos));
      }

      pCompound.put("breach_positions", breachList);
   }

   @Override
   public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
      super.readAdditionalSaveData(pCompound);
      if (pCompound.hasUUID("claimed_veil")) {
         this.claimedVeil = pCompound.getUUID("claimed_veil");
      }

      this.entityData.set(DATA_MIN, NbtUtils.readBlockPos(pCompound.getCompound("min")));
      this.entityData.set(DATA_MAX, NbtUtils.readBlockPos(pCompound.getCompound("max")));
      this.formed = pCompound.getBoolean("formed");
      this.incomplete = !pCompound.contains("incomplete") || pCompound.getBoolean("incomplete");
      this.placed = pCompound.getInt("placed");
      this.replacedPositions.clear();

      for (Tag tag : pCompound.getList("replaced_positions", 10)) {
         this.replacedPositions.add(NbtUtils.readBlockPos((CompoundTag)tag));
      }

      this.breachPositions.clear();

      for (Tag tag : pCompound.getList("breach_positions", 10)) {
         this.breachPositions.add(NbtUtils.readBlockPos((CompoundTag)tag));
      }

      if (this.clashers.isEmpty()) {
         this.clashers.add(this);
      }
   }

   @Override
   public void remove(@NotNull RemovalReason pReason) {
      if (!this.level().isClientSide) {
         ServerLevel level = (ServerLevel)this.level();
         this.closeBreach();
         if (this.claimedVeil != null && level.getEntity(this.claimedVeil) instanceof VeilEntity veil && !veil.isRemoved()) {
            veil.discard();
         }

         this.returnBorrowed();

         for (BlockPos pos : this.replacedPositions) {
            if (level.getBlockEntity(pos) instanceof DomainBlockEntity dbe) {
               UUID identifier = dbe.getIdentifier();
               if (identifier != null && identifier.equals(this.getUUID())) {
                  dbe.destroy();
               }
            }
         }

         this.replacedPositions.clear();
      }

      this.pending = null;
      super.remove(pReason);
   }

   public void push(@NotNull Entity pEntity) {
   }

   public boolean canBeCollidedWith() {
      return true;
   }
}
