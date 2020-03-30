package net.okocraft.userapi.bukkit;

import com.github.siroshun09.sirolibrary.message.BukkitMessage;
import net.okocraft.userapi.CheckResult;
import net.okocraft.userapi.Configuration;
import net.okocraft.userapi.UserAPIPlugin;
import net.okocraft.userapi.api.data.RenameData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
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
    public void onLogin(@NotNull AsyncPlayerPreLoginEvent e) {
        try {
            UUID uuid = e.getUniqueId();
            String name = e.getName();

            CheckResult result = UserAPIPlugin.get().getTable().checkUser(uuid, name);
            if (result.equals(CheckResult.FIRST_LOGIN)) {
                BukkitMessage.broadcastWithColor(UserAPIPlugin.get().getConfig().getFirstLoginMsg(name));
            }
        } catch (SQLException ex) {
            UserAPIPlugin.get().getLogger().severe("Exception occurred while executing SQL.");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        Configuration config = UserAPIPlugin.get().getConfig();
        UUID uuid = e.getPlayer().getUniqueId();

        try {
            RenameData data = UserAPIPlugin.get().getTable().getRenameData(uuid);
            if (isInNoticePeriod(data.getRenamedDate(), config.getNoticePeriod())) {
                BukkitMessage.broadcastWithColor(config.getNotificationMsg(data));
            }
        } catch (SQLException ex) {
            UserAPIPlugin.get().getLogger().severe("Exception occurred while executing SQL.");
            ex.printStackTrace();
        }
    }

    private boolean isInNoticePeriod(long renamedDate, long period) {
        return Duration.between(Instant.ofEpochMilli(renamedDate), Instant.now()).toDays() <= period;
    }
}
