package radon.jujutsu_kaisen.capability.data.sorcerer;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import radon.jujutsu_kaisen.config.ConfigHolder;

public enum DomainConfiguration {
   SIZE(
      ValueType.FLOAT,
      1.0F,
      ((Double)ConfigHolder.SERVER.minimumDomainSize.get()).floatValue(),
      ((Double)ConfigHolder.SERVER.maximumDomainSize.get()).floatValue()
   ),
   SHELL_BALANCE(ValueType.BOOLEAN, false),
   SURE_HIT_TOGGLE(ValueType.BOOLEAN, true),
   SURE_HIT_ALLIES(ValueType.BOOLEAN, true),
   OPEN_BARRIER(ValueType.BOOLEAN, false);

   private final ValueType type;
   private final Object defaultValue;
   private final Object minimumValue;
   private final Object maximumValue;

   private DomainConfiguration(ValueType type, Object defaultValue, Object minimumValue, Object maximumValue) {
      this.type = type;
      this.defaultValue = defaultValue;
      this.minimumValue = minimumValue;
      this.maximumValue = maximumValue;
   }

   private DomainConfiguration(ValueType type, Object defaultValue) {
      this(type, defaultValue, null, null);
   }

   public ValueType getType() {
      return this.type;
   }

   public <T> T getDefault() {
      return (T)this.defaultValue;
   }

   public Object getMinimumValue() {
      return this.minimumValue;
   }

   public Object getMaximumValue() {
      return this.maximumValue;
   }

   public String getRawName() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public Component getName() {
      return Component.translatable("domain_configuration.%s.%s".formatted("jujutsu_kaisen", this.name().toLowerCase()));
   }

   public Component getDescription() {
      return Component.translatable("domain_configuration.%s.%s.description".formatted("jujutsu_kaisen", this.name().toLowerCase()));
   }
}
