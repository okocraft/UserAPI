package net.okocraft.userapi.api;

import net.okocraft.userapi.api.data.RenameData;
import net.okocraft.userapi.api.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * UserAPI からデータを取得するクラス。
 */
public final class UserAPI {
    private static UserDataGetter GETTER;

    public static void setUserDataGetter(@NotNull UserDataGetter getter) {
        GETTER = getter;
    }

    /**
     * ユーザーデータを取得する。
     *
     * @param uuid 取得するユーザーの {@link UUID}
     * @return {@link UserData}
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static UserData getUserData(@NotNull UUID uuid) throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getUserData(uuid);
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * ユーザーデータを名前から取得する。
     * <p>
     * 取得できなかった場合、空の {@link Optional} を返す。
     *
     * @param name 取得するユーザーの名前
     * @return {@link Optional} でラップされた {@link UserData}
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static Optional<UserData> getUserDataByName(@NotNull String name) throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getUserDataByName(name);
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * 改名データを取得する。
     *
     * @param uuid 取得するユーザーの {@link UUID}
     * @return {@link RenameData}
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static RenameData getRenameData(@NotNull UUID uuid) throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getRenameData(uuid);
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * すべてのユーザーデータを取得する。
     *
     * @return すべてのユーザーデータ
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static Set<UserData> getAllUserData() throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getAllUserData();
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * ユーザーの名前をすべて取得する。
     *
     * @return データベースに記録されているすべてのユーザーネーム。
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static Set<String> getAllUserName() throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getAllUserName();
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * 引数を含む名前のユーザーデータを取得する。
     *
     * @param name 名前
     * @return 引数で渡された名前を含むユーザーのデータセット
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static Set<UserData> searchUser(@NotNull String name) throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.searchUser(name);
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }

    /**
     * すべての改名データを取得する。
     *
     * @return すべての改名データ
     * @throws SQLException          SQL の実行中に例外が発生した時
     * @throws IllegalStateException データを取得できない時
     */
    @NotNull
    public static Set<RenameData> getAllRenameData() throws SQLException, IllegalStateException {
        if (GETTER != null) {
            return GETTER.getAllRenameData();
        } else {
            throw new IllegalStateException("UserAPI cannot be used.");
        }
    }
}
