package com.example.stopthebomb.models;

public class DialogueBlock {
    private String title;
    private String dialogue;

    public DialogueBlock(String title, String dialogue) {
        this.title = title;
        this.dialogue = dialogue;
    }

    public String getTitle() {
        return title;
    }

    public String getDialogue() {
        return dialogue;
    }
}