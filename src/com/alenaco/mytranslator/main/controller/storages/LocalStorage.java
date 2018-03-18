package com.alenaco.mytranslator.main.controller.storages;

import com.alenaco.mytranslator.main.controller.Named;
import com.alenaco.mytranslator.main.model.Cash;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named("Local XML Storage")
public class LocalStorage implements Storage {
    private static final String FILE_NAME = "dict.xml";

    @Override
    public void saveCash(Cash cash) throws StorageException {

    }

    @Override
    public Cash restoreCash() throws StorageException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(System.getProperty("user.dir") + "/" + FILE_NAME);

            Node root = document.getDocumentElement();
            NodeList arTags = root.getChildNodes();

            for (int i = 0; i < arTags.getLength(); i++) {
                Node arTag = arTags.item(i);
                if (arTag.getNodeName().equals("ar")) {
                    NodeList kTags = arTag.getChildNodes();
                    for (int j = 0; j < kTags.getLength(); j++) {
                        Node kTag = kTags.item(j);
                        if (kTag.getNodeType() == Node.TEXT_NODE) {
                            List<String> words = normalizeEnChars(kTag.getNodeValue());
                            if (words != null) {
                                for (String chars : words) {
                                    System.out.println("EN " + chars);
                                }
                            }
                        } else if (kTag.getNodeName().equals("k")) {
                            String ruChars = kTag.getFirstChild().getNodeValue();
                            ruChars = normalizeRuChars(ruChars);
                            if (ruChars != null) {
                                System.out.println("RU " + ruChars);
                            }
                        }
                    }
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new StorageException(e.getMessage());
        }
        return null;
    }

    private String normalizeRuChars(String chars) {
        if (StringUtils.isNotBlank(chars)) {
            String result = chars.replaceAll("\n", "").trim();
            if (StringUtils.isNotBlank(result)) {
                return result.replaceFirst("-", "");
            }
        }
        return null;
    }

    private List<String> normalizeEnChars(String chars) {
        if (StringUtils.isNotBlank(chars)) {
            String result = chars.replaceAll("\n", "").trim();
            if (StringUtils.isNotBlank(result)) {
                result = result.replaceFirst("-", "");
                //находим в строке цифры, делим на части, находим первые вхождения английских слов
                String[] list = result.split("\\d");
                List<String> listChars = new ArrayList<>();
                for (String str : list) {
                    String[] list = result.split("\\d");
                }
                return listChars;
            }
        }
        return null;
    }
}
