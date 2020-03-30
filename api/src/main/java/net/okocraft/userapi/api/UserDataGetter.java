package net.okocraft.userapi.api;

import net.okocraft.userapi.api.data.RenameData;
import net.okocraft.userapi.api.data.UserData;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * ユーザーデータをデータベースから取得するインターフェース。
 */
public interface UserDataGetter {

    /**
     * {@link UserData} を {@link UUID} でデータベースから取得する。
     *
     * @param uuid 取得するユーザーの {@link UUID}
     * @return {@link UserData}
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    UserData getUserData(@NotNull UUID uuid) throws SQLException;

    /**
     * ユーザーデータを名前でデータベースから取得する。
     * <p>
     * 取得できなかった場合、空の {@link Optional} を返す。
     *
     * @param name 取得するユーザーの名前
     * @return {@link Optional} でラップされた {@link UserData}
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    Optional<UserData> getUserDataByName(@NotNull String name) throws SQLException;

    /**
     * 改名データをデータベースから取得する。
     *
     * @param uuid 取得するユーザーの {@link UUID}
     * @return {@link RenameData}
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    RenameData getRenameData(@NotNull UUID uuid) throws SQLException;

    /**
     * データベースに記録されているすべてのユーザーデータを取得する。
     *
     * @return すべてのユーザーデータ
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    Set<UserData> getAllUserData() throws SQLException;

    /**
     * データベースに記録されているユーザーの名前をすべて取得する。
     *
     * @return データベースに記録されているすべてのユーザーネーム。
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    Set<String> getAllUserName() throws SQLException;

    /**
     * データベースから引数を含む名前のユーザーデータを取得する。
     *
     * @param name 名前
     * @return 引数で渡された名前を含むユーザーのデータセット
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    Set<UserData> searchUser(@NotNull String name) throws SQLException;

    /**
     * データベースに記録されているすべての改名データを取得する。
     *
     * @return すべての改名データ
     * @throws SQLException SQL の実行中に例外が発生した時
     */
    @NotNull
    Set<RenameData> getAllRenameData() throws SQLException;
}