package radon.jujutsu_kaisen.item.veil.modifier;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ModifierUtils {
   private static final int MAX_MODIFIERS = 8;

   public static Modifier resolve(CompoundTag nbt) {
      Modifier.Type type = Modifier.Type.values()[nbt.getInt("type")];

      return (Modifier)(switch (type) {
         case NONE -> new Modifier(nbt);
         case PLAYER -> new PlayerModifier(nbt);
         case COLOR -> new ColorModifier(nbt);
         case TRANSPARENT -> new TransparentModifier(nbt);
         case CURSE -> new CurseModifier(nbt);
         case SORCERER -> new SorcererModifier(nbt);
         case GRIEFING -> new GriefingModifier(nbt);
         case OWNER_BYPASS -> new OwnerBypassModifier(nbt);
         case TELEPORT -> new TeleportModifier(nbt);
         case CURSE_SPAWN -> new CurseSpawnModifier(nbt);
         case SORCERER_SPAWN -> new SorcererSpawnModifier(nbt);
         case VIOLENCE -> new ViolenceModifier(nbt);
         case ADMIN -> new AdminModifier(nbt);
         case DOMAIN -> new DomainModifier(nbt);
         case ABILITY -> new AbilityModifier(nbt);
      });
   }

   public static Modifier create(Modifier.Type type, Modifier.Action action) {
      return (Modifier)(switch (type) {
         case TRANSPARENT -> new TransparentModifier(action);
         case CURSE -> new CurseModifier(action);
         case SORCERER -> new SorcererModifier(action);
         case GRIEFING -> new GriefingModifier(action);
         case OWNER_BYPASS -> new OwnerBypassModifier(action);
         case TELEPORT -> new TeleportModifier(action);
         case CURSE_SPAWN -> new CurseSpawnModifier(action);
         case SORCERER_SPAWN -> new SorcererSpawnModifier(action);
         case VIOLENCE -> new ViolenceModifier(action);
         case ADMIN -> new AdminModifier(action);
         case DOMAIN -> new DomainModifier(action);
         case ABILITY -> new AbilityModifier(action);
         default -> new Modifier(type, action);
      });
   }

   public static List<Modifier> getModifiers(CompoundTag nbt) {
      if (!nbt.contains("modifiers")) {
         return List.of();
      } else {
         ListTag modifiersTag = nbt.getList("modifiers", 10);
         return deserialize(modifiersTag);
      }
   }

   public static void setModifier(CompoundTag nbt, int index, Modifier modifier) {
      if (index >= 0 && index < 8) {
         ListTag modifiersTag;
         if (!nbt.contains("modifiers", 9)) {
            modifiersTag = new ListTag();
         } else {
            modifiersTag = nbt.getList("modifiers", 10);
         }

         while (modifiersTag.size() < 8) {
            modifiersTag.add(new Modifier(Modifier.Type.NONE, Modifier.Action.NONE).serializeNBT());
         }

         modifiersTag.set(index, modifier.serializeNBT());
         nbt.put("modifiers", modifiersTag);
      }
   }

   public static List<Modifier> deserialize(ListTag modifiersTag) {
      List<Modifier> modifiers = new ArrayList<>();

      for (Tag tag : modifiersTag) {
         CompoundTag modifier = (CompoundTag)tag;
         modifiers.add(resolve(modifier));
      }

      return modifiers;
   }

   public static ListTag serialize(List<Modifier> modifiers) {
      ListTag modifiersTag = new ListTag();

      for (Modifier modifier : modifiers) {
         modifiersTag.add(modifier.serializeNBT());
      }

      return modifiersTag;
   }
}
