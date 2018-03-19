package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by alena on 07.03.18.
 */
public interface CashManagerAPI {

    void restoreCash(Class storageClass) throws StorageException, InvocationTargetException, NoSuchMethodException,
            InstantiationException, IllegalAccessException;

    Word getTranslation(String chars);

    void saveCash() throws StorageException;

    void removeWords(List<Word> selectedWords);

    String getCashStr();

    Word createWord(String en, String ru, Language fromLang);

    String getTranslationsStr(Word word);

    void addNewTranslation(Word word);

    Set<Word> getWords();

    void addCashChangedListener(CashChangedListener listener);

    void fireCashChangedListeners(Word word, CashChangingType changingType);

    void fireAddTranslationListeners(Word word, Word translation);

    Word findWordById(UUID id);

    void importFileIntoCash() throws StorageException;

    enum CashChangingType {
        ADD, CHANGE_CHARS, CHANGE_COUNT, DELETE
    }

    interface CashChangedListener {
        void cashChanged(Word word, CashChangingType changingType);

        void addTranslation(Word word, Word translation);
    }
}
