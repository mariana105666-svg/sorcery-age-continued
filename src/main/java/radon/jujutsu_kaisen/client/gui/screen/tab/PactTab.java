package radon.jujutsu_kaisen.client.gui.screen.tab;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.ObjectSelectionList.Entry;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.Pact;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.widget.PactListWidget;
import radon.jujutsu_kaisen.client.gui.screen.widget.PlayerListWidget;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionCreatePactC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuestionRemovePactC2SPacket;

public class PactTab extends JJKTab {
   private static final Component TITLE = Component.translatable(String.format("gui.%s.pact", "jujutsu_kaisen"));
   private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/advancements/backgrounds/stone.png");
   private Button create;
   private Button remove;
   private MultiLineTextWidget description;
   @Nullable
   private PlayerListWidget.Entry player;
   @Nullable
   private PactListWidget.Entry pact;

   public PactTab(Minecraft minecraft, JujutsuScreen screen, JJKTabType type, int index, int page) {
      super(minecraft, screen, type, index, page, Items.WRITABLE_BOOK.getDefaultInstance(), TITLE, BACKGROUND, false);
   }

   @Nullable
   public PlayerListWidget.Entry getSelectedPlayer() {
      return this.player;
   }

   public void setSelectedPlayer(PlayerListWidget.Entry entry) {
      this.player = entry;
   }

   public void setSelectedPact(PactListWidget.Entry entry) {
      this.pact = entry;
      this.description.setMessage(entry.get().getDescription());
   }

   public <T extends Entry<T>> void buildPlayerList(Consumer<T> consumer, Function<PlayerInfo, T> result) {
      if (this.minecraft.player != null) {
         ClientPacketListener connection = this.minecraft.getConnection();
         if (connection != null) {
            for (PlayerInfo player : connection.getOnlinePlayers()) {
               if (!player.getProfile().getId().equals(this.minecraft.player.getUUID())) {
                  consumer.accept(result.apply(player));
               }
            }
         }
      }
   }

   public <T extends Entry<T>> void buildPactList(Consumer<T> consumer, Function<Pact, T> result) {
      for (Pact pact : Pact.values()) {
         consumer.accept(result.apply(pact));
      }
   }

   @Override
   public void drawContents(GuiGraphics pGuiGraphics, int pX, int pY) {
      super.drawContents(pGuiGraphics, pX, pY);
      int i = (this.screen.width - 252) / 2;
      int j = (this.screen.height - 140) / 2;
      int xOffset = i + 18;
      int yOffset = j + 27;
      pGuiGraphics.drawString(
         this.minecraft.font, Component.translatable(String.format("gui.%s.pact.players", "jujutsu_kaisen")), xOffset, yOffset, 16777215, true
      );
      pGuiGraphics.drawString(
         this.minecraft.font, Component.translatable(String.format("gui.%s.pact.pacts", "jujutsu_kaisen")), xOffset + 64, yOffset, 16777215, true
      );
   }

   @Override
   public void tick() {
      super.tick();
      if (this.minecraft != null && this.minecraft.player != null) {
         this.create.active = false;
         this.remove.active = false;
         if (this.player != null && this.pact != null) {
            ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
            this.create.active = !cap.hasPact(this.player.get().getProfile().getId(), this.pact.get());
            this.remove.active = cap.hasPact(this.player.get().getProfile().getId(), this.pact.get());
         }
      }
   }

   @Override
   public void addWidgets() {
      if (this.minecraft != null && this.minecraft.player != null) {
         int i = (this.screen.width - 252) / 2;
         int j = (this.screen.height - 140) / 2;
         int xOffset = i + 18;
         int yOffset = j + 27;
         this.addRenderableWidget(new PlayerListWidget(this::buildPlayerList, this::setSelectedPlayer, this.minecraft, 56, 85, xOffset, yOffset + 9 + 1));
         this.addRenderableWidget(new PactListWidget(this::buildPactList, this::setSelectedPact, this.minecraft, 56, 85, xOffset + 64, yOffset + 9 + 1, this));
         this.create = Button.builder(
               Component.translatable(String.format("gui.%s.pact.create", "jujutsu_kaisen")),
               pButton -> {
                  if (this.player != null && this.pact != null) {
                     ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     this.minecraft
                        .player
                        .sendSystemMessage(
                           Component.translatable(
                              String.format("chat.%s.pact_request_create", "jujutsu_kaisen"), new Object[]{this.player.get().getProfile().getName()}
                           )
                        );
                     PacketHandler.sendToServer(new QuestionCreatePactC2SPacket(this.player.get().getProfile().getId(), this.pact.get()));
                     cap.createPactCreationRequest(this.player.get().getProfile().getId(), this.pact.get());
                  }
               }
            )
            .size(40, 20)
            .pos(xOffset + 128, yOffset + 9 + 66)
            .build();
         this.addRenderableWidget(this.create);
         this.remove = Button.builder(
               Component.translatable(String.format("gui.%s.pact.remove", "jujutsu_kaisen")),
               pButton -> {
                  if (this.player != null && this.pact != null) {
                     ISorcererData cap = (ISorcererData)this.minecraft.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                     this.minecraft
                        .player
                        .sendSystemMessage(
                           Component.translatable(
                              String.format("chat.%s.pact_request_remove", "jujutsu_kaisen"), new Object[]{this.player.get().getProfile().getName()}
                           )
                        );
                     PacketHandler.sendToServer(new QuestionRemovePactC2SPacket(this.player.get().getProfile().getId(), this.pact.get()));
                     cap.createPactRemovalRequest(this.player.get().getProfile().getId(), this.pact.get());
                  }
               }
            )
            .size(40, 20)
            .pos(xOffset + 176, yOffset + 9 + 66)
            .build();
         this.addRenderableWidget(this.remove);
         this.description = new MultiLineTextWidget(xOffset + 128, yOffset + 9 + 1, Component.empty(), this.minecraft.font).setMaxWidth(87);
         this.addRenderableWidget(this.description);
      }
   }
}
