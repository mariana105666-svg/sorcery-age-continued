package radon.jujutsu_kaisen.client.ability;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.InputEvent.MouseButton;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import radon.jujutsu_kaisen.ability.AbilityHandler;
import radon.jujutsu_kaisen.ability.AbilityTriggerEvent;
import radon.jujutsu_kaisen.ability.JJKAbilities;
import radon.jujutsu_kaisen.ability.base.Ability;
import radon.jujutsu_kaisen.ability.base.ITransformation;
import radon.jujutsu_kaisen.capability.data.sorcerer.ISorcererData;
import radon.jujutsu_kaisen.capability.data.sorcerer.JujutsuType;
import radon.jujutsu_kaisen.capability.data.sorcerer.SorcererDataHandler;
import radon.jujutsu_kaisen.client.JJKKeys;
import radon.jujutsu_kaisen.client.gui.MeleeMenuType;
import radon.jujutsu_kaisen.client.gui.overlay.AbilityOverlay;
import radon.jujutsu_kaisen.client.gui.screen.AbilityScreen;
import radon.jujutsu_kaisen.client.gui.screen.DomainScreen;
import radon.jujutsu_kaisen.client.gui.screen.JujutsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.JutwotsuScreen;
import radon.jujutsu_kaisen.client.gui.screen.MeleeScreen;
import radon.jujutsu_kaisen.client.gui.screen.ShadowInventoryScreen;
import radon.jujutsu_kaisen.client.veil.QuickVeilSettings;
import radon.jujutsu_kaisen.client.visual.ClientVisualHandler;
import radon.jujutsu_kaisen.config.ConfigHolder;
import radon.jujutsu_kaisen.entity.base.IJumpInputListener;
import radon.jujutsu_kaisen.entity.base.IRightClickInputListener;
import radon.jujutsu_kaisen.item.JJKItems;
import radon.jujutsu_kaisen.network.PacketHandler;
import radon.jujutsu_kaisen.network.packet.c2s.ChangeOutputC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.JumpInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.OpenInventoryCurseC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.QuickVeilC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.RightClickInputListenerC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.TriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.network.packet.c2s.UntriggerAbilityC2SPacket;
import radon.jujutsu_kaisen.util.CuriosUtil;
import radon.jujutsu_kaisen.util.EntityUtil;

public class ClientAbilityHandler {
   @Nullable
   private static Ability channeled;
   @Nullable
   private static KeyMapping current;
   private static boolean isChanneling;
   private static boolean isRightDown;
   private static final Map<KeyMapping, Ability> activeChannels = new HashMap<>();
   private static final Map<KeyMapping, Boolean> isChannelingMap = new HashMap<>();

   public static Ability.Status trigger(Ability ability) {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer owner = mc.player;
      if (owner == null) {
         return Ability.Status.FAILURE;
      } else if (!owner.getCapability(SorcererDataHandler.INSTANCE).isPresent()) {
         return Ability.Status.FAILURE;
      } else {
         ISorcererData cap = (ISorcererData)owner.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
         if (ability.getActivationType(owner) == Ability.ActivationType.INSTANT) {
            ability.charge(owner);
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            ability.run(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
         } else if (ability.getActivationType(owner) == Ability.ActivationType.TOGGLED) {
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            cap.toggle(ability);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
         } else if (ability.getActivationType(owner) == Ability.ActivationType.DOMAIN) {
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            cap.toggle(ability);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
         } else if (ability.getActivationType(owner) == Ability.ActivationType.CHANNELED) {
            ability.addDuration(owner);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Pre(owner, ability));
            cap.channel(ability);
            MinecraftForge.EVENT_BUS.post(new AbilityTriggerEvent.Post(owner, ability));
         }

         return Ability.Status.SUCCESS;
      }
   }

