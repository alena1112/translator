package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.controller.Named;
import com.alenaco.mytranslator.main.model.Cash;

/**
 * Created by alena on 28.02.18.
 */
@Named("Hibernate Storage")
public class HibernateStorage implements Storage {

    @Override
    public void saveCash(Cash cash) throws StorageException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Cash restoreCash() throws StorageException {
        throw new UnsupportedOperationException();
    }
}
