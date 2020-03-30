package net.okocraft.userapi.api.data;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RenameData extends UserData {

    private final String previousName;
    private final long renamedDate;

    /**
     * コンストラクタ
     *
     * @param uuid         ユーザーの {@link UUID}
     * @param name         ユーザーの名前
     * @param previousName ユーザーの前の名前
     * @param renamedDate  ユーザーが改名した時間
     */
    public RenameData(@NotNull UUID uuid, @NotNull String name, @NotNull String previousName, long renamedDate) {
        super(uuid, name);
        this.previousName = previousName;
        this.renamedDate = renamedDate;
    }

    /**
     * ユーザーの前の名前を返す。
     * <p>
     * 改名が記録されてない場合は空文字になる。
     *
     * @return 前の名前
     */
    public String getPreviousName() {
        return previousName;
    }

    /**
     * ユーザーが改名した時間を返す。
     * <p>
     * 改名が記録されてない場合は 0 が返される。
     *
     * @return 改名時間
     */
    public long getRenamedDate() {
        return renamedDate;
    }
}
