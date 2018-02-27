package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.controller.AppSettings;
import com.alenaco.mytranslator.main.controller.translator.Named;
import com.alenaco.mytranslator.main.model.Cash;

/**
 * Created by alena on 28.02.18.
 */
@Named(name = "Hibernate Storage")
public class HibernateStorage extends AppSettings implements Storage {
    @Override
    public void saveCash() throws StorageException {

    }

    @Override
    public void restoreCash() throws StorageException {

    }

    @Override
    public Cash getCash() {
        return null;
    }
}
