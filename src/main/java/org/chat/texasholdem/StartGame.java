package org.chat.texasholdem;

import org.chat.texasholdem.chat.entity.ChatCore;
import org.chat.texasholdem.chat.entity.GameLevel;
import org.chat.texasholdem.gameFlow.MainFlow;

public class StartGame {


    public static void main(String[] args) {

        ChatCore chatCore = ChatCore.ChatGLM;
        int playerNum = 6;
        boolean goldFinger = true;
        GameLevel gameLevel = GameLevel.NORMAL;
        boolean allPromptsPrint = false;

        MainFlow mainFlow = new MainFlow(chatCore, playerNum, goldFinger, gameLevel, allPromptsPrint);
        mainFlow.play();
    }
}
