package radon.jujutsu_kaisen.network.packet.s2c;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.ClientWrapper;

public class SyncSorcererDataS2CPacket {
   private final CompoundTag nbt;

   public SyncSorcererDataS2CPacket(CompoundTag nbt) {
      this.nbt = nbt;
   }

   public SyncSorcererDataS2CPacket(FriendlyByteBuf buf) {
      this(buf.readNbt());
   }

   public void encode(FriendlyByteBuf buf) {
      buf.writeNbt(this.nbt);
   }

   public void handle(Supplier<Context> supplier) {
      Context ctx = supplier.get();
      ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
         Player player = ClientWrapper.getPlayer();

         assert player != null;

         ISorcererData oldCap = (ISorcererData)player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         ISorcererData newCap = new SorcererData();
         newCap.deserializeNBT(this.nbt);
         Set<Ability> oldToggled = oldCap.getToggled();
         Set<Ability> newToggled = new HashSet<>();

         for (Tag tag : this.nbt.getList("toggled", 8)) {
            newToggled.add(JJKAbilities.getValue(new ResourceLocation(tag.getAsString())));
         }

         oldToggled.removeAll(newToggled);

         for (Ability ability : oldToggled) {
            oldCap.toggle(ability);
         }

         oldCap.deserializeNBT(this.nbt);
      }));
      ctx.setPacketHandled(true);
   }
}
