package org.chat.texasholdem.chat.entity;

/**
 * Enum {@code GameLevel} 游戏难度.
 */
public enum GameLevel {
    EASY(1),
    NORMAL(2);
//    HARD(3);

    private Integer level;

    GameLevel(Integer level) {
        this.level = level;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
