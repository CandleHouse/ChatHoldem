package org.chat.texasholdem.judge.ranking;

import org.chat.texasholdem.judge.entity.Player;
import org.chat.texasholdem.judge.entity.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * Class {@code FourOfTheKindRankingImpl}
 * 解析玩家手中的牌是不是四条(四张连续相同的牌)
 */
public class FourOfTheKindRankingImpl extends AbstractRanking {

    protected RankingResult doResolve(Player player) {

        RankingResult result = null;
        Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

        boolean hasFour = false;

        Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() == 4) {
                hasFour = true;
                break;
            }
        }

        if (hasFour) {
            result = new RankingResult();
            result.setRankingEnum(RankingEnum.FOUR_OF_THE_KIND);
        }

        return result;
    }

}
