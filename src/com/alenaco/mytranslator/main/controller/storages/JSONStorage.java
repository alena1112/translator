package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.controller.AppSettings;
import com.alenaco.mytranslator.main.controller.translator.Named;
import com.alenaco.mytranslator.main.model.Cash;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * @author kovalenko
 * @version $Id$
 */
@Named(name = "JSON Storage")
public class JSONStorage extends AppSettings implements Storage {
    private static final String FILE_NAME = "xmlStorage.xml";

    private JAXBContext jaxbContext;
    private Cash cash;

    public JSONStorage() throws StorageException {
        try {
            jaxbContext = JAXBContext.newInstance(Cash.class);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void saveCash() throws StorageException {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(cash, file);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void restoreCash() throws StorageException {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            cash = (Cash) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    public Cash getCash() {
        return cash;
    }

    @Override
    public String getInstanceName() {
        return null;
    }
}
