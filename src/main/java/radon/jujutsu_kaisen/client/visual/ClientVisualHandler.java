package radon.jujutsu_kaisen.client.visual;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ActivePose;
import radon.jujutsu_kaisen.capability.data.sorcerer.CursedTechnique;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.capability.data.sorcerer.Trait;
import radon.jujutsu_kaisen.client.JJKPose;
import radon.jujutsu_kaisen.client.JJKPoses;
import radon.jujutsu_kaisen.client.visual.base.IOverlay;
import radon.jujutsu_kaisen.client.visual.base.IVisual;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.RequestVisualDataC2SPacket;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientVisualHandler {
   private static final Map<UUID, ClientVisualHandler.ClientData> synced = new HashMap<>();

   public static void receive(UUID identifier, CompoundTag nbt) {
      if (!synced.containsKey(identifier)) {
         synced.put(identifier, new ClientVisualHandler.ClientData(nbt));
      } else {
         synced.get(identifier).deserializeNBT(nbt);
      }
   }

   @Nullable
   public static ClientVisualHandler.ClientData get(UUID identifier) {
      return synced.get(identifier);
   }

   @Nullable
   public static ClientVisualHandler.ClientData get(Entity entity) {
      if (!entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return null;
      } else {
         Minecraft mc = Minecraft.getInstance();

         assert mc.level != null && mc.player != null;

         if (synced.containsKey(entity.getUUID())) {
            return synced.get(entity.getUUID());
         } else if (entity == mc.player && mc.player.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            Set<CursedTechnique> techniques = new HashSet<>();
            if (cap.getTechnique() != null) {
               techniques.add(cap.getTechnique());
            }

            if (cap.getCurrentCopied() != null) {
               techniques.add(cap.getCurrentCopied());
            }

            if (cap.getCurrentAbsorbed() != null) {
               techniques.add(cap.getCurrentAbsorbed());
            }

            if (cap.getCurrentStolen() != null) {
               techniques.add(cap.getCurrentStolen());
            }

            if (cap.getAdditional() != null) {
               techniques.add(cap.getAdditional());
            }

            return new ClientVisualHandler.ClientData(
               cap.getToggled(),
               cap.getChanneled(),
               cap.getTraits(),
               techniques,
               cap.getTechnique(),
               cap.getType(),
               cap.getExperience(),
               cap.getCursedEnergyColor(),
               cap.getActivePoses()
            );
         } else {
            return null;
         }
      }
   }

   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      LivingEntity entity = event.getEntity();
      ClientVisualHandler.ClientData data = get(entity);
      if (data != null) {
         for (IVisual visual : JJKVisuals.VISUALS) {
            if (visual.isValid(entity, data)) {
               visual.tick(entity, data);
            }
         }

         Iterator<Entry<ResourceLocation, ActivePose>> it = data.activePoses.entrySet().iterator();

         while (it.hasNext()) {
            Entry<ResourceLocation, ActivePose> entry = it.next();
            if (!entry.getValue().tick()) {
               it.remove();
            }
         }
      }
   }

   public static <T extends LivingEntity> void renderOverlays(
      T entity, ResourceLocation texture, EntityModel<T> model, PoseStack poseStack, MultiBufferSource buffer, float partialTicks, int packedLight
   ) {
      ClientVisualHandler.ClientData data = get(entity);
      if (data != null) {
         for (IOverlay overlay : JJKOverlays.OVERLAYS) {
            if (overlay.isValid(entity, data)) {
               overlay.render(entity, data, texture, model, poseStack, buffer, partialTicks, packedLight);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
      synced.remove(event.getEntity().getUUID());
   }

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.getConnection() != null) {
         Entity entity = event.getEntity();
         if (entity.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
            PacketHandler.sendToServer(new RequestVisualDataC2SPacket(entity.getUUID()));
         }
      }
   }

   public static class ClientData {
      public Set<Ability> toggled;
      @Nullable
      public Ability channeled;
      public Set<Trait> traits;
      public Set<CursedTechnique> techniques;
      @Nullable
      public CursedTechnique technique;
      public JujutsuType type;
      public float experience;
      public int cursedEnergyColor;
      public Map<ResourceLocation, ActivePose> activePoses;
      public int mouth;

      public ClientData(
         Set<Ability> toggled,
         @Nullable Ability channeled,
         Set<Trait> traits,
         Set<CursedTechnique> techniques,
         @Nullable CursedTechnique technique,
         JujutsuType type,
         float experience,
         int cursedEnergyColor,
         Map<ResourceLocation, ActivePose> activePoses
      ) {
         this.toggled = toggled;
         this.channeled = channeled;
         this.traits = traits;
         this.techniques = techniques;
         this.technique = technique;
         this.type = type;
         this.experience = experience;
         this.cursedEnergyColor = cursedEnergyColor;
         this.activePoses = activePoses;
      }

      public ClientData(CompoundTag nbt) {
         this.deserializeNBT(nbt);
      }

      public void deserializeNBT(CompoundTag nbt) {
         this.toggled = new HashSet<>();
         this.channeled = nbt.contains("channeled") ? JJKAbilities.getValue(new ResourceLocation(nbt.getString("channeled"))) : null;
         this.traits = new HashSet<>();
         this.techniques = new HashSet<>();
         this.technique = nbt.contains("technique") ? CursedTechnique.values()[nbt.getInt("technique")] : null;

         for (Tag key : nbt.getList("toggled", 8)) {
            this.toggled.add(JJKAbilities.getValue(new ResourceLocation(key.getAsString())));
         }

         for (Tag key : nbt.getList("traits", 3)) {
            if (key instanceof IntTag tag) {
               this.traits.add(Trait.values()[tag.getAsInt()]);
            }
         }

         for (Tag keyx : nbt.getList("techniques", 3)) {
            if (keyx instanceof IntTag tag) {
               this.techniques.add(CursedTechnique.values()[tag.getAsInt()]);
            }
         }

         this.type = JujutsuType.values()[nbt.getInt("type")];
         this.experience = nbt.getFloat("experience");
         this.cursedEnergyColor = nbt.getInt("cursed_energy_color");
         this.activePoses = new HashMap<>();

         for (Tag t : nbt.getList("active_poses", 10)) {
            CompoundTag tag = (CompoundTag)t;
            JJKPose pose = JJKPoses.get(new ResourceLocation(tag.getString("pose")));
            if (pose != null) {
               InteractionHand hand = InteractionHand.values()[tag.getInt("hand")];
               int time = tag.getInt("time");
               int ghostTicks = tag.getInt("ghost");
               this.activePoses.put(pose.id(), new ActivePose(pose, hand, time, ghostTicks));
            }
         }
      }

      public CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         ListTag toggledTag = new ListTag();

         for (Ability ability : this.toggled) {
            toggledTag.add(StringTag.valueOf(JJKAbilities.getKey(ability).toString()));
         }

         nbt.put("toggled", toggledTag);
         if (this.channeled != null) {
            nbt.putString("channeled", JJKAbilities.getKey(this.channeled).toString());
         }

         ListTag traitsTag = new ListTag();

         for (Trait trait : this.traits) {
            traitsTag.add(IntTag.valueOf(trait.ordinal()));
         }

         nbt.put("traits", traitsTag);
         ListTag techniquesTag = new ListTag();

         for (CursedTechnique technique : this.techniques) {
            techniquesTag.add(IntTag.valueOf(technique.ordinal()));
         }

         nbt.put("techniques", techniquesTag);
         if (this.technique != null) {
            nbt.putInt("technique", this.technique.ordinal());
         }

         nbt.putInt("type", this.type.ordinal());
         nbt.putFloat("experience", this.experience);
         nbt.putInt("cursed_energy_color", this.cursedEnergyColor);
         ListTag posesTag = new ListTag();

         for (Entry<ResourceLocation, ActivePose> entry : this.activePoses.entrySet()) {
            ResourceLocation ability = entry.getKey();
            ActivePose pose = entry.getValue();
            JJKPose poseKey = JJKPoses.get(ability);
            if (poseKey != null) {
               CompoundTag tag = new CompoundTag();
               tag.putString("pose", ability.toString());
               tag.putInt("hand", pose.hand.ordinal());
               tag.putInt("time", pose.getTicksLeft());
               tag.putInt("ghost", pose.ghostTicks);
               posesTag.add(tag);
            }
         }

         nbt.put("active_poses", posesTag);
         return nbt;
      }
   }
}
