package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.HttpResult;
import com.alenaco.mytranslator.main.controller.HttpSender;
import com.alenaco.mytranslator.main.controller.LanguageUtils;
import com.alenaco.mytranslator.main.model.Cash;
import com.alenaco.mytranslator.main.model.LanguageDirection;
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

public class UIApp extends Application {
    private Cash cash;

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

        VBox vbox = new VBox(oneLangArea, anotherLangArea, translateBtn);
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
            LanguageDirection direction = LanguageUtils.getLanguageDirection(clientInput);
            Word word = cash.getTranslation(clientInput);
            if (word == null) {
                HttpResult result = HttpSender.sendRequest(clientInput, direction);
                anotherLangArea.setText(result.getText());
                cash.put(clientInput, result.getText(), direction);
                previousWordsList.add(0, String.format(CASH_FORMAT, 1, clientInput, result.getText()));
            } else {
                anotherLangArea.setText(word.getTranslationsStr());
                for (int i = 0; i < previousWordsList.size(); i++) {
                    String item = previousWordsList.get(i);
                    int count = cash.getStatistic(word);
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
        cashView.setItems(previousWordsList);
    }

    private void prepareTranslator() {
        cash = new Cash();
    }
}
