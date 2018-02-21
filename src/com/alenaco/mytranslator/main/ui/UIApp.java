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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;

public class UIApp extends Application {
    private Storage<Cash> storage;
    private Translator translator;

    private Button translateBtn;
    private TextArea oneLangArea;
    private TextArea anotherLangArea;
    private ListView<String> cashView;
    private ObservableList<String> previousWordsList;

    private static final String CASH_FORMAT = "(%s) %s -> %s";

    @Override
    public void start(Stage primaryStage) throws Exception {
        prepareTranslator();

        createTranslateButton(400, 10);
        createTranslationAreas(400, 150);
        createCashList(200, 300);

        VBox vbox = new VBox(oneLangArea, translateBtn, anotherLangArea);
        HBox hBox = new HBox(vbox, cashView);

        StackPane root = new StackPane();
        root.getChildren().add(hBox);

        primaryStage.setTitle("My Translator");
        primaryStage.setScene(new Scene(root, 600, 310));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Button createTranslateButton(int width, int height) {
        translateBtn = new Button();
        translateBtn.setText("Translate");
        translateBtn.setPrefSize(width, height);
        translateBtn.setOnAction(event -> {
            String clientInput = oneLangArea.getText();
            Language fromLang = LanguageUtils.getLanguage(clientInput);
            Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
            Word word = storage.getObject().getTranslation(clientInput);
            if (word == null) {
                TranslatorResult result = translator.getTranslation(clientInput, fromLang, toLang);
                anotherLangArea.setText(result.getText());
                storage.getObject().put(clientInput, result.getText(), fromLang);
                previousWordsList.add(0, String.format(CASH_FORMAT, 1, clientInput, result.getText()));
            } else {
                anotherLangArea.setText(word.getTranslationsStr());
                for (int i = 0; i < previousWordsList.size(); i++) {
                    String item = previousWordsList.get(i);
                    int count = storage.getObject().getStatistic(word);
                    if (item.equals(String.format(CASH_FORMAT, count - 1, clientInput, word.getTranslationsStr()))) {
                        previousWordsList.remove(i);
                        previousWordsList.add(0, String.format(CASH_FORMAT, count,
                                clientInput, word.getTranslationsStr()));
                        break;
                    }
                }
            }
        });
        return translateBtn;
    }

    private void createTranslationAreas(int width, int height) {
        oneLangArea = new TextArea();
        oneLangArea.setPrefSize(width, height);

        anotherLangArea = new TextArea();
        anotherLangArea.setPrefSize(width, height);
        anotherLangArea.setEditable(false);
    }

    private void createCashList(int width, int height) {
        cashView = new ListView<>();
        cashView.setPrefSize(width, height);
        previousWordsList = FXCollections.observableArrayList();
        if (CollectionUtils.isNotEmpty(storage.getObject().getWords())) {
            for (Word word : storage.getObject().getWords()) {
                previousWordsList.add(0, String.format(CASH_FORMAT, 0,
                        word.getChars(), word.getTranslationsStr()));
            }
        }
        cashView.setItems(previousWordsList);
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
}
