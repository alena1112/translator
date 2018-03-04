package com.alenaco.mytranslator.main.ui.components;

import com.alenaco.mytranslator.main.model.Word;
import javafx.scene.control.Button;

/**
 * @author kovalenko
 * @version $Id$
 */
public class WordButton extends Button {
    private Word word;
    private WordButtonType type;

    public WordButton(Word word, WordButtonType type) {
        super(word.getChars());
        this.word = word;
        this.type = type;
        switch (type) {
            case WORD:
                setId("wordBtn");
                break;
            case TRANSLATION:
                setId("translationBtn");
                break;
        }
    }

    public Word getWord() {
        return word;
    }

    public enum WordButtonType {
        WORD, TRANSLATION
    }
}
