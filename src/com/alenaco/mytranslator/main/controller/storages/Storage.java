package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.model.Cash;

/**
 * @author kovalenko
 * @version $Id$
 */
public interface Storage {

    void saveCash() throws StorageException;

    void restoreCash() throws StorageException;

    Cash getCash();

    String getInstanceName();

//    void saveSession() throws StorageException;
//
//    void restoreSession() throws StorageException;
//
//    SessionContext getSession();
}
