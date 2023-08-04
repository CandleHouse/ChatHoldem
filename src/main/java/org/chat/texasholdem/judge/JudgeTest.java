package org.chat.texasholdem.judge;

import org.chat.texasholdem.judge.entity.Constants;
import org.chat.texasholdem.judge.entity.Dealer;
import org.chat.texasholdem.judge.entity.Player;

import java.util.List;

public class JudgeTest {

    public static void main(String[] args) {
        Dealer d = new Dealer();

        Player me = new Player("Player1");
        Player you = new Player("Player2");
        Player you2 = new Player("Player3");

        d.join(me);
        d.join(you);
        d.join(you2);

        d.start();
        d.showHand(Constants.TOTAL_ROUND-1);

        List<Player> players = d.getRankingPlayers();

        for (Player player : players) {
            System.out.println(player.toString());
        }
    }

}
