package net.okocraft.userapi.bukkit;

import com.github.siroshun09.configapi.bukkit.BukkitConfig;
import net.okocraft.userapi.UserAPIPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class UserAPIBukkit extends JavaPlugin {

    @Override
    public void onLoad() {
        UserAPIPlugin.init(new BukkitConfig(this, "config.yml", true), getLogger());
    }

    @Override
    public void onDisable() {
        if (UserAPIPlugin.isReady()) {

            if (UserAPIPlugin.get().getConfig().isListenerEnabled()) {
                HandlerList.unregisterAll(this);
            }

            UserAPIPlugin.shutdown();
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been successfully disabled.");
        }
    }

    @Override
    public void onEnable() {
        if (UserAPIPlugin.isReady() && UserAPIPlugin.get().getConfig().isListenerEnabled()) {
            getServer().getPluginManager().registerEvents(PlayerJoinListener.get(), this);
            getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " has been successfully enabled.");
        }
    }
}