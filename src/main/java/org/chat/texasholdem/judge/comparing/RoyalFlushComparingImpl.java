package org.chat.texasholdem.judge.comparing;

import org.chat.texasholdem.judge.entity.Player;

/**
 * Class {@code RoyalFlushComparingImpl}
 * 皇家同花顺的大小比较(唯一, 不考虑花色, 返回0)
 */
public class RoyalFlushComparingImpl extends AbstractComparing {
    public int compare(Player o1, Player o2) {
        return 0;
    }
}
