package org.chat.texasholdem.chat.entity;


public enum ChatCore {

//    ChatGPT("ChatGPT"),
    ChatGLM("ChatGLM");

    private String modelName;

    ChatCore(String modelName) {
        this.modelName = modelName;
    }
}
