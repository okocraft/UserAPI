package net.okocraft.userapi;

import com.github.siroshun09.sirolibrary.database.Database;
import net.okocraft.userapi.api.UserDataGetter;
import net.okocraft.userapi.api.data.RenameData;
import net.okocraft.userapi.api.data.UserData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserTable implements UserDataGetter {

    private final static String PLAYER_SELECT_NAME_BY_UUID = "select name from %table% where uuid=? limit 1";
    private final static String PLAYER_SELECT_UUID_BY_NAME = "select uuid from %table% where name=? limit 1";
    private final static String PLAYER_SELECT_UUID_BY_NAME_ALL = "select uuid from %table% where name=?";
    private final static String PLAYER_SELECT_RENAME_DATA_BY_UUID = "select name, previous_name, renamed_date from %table% where uuid=? limit 1";
    private final static String PLAYER_INSERT_NAME_BY_UUID = "insert into %table% (uuid, name) values(?,?)";
    private final static String PLAYER_RENAME_BY_UUID = "update %table% set name=?, previous_name=?, renamed_date=? where uuid=? limit 1";
    private final static String SELECT_ALL_USER_DATA = "select uuid, name from %table%";
    private final static String SELECT_ALL_RENAME_DATA = "select uuid, name, previous_name, renamed_date from %table%";
    private final static String SELECT_ALL_PLAYER_NAME = "select name from %table%";
    private final static String SEARCH_USER_NAME = "select uuid, name from %table% like ?";

    private final Database database;

    public UserTable(@NotNull Database database) {
        this.database = database;
        createTable();
    }

    @NotNull
    public UserData getUserData(@NotNull UUID uuid) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_SELECT_NAME_BY_UUID));
        st.setString(1, uuid.toString());

        ResultSet result = st.executeQuery();
        UserData data;
        if (result.next()) {
            data = createUserData(uuid, result);
        } else {
            data = new UserData(uuid, "");
        }
        close(c, st, result);
        return data;
    }

    @NotNull
    public Optional<UserData> getUserDataByName(@NotNull String name) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_SELECT_UUID_BY_NAME));
        st.setString(1, name);

        ResultSet result = st.executeQuery();
        UserData data = null;
        if (result.next()) {
            String strUuid = result.getString("uuid");
            if (strUuid != null) {
                try {
                    data = new UserData(UUID.fromString(strUuid), name);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        close(c, st, result);
        return Optional.ofNullable(data);
    }

    @NotNull
    public RenameData getRenameData(@NotNull UUID uuid) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_SELECT_RENAME_DATA_BY_UUID));
        st.setString(1, uuid.toString());

        ResultSet result = st.executeQuery();
        RenameData data;
        if (result.next()) {
            data = createRenameData(uuid, result);
        } else {
            data = new RenameData(uuid, "", "", 0);
        }
        close(c, st, result);
        return data;
    }

    @NotNull
    @Override
    public Set<UserData> getAllUserData() throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(SELECT_ALL_USER_DATA));
        ResultSet result = st.executeQuery();

        Set<UserData> dataSet = new HashSet<>();
        while (result.next()) {
            String strUuid = result.getString("uuid");
            if (strUuid != null) {
                try {
                    dataSet.add(createUserData(UUID.fromString(strUuid), result));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        close(c, st, result);
        return Set.copyOf(dataSet);
    }

    @Override
    @NotNull
    public Set<String> getAllUserName() throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(SELECT_ALL_PLAYER_NAME));
        ResultSet result = st.executeQuery();

        Set<String> nameSet = new HashSet<>();
        while (result.next()) {
            nameSet.add(result.getString("name"));
        }
        close(c, st, result);
        return Set.copyOf(nameSet);
    }

    @NotNull
    @Override
    public Set<UserData> searchUser(@NotNull String name) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(SEARCH_USER_NAME));
        st.setString(1, name + "%");

        ResultSet result = st.executeQuery();

        Set<UserData> dataSet = new HashSet<>();
        while (result.next()) {
            String strUuid = result.getString("uuid");
            if (strUuid != null) {
                try {
                    dataSet.add(createUserData(UUID.fromString(strUuid), result));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        close(c, st, result);
        return Set.copyOf(dataSet);
    }

    @NotNull
    @Override
    public Set<RenameData> getAllRenameData() throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(SELECT_ALL_RENAME_DATA));
        ResultSet result = st.executeQuery();

        Set<RenameData> dataSet = new HashSet<>();
        while (result.next()) {
            String strUuid = result.getString("uuid");
            if (strUuid != null) {
                try {
                    dataSet.add(createRenameData(UUID.fromString(strUuid), result));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        close(c, st, result);
        return Set.copyOf(dataSet);
    }

    public CheckResult checkUser(@NotNull UUID uuid, @NotNull String name) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_SELECT_NAME_BY_UUID));
        st.setString(1, uuid.toString());

        ResultSet result = st.executeQuery();
        if (!result.next()) {
            close(c, st, result);
            saveNewPlayer(uuid, name);
            return CheckResult.FIRST_LOGIN;
        } else {
            if (!result.getString("name").equals(name)) {
                close(c, st, result);
                rename(uuid, name);
                return CheckResult.RENAMED;
            }
        }
        close(c, st, result);
        return CheckResult.NONE;
    }

    private void saveNewPlayer(@NotNull UUID uuid, @NotNull String name) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_INSERT_NAME_BY_UUID));

        st.setString(1, uuid.toString());
        st.setString(2, name);
        st.execute();
        close(c, st);
    }

    private void rename(@NotNull UUID uuid, @NotNull String newName) throws SQLException {
        String previousName = getUserData(uuid).getName();

        checkExistingName(uuid, newName);

        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_RENAME_BY_UUID));

        st.setString(1, newName);
        st.setString(2, previousName);
        st.setLong(3, System.currentTimeMillis());
        st.setString(4, uuid.toString());
        st.execute();
        close(c, st);
    }

    private void checkExistingName(@NotNull UUID uuid, @NotNull String name) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_SELECT_UUID_BY_NAME_ALL));
        st.setString(1, name);

        ResultSet result = st.executeQuery();
        close(c, st);
        while (result.next()) {
            String strUuid = result.getString("uuid");
            if (!strUuid.equalsIgnoreCase(uuid.toString())) {
                replaceToEmptyName(strUuid, name);
            }
        }
        result.close();
    }

    private void replaceToEmptyName(@NotNull String uuid, @NotNull String previousName) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st = c.prepareStatement(replaceTableName(PLAYER_RENAME_BY_UUID));
        st.setString(1, "");
        st.setString(2, previousName);
        st.setLong(3, System.currentTimeMillis());
        st.setString(4, uuid);
        st.execute();
        close(c, st);
    }

    @NotNull
    private String replaceTableName(@NotNull String sql) {
        return sql.replaceAll("%table%", UserAPIPlugin.get().getConfig().getTablePrefix() + "users");
    }

    private void createTable() {
        try (Connection c = database.getConnection(); Statement statement = c.createStatement()) {
            String tableName = UserAPIPlugin.get().getConfig().getTablePrefix() + "users";
            statement.addBatch(
                    "create table if not exists " + tableName + " (" +
                            "uuid varchar(36) not null default \"\", " +
                            "name varchar(16) not null default \"\", " +
                            "previous_name varchar(16) not null default \"\", " +
                            "renamed_date bigint UNSIGNED not null default 0, " +
                            "primary key(`uuid`)" + ");"
            );
            statement.executeBatch();
        } catch (SQLException e) {
            UserAPIPlugin.get().getLogger().severe("Exception occurred while executing SQL.");
            e.printStackTrace();
        }
    }

    private void close(@NotNull Connection c, @NotNull Statement st, @NotNull ResultSet resultSet) throws SQLException {
        close(c, st);
        if (!resultSet.isClosed()) resultSet.close();

    }

    private void close(@NotNull Connection c, @NotNull Statement st) throws SQLException {
        if (!c.isClosed()) c.close();
        if (!st.isClosed()) st.close();
    }

    @NotNull
    @Contract("_, _ -> new")
    private UserData createUserData(@NotNull UUID uuid, @NotNull ResultSet resultSet) throws SQLException {
        return new UserData(
                uuid,
                Objects.requireNonNullElse(resultSet.getString("name"), "")
        );
    }

    @NotNull
    private RenameData createRenameData(@NotNull UUID uuid, @NotNull ResultSet resultSet) throws SQLException {
        return new RenameData(
                uuid,
                Objects.requireNonNullElse(resultSet.getString("name"), ""),
                Objects.requireNonNullElse(resultSet.getString("previous_name"), ""),
                resultSet.getLong("renamed_date")
        );
    }

    void migrate(@NotNull RenameData data) throws SQLException {
        Connection c = database.getConnection();
        PreparedStatement st1 = c.prepareStatement(replaceTableName(PLAYER_SELECT_NAME_BY_UUID));
        st1.setString(1, data.getUuid().toString());
        ResultSet result = st1.executeQuery();
        if (result.next()) {
            close(c, st1, result);
            c = database.getConnection();
            PreparedStatement st2 = c.prepareStatement(replaceTableName(PLAYER_RENAME_BY_UUID));
            st2.setString(1, data.getName());
            st2.setString(2, data.getPreviousName());
            st2.setLong(3, data.getRenamedDate());
            st2.setString(4, data.getUuid().toString());
            st2.execute();
            close(c, st2);
        } else {
            close(c, st1, result);
            c = database.getConnection();
            PreparedStatement st2 = c.prepareStatement(replaceTableName(PLAYER_INSERT_NAME_BY_UUID));
            st2.setString(1, data.getUuid().toString());
            st2.setString(2, data.getName());
            st2.execute();
            close(c, st2);

            c = database.getConnection();
            PreparedStatement st3 = c.prepareStatement(replaceTableName(PLAYER_RENAME_BY_UUID));
            st3.setString(1, data.getName());
            st3.setString(2, data.getPreviousName());
            st3.setLong(3, data.getRenamedDate());
            st3.setString(4, data.getUuid().toString());
            st3.execute();
            close(c, st3);
        }
    }
}