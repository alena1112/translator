package com.alenaco.mytranslator.main.controller.storages;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * @author kovalenko
 * @version $Id$
 */
public class JSONStorage<T> implements Storage<T> {
    private static final String FILE_NAME = "xmlStorage.xml";

    private JAXBContext jaxbContext;
    private T object;

    public JSONStorage(T object) throws StorageException {
        this.object = object;
        try {
            jaxbContext = JAXBContext.newInstance(object.getClass());
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void saveObject() throws StorageException {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(object, file);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    @Override
    public void restoreObject() throws StorageException {
        try {
            File file = new File(System.getProperty("user.dir") + "/" + FILE_NAME);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            object = (T) jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            throw new StorageException(e.getMessage());
        }
    }

    public T getObject() {
        return object;
    }
}
