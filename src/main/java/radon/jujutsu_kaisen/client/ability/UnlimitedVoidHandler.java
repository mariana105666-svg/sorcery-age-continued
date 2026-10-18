package radon.jujutsu_kaisen.client.ability;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import radon.jujutsu_kaisen.effect.JJKEffects;
import radon.jujutsu_kaisen.util.HelperMethods;

@EventBusSubscriber(
   modid = "jujutsu_kaisen",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class UnlimitedVoidHandler {
   private static final String[] SYMBOLS = new String[]{
      "⍑",
      "ʖ",
      "ᓵ",
      "╎",
      "ᒷ",
      "⍊",
      "⍋",
      "ᒲ",
      "リ",
      "ᔑ",
      "ꖎ",
      "ᒣ",
      "ᓭ",
      "ᘉ",
      "⨅",
      "╎⨅",
      "ᓵ⍑",
      "⍙",
      "ᔑ⨅",
      "ꖌ",
      "⍜",
      "⍀",
      "∷",
      "⨇",
      "ᒲ⍑",
      "ꖇ",
      "㇣",
      "˥",
      "˩",
      "˧˥˧",
      "ʢ",
      "ʖ̇ ",
      "˩˥ ",
      "ʖ̬ ",
      "ʖ̥"
   };

   private static String generateRandomSGAText(int length) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < length; i++) {
         int index = HelperMethods.RANDOM.nextInt(SYMBOLS.length);
         sb.append(SYMBOLS[index]);
      }

      return sb.toString();
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player != null && mc.player.hasEffect((MobEffect)JJKEffects.UNLIMITED_VOID.get())) {
         mc.gui.setOverlayMessage(Component.literal(generateRandomSGAText(HelperMethods.RANDOM.nextInt(20, 100))), false);
      }
   }
}
