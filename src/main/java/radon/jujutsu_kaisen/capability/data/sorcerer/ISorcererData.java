package radon.jujutsu_kaisen.capability.data.sorcerer;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.AbstractMap.SimpleEntry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ActivePose;

@AutoRegisterCapability
public interface ISorcererData {
   void attack(DamageSource var1, LivingEntity var2);

   void onRightClick(LivingEntity var1);

   void tick(LivingEntity var1);

   void init(LivingEntity var1);

   int getCursedEnergyColor();

   void setCursedEnergyColor(int var1);

   float getMaximumOutput();

   void increaseOutput();

   void decreaseOutput();

   void maxOutput();

   int getPoints();

   void setPoints(int var1);

   void addPoints(int var1);

   void usePoints(int var1);

   boolean isUnlocked(Ability var1);

   void unlock(Ability var1);

   void unlockAll(List<Ability> var1);

   void lock(Ability var1);

   void lockAll();

   void createPact(UUID var1, Pact var2);

   Map<UUID, Set<Pact>> getAcceptedPacts();

   boolean hasPact(UUID var1, Pact var2);

   Player getPactPartner(UUID var1, Pact var2);

   void removePact(UUID var1, Pact var2);

   void createPactCreationRequest(UUID var1, Pact var2);

   void createPactRemovalRequest(UUID var1, Pact var2);

   void removePactCreationRequest(UUID var1, Pact var2);

   void removePactRemovalRequest(UUID var1, Pact var2);

   boolean hasRequestedPactCreation(UUID var1, Pact var2);

   boolean hasRequestedPactRemoval(UUID var1, Pact var2);

   void addBindingVow(BindingVow var1);

   void removeBindingVow(BindingVow var1);

   boolean hasBindingVow(BindingVow var1);

   void addBindingVowCooldown(BindingVow var1);

   int getRemainingCooldown(BindingVow var1);

   void resetVows();

   float getAdditionalEnergy();

   void setAdditionalEnergy(float var1);

   int getShieldTicks();

   void setShieldTicks(int var1);

   boolean isCooldownDone(BindingVow var1);

   void addAbilityPose(LivingEntity var1, Ability.IPosedMove var2, InteractionHand var3, int var4);

   void removeAbilityPose(LivingEntity var1, Ability.IPosedMove var2);

   Map<ResourceLocation, ActivePose> getActivePoses();

   void addChant(Ability var1, String var2);

   void addChants(Ability var1, Set<String> var2);

   void removeChant(Ability var1, String var2);

   boolean hasChant(Ability var1, String var2);

   boolean isChantsAvailable(Set<String> var1);

   @Nullable
   Ability getAbility(String var1);

   @Nullable
   Ability getAbility(Set<String> var1);

   Set<String> getFirstChants();

   Set<String> getFirstChants(Ability var1);

   float getOutput();

   float getAbilityPower();

   float getRealPower();

   float getRealPower(float var1);

   float getExperience();

   void setExperience(float var1);

   boolean addExperience(float var1);

   void setDomainConfig(DomainConfiguration var1, Object var2);

   <T> T getDomainConfig(DomainConfiguration var1);

   float getDomainSize();

   void setDomainSize(float var1);

   void setShellBalance(boolean var1);

   boolean getShellBalance();

   void setToggleSureHit(boolean var1);

   boolean getToggleSureHit();

   void setAlliedSureHit(boolean var1);

   boolean getAlliedSureHit();

   boolean hasToggled(Ability var1);

   @Nullable
   CursedTechnique getAdditional();

   void setAdditional(CursedTechnique var1);

   @Nullable
   CursedTechnique getTechnique();

   Set<CursedTechnique> getTechniques();

   boolean hasTechnique(CursedTechnique var1);

   void setTechnique(@Nullable CursedTechnique var1);

   CursedEnergyNature getNature();

   void setNature(CursedEnergyNature var1);

   void setGrade(SorcererGrade var1);

   boolean hasTrait(Trait var1);

   void addTrait(Trait var1);

   void addTraits(List<Trait> var1);

   void removeTrait(Trait var1);

   Set<Trait> getTraits();

   void setTraits(Set<Trait> var1);

   void setType(JujutsuType var1);

   JujutsuType getType();

   void toggle(Ability var1);

   void clearToggled();

   Set<Ability> getToggled();

   void clearCooldown(Ability var1);

   void addCooldown(Ability var1);

   void setCooldown(Ability var1, int var2);

   int getRemainingCooldown(Ability var1);

   void disrupt(Ability var1, int var2);

   boolean isCooldownDone(Ability var1);

   void addDuration(Ability var1);

   void increaseBrainDamage();

   int getBrainDamage();

   void hurtThroat(int var1);

   int getThroatDamage();

   boolean isThroatDamaged();

   void setBurnout(int var1);

   void setDisable(int var1);

   void setDisarmed(int var1);

