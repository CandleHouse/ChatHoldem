package org.chat.texasholdem.judge.ranking;

import org.chat.texasholdem.judge.entity.Player;
import org.chat.texasholdem.judge.entity.RankingEnum;

/**
 * Class {@code DefaultRankingImpl}
 * 默认实现, 返回HighCard类型
 */
public class DefaultRankingImpl extends AbstractRanking {

    protected RankingResult doResolve(Player player) {

        RankingResult result = new RankingResult();
        result.setRankingEnum(RankingEnum.HIGH_CARD);

        return result;
    }

}
