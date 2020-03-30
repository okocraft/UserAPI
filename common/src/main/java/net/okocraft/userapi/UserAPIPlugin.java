package net.okocraft.userapi;

import com.github.siroshun09.sirolibrary.config.Yaml;
import com.github.siroshun09.sirolibrary.database.Database;
import com.github.siroshun09.sirolibrary.database.DatabaseType;
import com.github.siroshun09.sirolibrary.database.MySQL;
import com.github.siroshun09.sirolibrary.database.SQLite;
import com.github.siroshun09.sirolibrary.file.FileUtil;
import net.okocraft.userapi.api.UserAPI;
import net.okocraft.userapi.api.data.RenameData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Logger;

public class UserAPIPlugin {
    private static UserAPIPlugin INSTANCE;
    private static boolean READY = false;

    private final Configuration config;
    private final Database database;
    private final Logger logger;
    private UserTable table;

    private UserAPIPlugin(@NotNull Yaml yaml, @NotNull Logger logger) {
        this.config = new Configuration(yaml);
        this.logger = logger;
        database = createDatabase(config.getDatabaseType());
    }

    @NotNull
    public static UserAPIPlugin get() {
        if (INSTANCE == null) {
            throw new IllegalStateException("UserAPI is not initialized.");
        }
        return INSTANCE;
    }

    public static void init(@NotNull Yaml yaml, @NotNull Logger logger) {
        if (INSTANCE != null) {
            throw new IllegalStateException("UserAPI has already initialized.");
        }

        INSTANCE = new UserAPIPlugin(yaml, logger);
        INSTANCE.start();
    }

    public static boolean isReady() {
        return READY;
    }

    public static void shutdown() {
        if (INSTANCE != null) {
            INSTANCE.database.shutdown();
            READY = false;
        }
    }

    @NotNull
    public Configuration getConfig() {
        return config;
    }

    @NotNull
    public Logger getLogger() {
        return logger;
    }

    @NotNull
    public UserTable getTable() {
        if (INSTANCE == null) {
            throw new IllegalStateException("Database is unavailable.");
        }
        return table;
    }

    private void start() {
        database.start();
        table = new UserTable(database);
        UserAPI.setUserDataGetter(table);

        if (database.getDatabaseType().equals(DatabaseType.MYSQL) && config.isMigrationMode()) {
            try {
                migrate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        READY = true;
    }

    private void migrate() throws SQLException {
        getLogger().info("Start migrating from SQLite to MySQL...");
        Database sqlite = createDatabase(DatabaseType.SQLITE);
        sqlite.start();
        Set<RenameData> dataSet = new UserTable(sqlite).getAllRenameData();
        sqlite.shutdown();

        for (RenameData data : dataSet) {
            table.migrate(data);
            getLogger().info("Migrated: " + data.getUuid() + " (" + data.getName() + ")");
        }
        getLogger().info("Migration completed.");
    }

    @NotNull
    @Contract("_ -> new")
    private Database createDatabase(@NotNull DatabaseType type) {
        switch (type) {
            case MYSQL:
                getLogger().info("We use MySQL...");
                return new MySQL("UserAPI-Database",
                        config.getJdbcUrl(),
                        config.getYaml().getString("database.username", ""),
                        config.getYaml().getString("database.password", ""));
            case SQLITE:
            default:
                getLogger().info("We use SQLite...");
                Path path = Paths.get("./plugins/UserAPI/data.db");
                try {
                    FileUtil.createDirAndFile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new SQLite("UserAPI-Database", path);
        }
    }
}