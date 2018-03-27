package com.alenaco.mytranslator.main.ui.listeners;

import com.alenaco.mytranslator.main.controller.managers.CashManagerAPI;
import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.UIApp;
import com.alenaco.mytranslator.main.ui.components.CashListViewHBox;
import com.alenaco.mytranslator.main.ui.components.WordButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kovalenko
 * @version $Id$
 */
public class UIAppCashChangedListener implements CashManagerAPI.CashChangedListener {
    private UIApp app;
    private SessionManager sessionManager;

    public UIAppCashChangedListener(UIApp app, SessionManager sessionManager) {
        this.app = app;
        this.sessionManager = sessionManager;
    }

    @Override
    public void cashChanged(Word word, CashManagerAPI.CashChangingType changingType) {
        switch (changingType) {
            case ADD:
                app.getPreviousWordsList().add(0, new CashListViewHBox(word, sessionManager, app.getPrimaryStage()));
                break;
            case CHANGE_CHARS:
                List<WordButton> wordButtons = findWordButtons(word);
                for (WordButton wB : wordButtons) {
                    wB.setText(word.getChars());
                }
                break;
            case CHANGE_COUNT:
                break;
            case DELETE:
                deleteWordButtons(word);
                break;
        }
    }

    @Override
    public void addTranslation(Word word, Word translation) {
        for (CashListViewHBox item : app.getPreviousWordsList()) {
            WordButton wB = item.getBaseWordBtn();
            if (wB.getWord().equals(word)) {
                item.createTranslationTextButton(translation);
            }
        }
    }

    private List<WordButton> findWordButtons(Word word) {
        List<WordButton> foundItems = new ArrayList<>();
        for (CashListViewHBox item : app.getPreviousWordsList()) {
            WordButton wB = item.getWordButton(word);
            if (wB != null) {
                foundItems.add(wB);
            }
        }
        return foundItems;
    }

    private void deleteWordButtons(Word word) {
        List<CashListViewHBox> toDelete = new ArrayList<>();
        for (CashListViewHBox item : app.getPreviousWordsList()) {
            WordButton wB = item.getWordButton(word);
            if (wB != null) {
                item.deleteWordButton(wB);
            }
            if (item.isListViewEmpty()) {
                toDelete.add(item);
            }
        }
        app.getPreviousWordsList().removeAll(toDelete);
    }
}
