package org.chat.texasholdem.judge.ranking;

import org.chat.texasholdem.judge.entity.Card;
import org.chat.texasholdem.judge.entity.Player;
import org.chat.texasholdem.judge.entity.RankingEnum;

import java.util.List;

/**
 * Class {@code FlushRankingImpl}
 * 解析玩家手中的牌是不是同花(花色连续一样)
 */
public class FlushRankingImpl extends AbstractRanking {

    protected RankingResult doResolve(Player player) {

        RankingResult result = null;

        List<Card> cards = player.getCards();
        if (this.isSameSuit(cards)) { // 如果是同色
            result = new RankingResult();
            result.setRankingEnum(RankingEnum.FLUSH);
        }

        return result;
    }

}
