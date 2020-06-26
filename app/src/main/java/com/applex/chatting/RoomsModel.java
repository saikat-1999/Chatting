package com.applex.chatting;

import java.util.Map;

public class RoomsModel {

    private Map<String, Long> block, typing;

    public RoomsModel() {
    }

    public Map<String, Long> getBlock() {
        return block;
    }

    public void setBlock(Map<String, Long> block) {
        this.block = block;
    }

    public Map<String, Long> getTyping() {
        return typing;
    }

    public void setTyping(Map<String, Long> typing) {
        this.typing = typing;
    }
}
