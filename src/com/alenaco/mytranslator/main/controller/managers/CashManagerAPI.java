package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;

import java.util.List;
import java.util.Set;

/**
 * Created by alena on 07.03.18.
 */
public interface CashManagerAPI {
    Word getTranslation(String chars);

    void saveCash() throws StorageException;

    void removeWordsWithTranslations(List<Word> selectedWords);

    String getCashStr();

    Word createWord(String en, String ru, Language fromLang);

    String getTranslationsStr(Word word);

    void addNewTranslation(Word word);

    Set<Word> getWords();

    void addCashChangedListener(CashChangedListener listener);

    enum CashChangingType {
        ADD, CHANGE_CHARS, CHANGE_COUNT, ADD_TRANSLATION, DELETE
    }

    interface CashChangedListener {
        void cashChanged(Word word, CashChangingType changingType);
    }
}
