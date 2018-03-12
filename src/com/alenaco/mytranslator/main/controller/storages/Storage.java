package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.model.Cash;

/**
 * @author kovalenko
 * @version $Id$
 */
public interface Storage {

    void saveCash(Cash cash) throws StorageException;

    Cash restoreCash() throws StorageException;

//    void saveSession() throws StorageException;
//
//    void restoreSession() throws StorageException;
//
//    SessionContext getSession();
}
