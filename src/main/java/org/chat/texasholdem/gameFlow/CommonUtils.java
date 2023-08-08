package org.chat.texasholdem.gameFlow;

import org.chat.texasholdem.chat.prompt.PlayerMoveHistory;
import org.chat.texasholdem.judge.entity.Player;

import java.util.List;

public class CommonUtils {
    public String getBettingRound(int nowRound) {
        if (nowRound == 0)  // pre-flop
            return "pre-flop";
        if (nowRound == 1)  // flop
            return "flop";
        if (nowRound == 2)  // turn
            return "turn";
        if (nowRound == 3)  // river
            return "river";

        return null;
    }

    public String getYourPosition(int yourIndex, int smallBlindIndex, int bigBlindIndex) {
        if (yourIndex == smallBlindIndex)
            return "small blind";
        if (yourIndex == bigBlindIndex)
            return "big blind";

        return null;
    }

    public void printYourPosition(int yourIndex, int smallBlindIndex, int bigBlindIndex) {
        String yourPosition = this.getYourPosition(yourIndex, smallBlindIndex, bigBlindIndex);
        if (yourPosition == null)
            System.out.println();
        else if (yourPosition.equals("small blind"))
            System.out.println(getColoredString("[小盲]", 30, 4, 44));
        else if (yourPosition.equals("big blind"))
            System.out.println(getColoredString("[大盲]", 30, 4, 43));
    }

    public int getMaxAmount(List<PlayerMoveHistory> playerMoveHistoryList) {
        int maxAmount = 0;
        for (PlayerMoveHistory playerMoveHistory : playerMoveHistoryList) {
            if (playerMoveHistory.getAmount() >= maxAmount)
                maxAmount = playerMoveHistory.getAmount();
        }
        return maxAmount;
    }

    public Player searchPlayerByName(String playerName, List<Player> playerList) {
        for (Player player : playerList) {
            if (player.getPlayerName().equals(playerName))
                return player;
        }
        return null;
    }

    public String getColoredString(String content, int fontColor, int fontType, int backgroundColor){
        return String.format("\033[%d;%d;%dm%s\033[0m", fontColor, fontType, backgroundColor, content);
    }

    public String getColoredString(String content, int fontColor, int fontType){
        return String.format("\033[%d;%dm%s\033[0m", fontColor, fontType, content);
    }
}
