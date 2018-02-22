package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.storages.JSONStorage;
import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//добавить логирование

public class UIApp extends Application {
    private Storage<Cash> storage;
    private Translator translator;

    private MenuBar menuBar;
    private Button translateBtn;
    private TextArea oneLangArea;
    private TextArea anotherLangArea;
    private ListView<ListViewHBox> cashView;
    private ObservableList<ListViewHBox> previousWordsList;

    public static final String ADD_ICON = "resources/icons/add.png";
    public static final String REMOVE_ICON = "resources/icons/remove.png";
    public static final String EDIT_ICON = "resources/icons/edit.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        prepareTranslator();

        createMenu();
        createTranslateButton(400, 10);
        createTranslationAreas(400, 150);
        createCashList(300, 300);

        VBox translationVBox = new VBox(menuBar, oneLangArea, translateBtn, anotherLangArea);
        HBox cashHBox = new HBox(translationVBox, cashView);
        VBox menuVBox = new VBox(menuBar, cashHBox);

        StackPane root = new StackPane();
        root.getChildren().add(menuVBox);

        primaryStage.setTitle("My Translator");
        primaryStage.setScene(new Scene(root, 700, 320));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void createTranslateButton(int width, int height) {
        translateBtn = new Button();
        translateBtn.setText("Translate");
        translateBtn.setPrefSize(width, height);
        translateBtn.setOnAction(event -> {
            translateWord();
        });
    }

    private void createTranslationAreas(int width, int height) {
        oneLangArea = new TextArea();
        oneLangArea.setPrefSize(width, height);
        oneLangArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                translateWord();
            }
        });

        anotherLangArea = new TextArea();
        anotherLangArea.setPrefSize(width, height);
        anotherLangArea.setEditable(false);
    }

    private void createCashList(int width, int height) {
        cashView = new ListView<>();
        cashView.setPrefSize(width, height);
        previousWordsList = FXCollections.observableArrayList();
        List<Word> words = new ArrayList<>(storage.getObject().getWords());
        if (CollectionUtils.isNotEmpty(words)) {
            words.sort((o1, o2) -> ObjectUtils.compare(o1.getLastSearchDate(), o2.getLastSearchDate()));//убрать
            for (Word word : words) {
                if (word.getSearchCount() != 0) {
                    previousWordsList.add(0, new ListViewHBox(word));
                }
            }
        }
        cashView.setItems(previousWordsList);
    }

    private void createMenu() {
        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        Menu train = new Menu("Train");
        menuBar.getMenus().addAll(menu, train);
    }

    private void prepareTranslator() {
        translator = new YandexTranslator();
        try {
            storage = new JSONStorage<>(new Cash());
            storage.restoreObject();
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        storage.saveObject();
    }

    private void translateWord() {
        String clientInput = oneLangArea.getText();
        if (StringUtils.isNotBlank(clientInput)) {
            Language fromLang = LanguageUtils.getLanguage(clientInput);
            Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
            Word word = storage.getObject().getTranslation(clientInput);
            if (word == null) {
                TranslatorResult result = translator.getTranslation(clientInput, fromLang, toLang);
                anotherLangArea.setText(result.getText());
                Word newWord = storage.getObject().put(clientInput, result.getText(), fromLang);
                previousWordsList.add(0, new ListViewHBox(newWord));
            } else {
                anotherLangArea.setText(word.getTranslationsStr(storage.getObject()));
                ListViewHBox foundItem = null;
                for (ListViewHBox item : previousWordsList) {
                    if (item.getWord().equals(word)) {
                        item.updateLabelText();
                        foundItem = item;
                        break;
                    }
                }
                if (foundItem != null) {
                    previousWordsList.remove(foundItem);
                    previousWordsList.add(0, foundItem);
                }
            }
        }
    }

    private class ListViewHBox extends HBox {
        private Label label;
        private Button addBtn;
        private Button editBtn;
        private Button deleteBtn;

        private Word word;

        private static final String CASH_FORMAT = "(%s) %s -> %s";

        ListViewHBox(Word word) {
            super();
            this.word = word;

            label = new Label();
            updateLabelText();
            label.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(label, Priority.ALWAYS);

            Image addIcon = new Image(getClass().getClassLoader().getResourceAsStream(ADD_ICON));
            addBtn = new Button("", new ImageView(addIcon));

            Image editIcon = new Image(getClass().getClassLoader().getResourceAsStream(EDIT_ICON));
            editBtn = new Button("", new ImageView(editIcon));

            Image removeIcon = new Image(getClass().getClassLoader().getResourceAsStream(REMOVE_ICON));
            deleteBtn = new Button("", new ImageView(removeIcon));
            deleteBtn.setOnAction(event -> {
                storage.getObject().removeWords(word);
                for (UUID id : word.getTranslations()) {
                    storage.getObject().removeWords(storage.getObject().findWordById(id));
                }
                previousWordsList.remove(this);
            });

            this.getChildren().addAll(label, addBtn, editBtn, deleteBtn);
        }

        public Word getWord() {
            return word;
        }

        public void updateLabelText() {
            label.setText(String.format(CASH_FORMAT, word.getSearchCount(), word.getChars(),
                    word.getTranslationsStr(storage.getObject())));
        }
    }
}
