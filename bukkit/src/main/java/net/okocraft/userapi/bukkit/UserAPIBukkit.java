package net.okocraft.userapi.bukkit;

import com.github.siroshun09.sirolibrary.bukkitutils.BukkitUtil;
import com.github.siroshun09.sirolibrary.config.BukkitConfig;
import com.github.siroshun09.sirolibrary.message.BukkitMessage;
import net.okocraft.userapi.UserAPIPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class UserAPIBukkit extends JavaPlugin {

    @Override
    public void onLoad() {
        super.onLoad();
        UserAPIPlugin.init(new BukkitConfig(this, "config.yml", true), getLogger());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (UserAPIPlugin.isReady() && UserAPIPlugin.get().getConfig().isListenerEnabled()) {
            BukkitUtil.registerEvents(PlayerJoinListener.get(), this);
            BukkitMessage.printEnabledMsg(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (UserAPIPlugin.isReady()) {
            if (UserAPIPlugin.get().getConfig().isListenerEnabled()) {
                BukkitUtil.unregisterEvents(this);
                BukkitMessage.printDisabledMsg(this);
            }
            UserAPIPlugin.shutdown();
        }
    }
}