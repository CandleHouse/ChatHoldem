package org.chat.texasholdem.judge.entity;

/**
 * Enum {@code CardSuitEnum} 扑克牌的花色.
 */
public enum CardSuitEnum {
    HEART("H"),
    DIAMOND("D"),
    SPADE("S"),
    CLUB("C");

    private String name;

    CardSuitEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
