package net.okocraft.userapi.bungee;

import com.github.siroshun09.sirolibrary.bungeeutils.BungeeUtil;
import com.github.siroshun09.sirolibrary.config.BungeeConfig;
import com.github.siroshun09.sirolibrary.message.BungeeMessage;
import net.md_5.bungee.api.plugin.Plugin;
import net.okocraft.userapi.UserAPIPlugin;

public class UserAPIBungee extends Plugin {

    @Override
    public void onEnable() {
        super.onEnable();
        UserAPIPlugin.init(new BungeeConfig(this, "config.yml", true), getLogger());
        if (UserAPIPlugin.isReady() && UserAPIPlugin.get().getConfig().isListenerEnabled()) {
            BungeeUtil.registerListener(this, PlayerJoinListener.get());
            BungeeMessage.printEnabledMsg(this);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (UserAPIPlugin.isReady()) {
            if (UserAPIPlugin.get().getConfig().isListenerEnabled()) {
                BungeeUtil.unregisterListeners(this);
            }
            UserAPIPlugin.shutdown();
            BungeeMessage.printDisabledMsg(this);
        }
    }
}
