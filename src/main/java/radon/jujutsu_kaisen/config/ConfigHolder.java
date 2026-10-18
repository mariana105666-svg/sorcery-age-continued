package radon.jujutsu_kaisen.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {
   public static final ForgeConfigSpec CLIENT_SPEC;
   public static final ForgeConfigSpec SERVER_SPEC;
   public static final ClientConfig CLIENT;
   public static final ServerConfig SERVER;

   static {
      Pair<ClientConfig, ForgeConfigSpec> specPair = new Builder().configure(ClientConfig::new);
      CLIENT = (ClientConfig)specPair.getLeft();
      CLIENT_SPEC = (ForgeConfigSpec)specPair.getRight();
      Pair<ServerConfig, ForgeConfigSpec> serverSpecPair = new Builder().configure(ServerConfig::new);
      SERVER = serverSpecPair.getLeft();
      SERVER_SPEC = serverSpecPair.getRight();
   }
}
