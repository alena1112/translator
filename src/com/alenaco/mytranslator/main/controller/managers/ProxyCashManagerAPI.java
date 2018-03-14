package com.alenaco.mytranslator.main.controller.managers;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import org.apache.commons.collections.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kovalenko
 * @version $Id$
 */
//temporary class in order to check cash for server-client application
public class ProxyCashManagerAPI implements CashManagerAPI {
    private CashManagerAPI serviceManager;

    public ProxyCashManagerAPI(Class storageClass) throws StorageException, InvocationTargetException,
            NoSuchMethodException, InstantiationException, IllegalAccessException {
        serviceManager = new ServiceCashManager(storageClass);
    }

    @Override
    public Word getTranslation(String chars) {
        Word translation = serviceManager.getTranslation(chars);
        if (translation != null) {
            return translation.getCopyWord();
        }
        return null;
    }

    @Override
    public void saveCash() throws StorageException {
        serviceManager.saveCash();
    }

    @Override
    public void removeWordsWithTranslations(List<Word> selectedWords) {
        List<Word> words = new ArrayList<>(selectedWords.size());
        for (Word word : selectedWords) {
            words.add(word.getCopyWord());
        }
        serviceManager.removeWordsWithTranslations(words);
    }

    @Override
    public String getCashStr() {
        return serviceManager.getCashStr();
    }

    @Override
    public Word createWord(String en, String ru, Language fromLang) {
        Word word = serviceManager.createWord(en, ru, fromLang);
        if (word != null) {
            return word.getCopyWord();
        }
        return null;
    }

    @Override
    public String getTranslationsStr(Word word) {
        return word != null ? serviceManager.getTranslationsStr(word.getCopyWord()) : null;
    }

    @Override
    public void addNewTranslation(Word word) {
        if (word != null) {
            serviceManager.addNewTranslation(word.getCopyWord());
        }
    }

    @Override
    public Set<Word> getWords() {
        Set<Word> words = serviceManager.getWords();
        if (CollectionUtils.isNotEmpty(words)) {
            Set<Word> copies = new HashSet<>();
            for (Word word : words) {
                copies.add(word.getCopyWord());
            }
            return copies;
        }
        return words;
    }

    @Override
    public void addCashChangedListener(CashChangedListener listener) {
        serviceManager.addCashChangedListener(listener);
    }

    @Override
    public Word findWordById(UUID id) {
        Word word = serviceManager.findWordById(id);
        if (word != null) {
            return word.getCopyWord();
        }
        return null;
    }

    @Override
    public void fireCashChangedListeners(Word word, CashChangingType changingType) {
        if (word != null) {
            serviceManager.fireCashChangedListeners(word.getCopyWord(), changingType);
        }
    }

    @Override
    public void fireAddTranslationListeners(Word word, Word translation) {
        if (word != null && translation != null) {
            serviceManager.fireAddTranslationListeners(word.getCopyWord(), translation.getCopyWord());
        }
    }
}