   void setSilenced(int var1);

   void setSelfHit(int var1);

   void setExtraMeleeTaken(int var1);

   int getBurnout();

   int getDisable();

   int getDisarmed();

   int getSilenced();

   int getSelfHit();

   int getExtraMeleeTaken();

   boolean hasBurnout();

   boolean hasDisable();

   boolean hasDisarmed();

   boolean hasSilenced();

   boolean hasSelfHit();

   boolean hasExtraMeleeTaken();

   void resetCooldowns();

   void resetBurnout();

   void resetDisable();

   void resetDisarmed();

   void resetSilenced();

   void resetSelfHit();

   void setStoredHealth(float var1);

   void updateMaxHealth();

   float getMaxEnergy();

   void setMaxEnergy(float var1);

   float getEnergy();

   void addEnergy(float var1);

   void useEnergy(float var1);

   void setEnergy(float var1);

   float getExtraEnergy();

   void addExtraEnergy(float var1);

   void resetExtraEnergy();

   void onBlackFlash();

   long getLastBlackFlashTime();

   boolean addBlackFlash();

   void removeLife();

   void setLives(int var1);

   int getLives();

   boolean hasAdminVeil();

   void setAdminVeil(boolean var1);

   CompoundTag getQuickVeil();

   void setQuickVeil(CompoundTag var1);

   Set<Ability> getBorrowed();

   void borrow(Ability var1);

   void clearBorrowed();

   boolean hasGuaranteedBlackFlash();

   void setGuaranteedBlackFlash(boolean var1);

   void setRevivable(boolean var1);

   boolean getRevivable();

   boolean checkWombAwakened();

   void setWombAwakened(boolean var1);

   void moreBlackFlash(boolean var1);

   void resetBlackFlash();

   boolean isInZone();

   void delayTickEvent(Runnable var1, int var2);

   void uncopy(CursedTechnique var1);

   void unsteal(CursedTechnique var1);

   void copy(@Nullable CursedTechnique var1);

   void steal(@Nullable CursedTechnique var1);

   void resetSteal();

   void setStolen(Set<CursedTechnique> var1);

   void resetAbsorbed();

   void resetCopy();

   void resetCurses();

   void setCopies(Set<CursedTechnique> var1);

   Set<CursedTechnique> getCopied();

   void setCurrentCopied(@Nullable CursedTechnique var1);

   @Nullable
   CursedTechnique getCurrentCopied();

   void absorb(@Nullable CursedTechnique var1);

   void unabsorb(CursedTechnique var1);

   Set<CursedTechnique> getAbsorbed();

   void setCurrentAbsorbed(@Nullable CursedTechnique var1);

   @Nullable
   CursedTechnique getCurrentAbsorbed();

   Set<CursedTechnique> getStolen();

   void setCurrentStolen(@Nullable CursedTechnique var1);

   @Nullable
   CursedTechnique getCurrentStolen();

   @Nullable
   CursedTechnique getLastStolen();

   @Nullable
   CursedTechnique getLatestStolen();

   void addStolen(CursedTechnique var1);

   boolean hasStolen(CursedTechnique var1);

   int getTransfiguredSouls();

   void increaseTransfiguredSouls();

   void decreaseTransfiguredSouls();

   void useTransfiguredSouls(int var1);

   @Nullable
   Ability getChanneled();

   void channel(@Nullable Ability var1);

   boolean isChanneling(Ability var1);

   int getCharge(Ability var1);

   void wipe(ServerPlayer var1);

   void generate(ServerPlayer var1);

   void addSummon(Entity var1);

   void removeSummon(Entity var1);

   List<Entity> getSummons();

   @Nullable
   <T extends Entity> T getSummonByClass(Class<T> var1);

   <T extends Entity> List<T> getSummonsByClass(Class<T> var1);

   <T extends Entity> void unsummonByClass(Class<T> var1);

   <T extends Entity> void removeSummonByClass(Class<T> var1);

   <T extends Entity> boolean hasSummonOfClass(Class<T> var1);

   void addCurse(AbsorbedCurse var1);

   void removeCurse(AbsorbedCurse var1);

   List<AbsorbedCurse> getCurses();

   @Nullable
   AbsorbedCurse getCurse(EntityType<?> var1);

   boolean hasCurse(EntityType<?> var1);

   List<SimpleEntry<Vec3, Float>> getFrames();

   void addFrame(Vec3 var1, float var2);

   void removeFrame(SimpleEntry<Vec3, Float> var1);

   void resetFrames();

   int getSpeedStacks();

   void addSpeedStack();

   void lowerSpeedStacks();

   void resetSpeedStacks();

   int getFingers();

   void setFingers(int var1);

   int addFingers(int var1);

   CompoundTag serializeNBT();

   void deserializeNBT(CompoundTag var1);

   void addDash();

   int getDash();

   void subDash();

   void resetDash();
}
