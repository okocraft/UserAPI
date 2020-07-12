package net.okocraft.userapi;

import com.github.siroshun09.configapi.common.Yaml;
import com.github.siroshun09.databaselibs.common.database.Database;
import net.okocraft.userapi.api.data.RenameData;
import org.jetbrains.annotations.NotNull;

public class Configuration {

    private final Yaml yaml;

    public Configuration(@NotNull Yaml yaml) {
        this.yaml = yaml;
    }

    public Yaml getYaml() {
        return yaml;
    }

    public Database.Type getDatabaseType() {
        String type = yaml.getString("database.type", "SQLite");
        try {
            return Database.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            UserAPIPlugin.get().getLogger().warning("Invalid database type: " + type);
            return Database.Type.SQLITE;
        }
    }

    @NotNull
    public String getJdbcUrl() {
        return "jdbc:mysql://" + yaml.getString("database.address", "localhost:3306") + "/" +
                yaml.getString("database.db-name", "userapi") + "?verifyServerCertificate=false&useSSL=true";
    }

    @NotNull
    public String getTablePrefix() {
        return yaml.getString("database.table-prefix", "userapi_");
    }

    public boolean isMigrationMode() {
        return yaml.getBoolean("migration-mode", false);
    }

    public Database.Type getMigrationFrom() {
        String type = yaml.getString("migration-from", "SQLite");
        try {
            return Database.Type.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            UserAPIPlugin.get().getLogger().warning("Invalid database type: " + type);
            return Database.Type.SQLITE;
        }
    }

    public boolean isListenerEnabled() {
        return yaml.getBoolean("enable-listener", true);
    }

    @NotNull
    public String getFirstLoginMsg(@NotNull String name) {
        return yaml.getString("first-login-msg", "&6* %name% が初めてログインしました!").replace("%name%", name);
    }

    @NotNull
    public String getNotificationMsg(@NotNull RenameData data) {
        return yaml.getString("rename.notification-msg", "&7* &b%name%&7 の昔の名前: &b%previous%")
                .replace("%previous%", data.getPreviousName()).replace("%name%", data.getName());
    }

    public long getNoticePeriod() {
        return yaml.getLong("rename.notice-period", 30);
    }
}
