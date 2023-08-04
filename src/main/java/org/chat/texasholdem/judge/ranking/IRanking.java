package org.chat.texasholdem.judge.ranking;

import org.chat.texasholdem.judge.entity.Player;

/**
 * Interface {@code IRanking} 牌型解析接口, 负责解析玩家手中的牌处于什么牌型
 */
public interface IRanking {
    RankingResult resolve(Player player);
}
