package radon.jujutsu_kaisen.item.veil.modifier;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PlayerModifier extends Modifier {
   private final String name;
   private final UUID uuid;

   UUID getUUIDFromName(String name) {
      MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
      ServerPlayer player = server.getPlayerList().getPlayerByName(name);
      if (player != null) {
         return player.getUUID();
      } else {
         GameProfileCache cache = server.getProfileCache();
         if (cache != null) {
            Optional<GameProfile> profile = cache.get(name);
            if (profile.isPresent()) {
               return profile.get().getId();
            }
         }

         return null;
      }
   }

   public PlayerModifier(String name, Modifier.Action action) {
      super(Modifier.Type.PLAYER, action);
      this.name = name;
      this.uuid = this.getUUIDFromName(name);
   }

   public PlayerModifier(CompoundTag nbt) {
      super(nbt);
      this.name = nbt.getString("name");
      this.uuid = nbt.getUUID("uuid");
   }

   public String getName() {
      return this.name;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   @Override
   public Component getComponent() {
      return Component.translatable(String.format("item.%s.veil_rod.player.%s", "jujutsu_kaisen", this.getAction().name().toLowerCase()), new Object[]{this.name})
         .withStyle(this.getAction() == Modifier.Action.DENY ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN);
   }

   @Override
   public CompoundTag serializeNBT() {
      CompoundTag nbt = super.serializeNBT();
      nbt.putString("name", this.name);
      nbt.putUUID("uuid", this.uuid);
      return nbt;
   }
}
