package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.controller.Named;
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
@Named("JSON Storage")
public class JSONStorage implements Storage {
    private JAXBContext jaxbContext;

    private static final String FILE_NAME = "xmlStorage.xml";

    public JSONStorage() throws StorageException {
        try {
            jaxbContext = JAXBContext.newInstance(Cash.class);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void saveCash(Cash cash) throws StorageException {
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
    public Cash restoreCash() throws StorageException {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (Cash) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }
}
