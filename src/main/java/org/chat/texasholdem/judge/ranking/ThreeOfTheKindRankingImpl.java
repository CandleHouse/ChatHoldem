package org.chat.texasholdem.judge.ranking;

import org.chat.texasholdem.judge.entity.Player;
import org.chat.texasholdem.judge.entity.RankingEnum;

import java.util.Iterator;
import java.util.Map;

/**
 * Class {@code ThreeOfTheKindRankingImpl}
 * 解析玩家手中的牌是不是三条(3+1+1)
 */
public class ThreeOfTheKindRankingImpl extends AbstractRanking {

    protected RankingResult doResolve(Player player) {

        RankingResult result = null;
        Map<Integer, Integer> rankCount = player.getCardsRankCountMap();

        boolean hasThree = false;

        Iterator<Map.Entry<Integer, Integer>> it = rankCount.entrySet().iterator();
        while (it.hasNext()) {
            if (it.next().getValue() == 3) {
                hasThree = true;
                break;
            }
        }

        if (hasThree) {
            result = new RankingResult();
            result.setRankingEnum(RankingEnum.THREE_OF_THE_KIND);
        }

        return result;
    }

}