   @EventBusSubscriber(
      modid = "jujutsu_kaisen",
      bus = Bus.FORGE,
      value = {Dist.CLIENT}
   )
   public static class ClientAbilityHandlerForgeEvents {
      @SubscribeEvent
      public static void onRenderLivingPre(Pre<?, ?> event) {
         LivingEntity entity = event.getEntity();
         ClientVisualHandler.ClientData data = ClientVisualHandler.get(entity);
         if (data != null) {
            if (event.getRenderer().getModel() instanceof HumanoidModel<?> humanoid) {
               PlayerModel var9 = null;
               if (humanoid instanceof PlayerModel<?> pm) {
                  var9 = pm;
               }

               for (Ability ability : data.toggled) {
                  if (ability instanceof ITransformation transformation && transformation.isReplacement()) {
                     switch (transformation.getBodyPart()) {
                        case HEAD:
                           humanoid.head.visible = false;
                           if (var9 != null) {
                              var9.hat.visible = false;
                           }
                           break;
                        case BODY:
                           humanoid.body.visible = false;
                           if (var9 != null) {
                              var9.jacket.visible = false;
                           }
                           break;
                        case RIGHT_ARM:
                           humanoid.rightArm.visible = false;
                           if (var9 != null) {
                              var9.rightSleeve.visible = false;
                           }
                           break;
                        case LEFT_ARM:
                           humanoid.leftArm.visible = false;
                           if (var9 != null) {
                              var9.leftSleeve.visible = false;
                           }
                           break;
                        case LEGS:
                           humanoid.rightLeg.visible = false;
                           humanoid.leftLeg.visible = false;
                           if (var9 != null) {
                              var9.rightPants.visible = false;
                              var9.leftPants.visible = false;
                           }
                     }

                     ArmPose pose = IClientItemExtensions.of(transformation.getItem())
                        .getArmPose(event.getEntity(), InteractionHand.MAIN_HAND, transformation.getItem().getDefaultInstance());
                     if (pose != null) {
                        if (transformation.getBodyPart() == ITransformation.Part.RIGHT_ARM) {
                           humanoid.rightArmPose = pose;
                        } else if (transformation.getBodyPart() == ITransformation.Part.LEFT_ARM) {
                           humanoid.leftArmPose = pose;
                        }
                     }
                  }
               }
            }
         }
      }

