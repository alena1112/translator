package com.alenaco.mytranslator.main.controller.storages;

/**
 * @author kovalenko
 * @version $Id$
 */
public interface Storage<T> {

    void saveObject() throws StorageException;

    void restoreObject() throws StorageException;

    T getObject();
}
