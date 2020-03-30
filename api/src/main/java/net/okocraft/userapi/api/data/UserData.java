package net.okocraft.userapi.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * ユーザーデータクラス。
 */
public class UserData {

    private final UUID uuid;
    private final String name;

    /**
     * コンストラクタ。
     *
     * @param uuid ユーザーの {@link UUID}
     * @param name ユーザーの名前
     */
    public UserData(@NotNull UUID uuid, @NotNull String name) {
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * ユーザーの {@link UUID} を返す。
     *
     * @return ユーザーの {@link UUID}
     */
    @NotNull
    public UUID getUuid() {
        return uuid;
    }

    /**
     * ユーザーの名前 (Minecraft ID) を返す。
     * <p>
     * 改名が記録されてない場合は 空文字になる。
     *
     * @return ユーザーの名前
     */
    @NotNull
    public String getName() {
        return name;
    }
}
