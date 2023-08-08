package org.chat.texasholdem;

import org.chat.texasholdem.chat.prompt.GameLevel;
import org.chat.texasholdem.gameFlow.MainFlow;

public class StartGame {


    public static void main(String[] args) {

        int playerNum = 6;
        boolean goldFinger = true;
        GameLevel gameLevel = GameLevel.NORMAL;
        boolean allPromptsPrint = false;

        MainFlow mainFlow = new MainFlow(playerNum, goldFinger, gameLevel, allPromptsPrint);
        mainFlow.play();
    }
}