      private static void channel(@Nullable Ability ability, @Nullable KeyMapping key) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            ClientAbilityHandler.channeled = ability;
            ClientAbilityHandler.current = key;
            ClientAbilityHandler.isChanneling = false;
         }
      }

      @SubscribeEvent
      public static void onClientTick(ClientTickEvent event) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            if (ClientAbilityHandler.current != null && ClientAbilityHandler.channeled != null) {
               boolean isHeld = ClientAbilityHandler.current.isDown();
               if (isHeld) {
                  if (!ClientAbilityHandler.isChanneling) {
                     PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ClientAbilityHandler.channeled)));
                  }

                  ClientAbilityHandler.isChanneling = true;
               } else if (ClientAbilityHandler.isChanneling) {
                  AbilityHandler.untrigger(mc.player, ClientAbilityHandler.channeled);
                  PacketHandler.sendToServer(new UntriggerAbilityC2SPacket(JJKAbilities.getKey(ClientAbilityHandler.channeled)));
                  channel(null, null);
               }
            }

            if (mc.player.getVehicle() instanceof IRightClickInputListener listener) {
               if (!ClientAbilityHandler.isRightDown && mc.mouseHandler.isRightPressed()) {
                  listener.setDown(true);
                  PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(true));
                  ClientAbilityHandler.isRightDown = true;
               } else if (ClientAbilityHandler.isRightDown && !mc.mouseHandler.isRightPressed()) {
                  listener.setDown(false);
                  PacketHandler.sendToServer(new RightClickInputListenerC2SPacket(false));
                  ClientAbilityHandler.isRightDown = false;
               }
            }
         }
      }

      private static void handleInput(int inputObj, int action) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.player != null) {
            if ((inputObj == 32 || inputObj == 32) && (action == 1 || action == 0)) {
               boolean down = action == 1;
               if (mc.player.getVehicle() instanceof IJumpInputListener listener) {
                  listener.setJump(down);
                  PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
               } else if (mc.player.getFirstPassenger() instanceof IJumpInputListener listener) {
                  listener.setJump(down);
                  PacketHandler.sendToServer(new JumpInputListenerC2SPacket(down));
               }
            }

            if (action == 1) {
               if (JJKKeys.ACTIVATE_ABILITY.isDown()) {
                  Ability ability = AbilityOverlay.getSelected();
                  if (ability != null) {
                     if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                        if (ClientAbilityHandler.channeled == null) {
                           channel(ability, JJKKeys.ACTIVATE_ABILITY);
                        }
                     } else if (JJKKeys.ACTIVATE_ABILITY.consumeClick()) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                     }
                  }
               }

               if (JJKKeys.ACTIVATE_J2TSU.isDown()) {
                  Ability ability = AbilityOverlay.getSelected2();
                  if (ability != null) {
                     if (ability.getActivationType(mc.player) == Ability.ActivationType.CHANNELED) {
                        if (ClientAbilityHandler.channeled == null) {
                           channel(ability, JJKKeys.ACTIVATE_J2TSU);
                        }
                     } else if (JJKKeys.ACTIVATE_J2TSU.consumeClick()) {
                        PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey(ability)));
                     }
                  }
               }

               if (JJKKeys.OPEN_INVENTORY_CURSE.consumeClick()
                  && (
                     mc.player.getItemBySlot(EquipmentSlot.CHEST).is((Item)JJKItems.INVENTORY_CURSE.get())
                        || CuriosUtil.findSlot(mc.player, "body").is((Item)JJKItems.INVENTORY_CURSE.get())
                  )) {
                  PacketHandler.sendToServer(new OpenInventoryCurseC2SPacket());
               }

               if (JJKKeys.OPEN_JUJUTSU_MENU.consumeClick()) {
                  mc.setScreen(new JujutsuScreen());
               }

               if (JJKKeys.SHOW_ABILITY_MENU.consumeClick()) {
                  mc.setScreen(new AbilityScreen());
               }

               if (JJKKeys.SHOW_DOMAIN_MENU.consumeClick()) {
                  mc.setScreen(new DomainScreen());
               }

               if (ConfigHolder.CLIENT.meleeMenuType.get() == MeleeMenuType.TOGGLE && JJKKeys.ACTIVATE_MELEE_MENU.consumeClick()) {
                  mc.setScreen(new MeleeScreen());
               }

               if (JJKKeys.ACTIVATE_J2TSU_MENU.consumeClick()) {
                  mc.setScreen(new JutwotsuScreen());
               }

               if (JJKKeys.INCREASE_OUTPUT.consumeClick()) {
                  ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  PacketHandler.sendToServer(new ChangeOutputC2SPacket(1));
                  cap.increaseOutput();
               }

               if (JJKKeys.DECREASE_OUTPUT.consumeClick()) {
                  ISorcererData cap = (ISorcererData)mc.player.getCapability(SorcererDataHandler.INSTANCE).resolve().orElseThrow();
                  PacketHandler.sendToServer(new ChangeOutputC2SPacket(-1));
                  cap.decreaseOutput();
               }

               if (JJKKeys.ACTIVATE_RCT_OR_HEAL.isDown()) {
                  Ability rct = EntityUtil.getRCTTier(mc.player);
                  Ability ability = JJKAbilities.getType(mc.player) == JujutsuType.CURSE ? (Ability)JJKAbilities.HEAL.get() : rct;
                  if (ability != null && ClientAbilityHandler.channeled == null) {
                     channel(ability, JJKKeys.ACTIVATE_RCT_OR_HEAL);
                  }
               }

               if (JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD.isDown() && ClientAbilityHandler.channeled == null) {
                  channel((Ability)JJKAbilities.CURSED_ENERGY_SHIELD.get(), JJKKeys.ACTIVATE_CURSED_ENERGY_SHIELD);
               }

               if (JJKKeys.DASH.consumeClick()) {
                  PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey((Ability)JJKAbilities.DASH.get())));
               }

               if (JJKKeys.QUICK_VEIL.consumeClick() && JujutsuScreen.hasQuickVeil()) {
                  PacketHandler.sendToServer(new QuickVeilC2SPacket(QuickVeilSettings.toModifiers(), QuickVeilSettings.getSize()));
               }

               if (JJKKeys.QUICKDASH.consumeClick()) {
                  PacketHandler.sendToServer(new TriggerAbilityC2SPacket(JJKAbilities.getKey((Ability)JJKAbilities.QUICKDASH.get())));
               }
            } else if (action == 0
               && (
                  inputObj == JJKKeys.SHOW_ABILITY_MENU.getKey().getValue() && mc.screen instanceof AbilityScreen
                     || inputObj == JJKKeys.SHOW_DOMAIN_MENU.getKey().getValue() && mc.screen instanceof DomainScreen
                     || inputObj == JJKKeys.ACTIVATE_J2TSU.getKey().getValue() && mc.screen instanceof ShadowInventoryScreen
               )) {
               mc.screen.onClose();
            }
         }
      }

      @SubscribeEvent
      public static void onKeyInput(Key event) {
         handleInput(event.getKey(), event.getAction());
      }

      @SubscribeEvent
      public static void onMouseInput(MouseButton event) {
         handleInput(event.getButton(), event.getAction());
      }
   }
}
