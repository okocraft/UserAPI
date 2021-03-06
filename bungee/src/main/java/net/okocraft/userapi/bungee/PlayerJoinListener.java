package net.okocraft.userapi.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.okocraft.userapi.CheckResult;
import net.okocraft.userapi.Configuration;
import net.okocraft.userapi.UserAPIPlugin;
import net.okocraft.userapi.UserTable;
import net.okocraft.userapi.api.data.RenameData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final static PlayerJoinListener INSTANCE = new PlayerJoinListener();

    private PlayerJoinListener() {
    }

    @NotNull
    public static PlayerJoinListener get() {
        return INSTANCE;
    }

    @EventHandler
    public void onLogin(@NotNull PostLoginEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Configuration config = UserAPIPlugin.get().getConfig();
        UserTable table = UserAPIPlugin.get().getTable();

        try {
            String name = e.getPlayer().getName();
            CheckResult result = table.checkUser(uuid, name);
            if (result.equals(CheckResult.FIRST_LOGIN)) {
                broadcast(config.getFirstLoginMsg(name));
            }
        } catch (SQLException ex) {
            UserAPIPlugin.get().getLogger().severe("Exception occurred while executing SQL.");
            ex.printStackTrace();
            return;
        }

        try {
            RenameData data = table.getRenameData(uuid);
            if (isInNoticePeriod(data.getRenamedDate(), config.getNoticePeriod())) {
                broadcast(config.getNotificationMsg(data));
            }
        } catch (SQLException ex) {
            UserAPIPlugin.get().getLogger().severe("Exception occurred while executing SQL.");
            ex.printStackTrace();
        }
    }

    private boolean isInNoticePeriod(long renamedDate, long period) {
        return System.currentTimeMillis() - renamedDate <= period * 86400000;
    }

    private void broadcast(String message) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        ProxyServer.getInstance().broadcast(TextComponent.fromLegacyText(message));
    }
}
