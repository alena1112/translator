package com.alenaco.mytranslator.main.controller.importing;

import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
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
import java.util.List;

public class ImportWordsFromFile {
    private static final String FILE_NAME = "dict.xml";

    public List<Word> importWords() throws StorageException {
        List<Word> allWords = new ArrayList<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(System.getProperty("user.dir") + "/" + FILE_NAME);

            Node root = document.getDocumentElement();
            NodeList arTags = root.getChildNodes();

            for (int i = 0; i < arTags.getLength(); i++) {
                Node arTag = arTags.item(i);
                if (arTag.getNodeName().equals("ar")) {
                    NodeList kTags = arTag.getChildNodes();
                    Word baseWord = null;
                    for (int j = 0; j < kTags.getLength(); j++) {
                        Node kTag = kTags.item(j);
                        if (kTag.getNodeType() == Node.TEXT_NODE) {
                            List<String> words = normalizeEnChars(kTag.getNodeValue());
                            if (words != null) {
                                for (String chars : words) {
                                    Word enWord = new Word(chars, Language.EN);
                                    allWords.add(enWord);
                                    if (baseWord != null) {
                                        enWord.addTranslation(baseWord);
                                        baseWord.addTranslation(enWord);
                                    }
                                    System.out.println("EN " + chars);
                                }
                            }
                        } else if (kTag.getNodeName().equals("k")) {
                            String ruChars = kTag.getFirstChild().getNodeValue();
                            ruChars = normalizeRuChars(ruChars);
                            if (ruChars != null) {
                                baseWord = new Word(ruChars, Language.RU);
                                allWords.add(baseWord);
                                System.out.println("RU " + ruChars);
                            }
                        }
                    }
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new StorageException(e.getMessage());
        }

        return allWords;
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
            String cleanStr = chars.replaceAll("[а-яА-ЯёЁ\n]", "").trim();//убираем перевод строк, русские буквы
            if (StringUtils.isNotBlank(cleanStr)) {
                String[] array = cleanStr.split("\\d.|;|≈");//делим строку по разделителям
                List<String> result = new ArrayList<>();
                for (String word : array) {
                    String cleanWord = word.trim();
                    int index = cleanWord.indexOf("[a-zA-Z]");
                    if (index != -1) {
                        cleanWord = cleanWord.substring(index);
                        if (StringUtils.isNotBlank(cleanWord)) {
                            result.add(cleanWord);
                        }
                    }
                }
                return result;
            }
        }
        return null;
    }
}
