package net.okocraft.userapi.bungee;

import com.github.siroshun09.configapi.bungee.BungeeConfig;
import net.md_5.bungee.api.plugin.Plugin;
import net.okocraft.userapi.UserAPIPlugin;

public class UserAPIBungee extends Plugin {

    @Override
    public void onEnable() {
        UserAPIPlugin.init(new BungeeConfig(this, "config.yml", true), getLogger());

        if (UserAPIPlugin.isReady() && UserAPIPlugin.get().getConfig().isListenerEnabled()) {
            getProxy().getPluginManager().registerListener(this, PlayerJoinListener.get());
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been successfully enabled.");
        }
    }

    @Override
    public void onDisable() {
        if (UserAPIPlugin.isReady()) {

            if (UserAPIPlugin.get().getConfig().isListenerEnabled()) {
                getProxy().getPluginManager().unregisterListeners(this);
            }

            UserAPIPlugin.shutdown();

            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been successfully disabled.");
        }
    }
}
