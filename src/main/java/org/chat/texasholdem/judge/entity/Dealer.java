package org.chat.texasholdem.judge.entity;

import org.chat.texasholdem.judge.ranking.RankingFacade;

import java.util.*;

/**
 * Class {@code Dealer} 荷官, 负责发牌, 启动游戏.
 */
public class Dealer {

    private Poker poker;
    public Board board;
    private List<Player> playerList;

    public Dealer() {
        this.poker = new Poker();
        this.playerList = new ArrayList<Player>();
    }

    public Dealer(Player top, Player... players) {
        this();
        this.playerList.add(top);
        this.playerList.addAll(Arrays.asList(players));
    }

    public Dealer(List<Player> playerList) {
        this();
        this.playerList.addAll(playerList);
    }

    /**
     * 新增玩家
     *
     * @param player
     */
    public void join(Player player) {
        this.playerList.add(player);
    }

    /**
     * 获得玩家数量
     *
     * @return
     */
    public int getPlayerSize() {
        return this.playerList.size();
    }

    public List<Player> getPlayerList() {
        return this.playerList;
    }

    /**
     * 开始游戏, 负责给每个玩家发牌和给桌子发牌
     */
    public void start() {
        for (int i = 0; i < this.playerList.size(); i++) {
            for (int j = 0; j < Constants.HAND_CARD_NUMERS; j++) {
                this.playerList.get(i).addCard(this.poker.dispatch());
            }
        }

        List<Card> tableCards = new ArrayList<Card>();
        for (int j = 0; j < Constants.DESK_CARD_NUMERS; j++) {
            tableCards.add(this.poker.dispatch());
        }
        this.board = new Board(tableCards);
    }

    /**
     * 玩家弃牌
     */
    public void playerFold(String playerName) {
        Iterator<Player> iterator = this.playerList.iterator();
        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (player.getPlayerName().equals(playerName)) {
                iterator.remove();
            }
        }
    }

    /**
     * 先把牌桌上的牌加入所有玩家手中，然后计算每个玩家的牌型
     * 需要把可能的组合中ranking最靠前的作为这个玩家跟所有玩家比较的牌型
     */
    public void showHand(int nowRound) {

        for (int i = 0; i < this.playerList.size(); i++) {
            List<Player> permutation = new ArrayList<Player>();

            for (int a = 0; a < nowRound; a++) {
                for (int b = a + 1; b < nowRound+1; b++) {
                    for (int c = b + 1; c < nowRound+2; c++) {
                        Player tempPlayer = new Player(this.playerList.get(i));
                        tempPlayer.addCard(this.board.getCards().get(a));
                        tempPlayer.addCard(this.board.getCards().get(b));
                        tempPlayer.addCard(this.board.getCards().get(c));
                        permutation.add(tempPlayer);
                    }
                }
            }

            for (int m = 0; m < permutation.size(); m++) {
                RankingFacade.getInstance().resolve(permutation.get(m));
            }
            Collections.sort(permutation);
            this.playerList.set(i, permutation.get(0));
        }

    }

    public List<Player> getRankingPlayers() {
        Collections.sort(this.playerList);
        return this.playerList;
    }
}
