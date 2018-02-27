package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.storages.JSONStorage;
import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorResult;
import com.alenaco.mytranslator.main.controller.translator.yandex.YandexTranslator;
import com.alenaco.mytranslator.main.controller.utils.LanguageUtils;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.settings_window.SettingsWindowController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//добавить логирование

public class UIApp extends Application {
    private SessionContext sessionContext;

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

        createMenu(primaryStage, 300, 150);
        createTranslateButton(400, 10);
        createTranslationAreas(400, 150);
        createCashList(400, 300);

        VBox translationVBox = new VBox(menuBar, oneLangArea, translateBtn, anotherLangArea);
        HBox cashHBox = new HBox(translationVBox, cashView);
        VBox menuVBox = new VBox(menuBar, cashHBox);

        StackPane root = new StackPane();
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        root.getChildren().add(menuVBox);

        primaryStage.setTitle("My Translator");
        primaryStage.setScene(new Scene(root, 800, 320));
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
        List<Word> words = new ArrayList<>(sessionContext.getStorage().getCash().getWords());
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

    private void createMenu(Stage primaryStage, int width, int height) {
        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        Menu train = new Menu("Train");
        Menu tools = new Menu("Tools");

        MenuItem settings = new MenuItem("Settings");
        initSettingsDialogWindow(settings, primaryStage, width, height);
        tools.getItems().add(settings);

        menuBar.getMenus().addAll(menu, train, tools);
    }

    private void initSettingsDialogWindow(MenuItem settings, Stage parentStage, int width, int height) {
        settings.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("settings_window/settings-window.fxml"));
                loader.setController(new SettingsWindowController(sessionContext));
                Parent root = loader.load();
                Stage dialog = new Stage();
                dialog.setScene(new Scene(root, width, height));
                dialog.setTitle("Settings");
                dialog.initOwner(parentStage);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void prepareTranslator() {
        sessionContext = new SessionContext();
        Translator translator = new YandexTranslator();
        sessionContext.setTranslator(translator, translator.getInstanceName());
        try {
            Storage storage = new JSONStorage();
            storage.restoreCash();
            sessionContext.setStorage(storage, storage.getInstanceName());
        } catch (StorageException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        sessionContext.getStorage().saveCash();
    }

    private void translateWord() {
        String clientInput = oneLangArea.getText();
        if (StringUtils.isNotBlank(clientInput)) {
            Language fromLang = LanguageUtils.getLanguage(clientInput);
            Language toLang = fromLang == Language.RU ? Language.EN : Language.RU;
            Word word = sessionContext.getStorage().getCash().getTranslation(clientInput);
            if (word == null) {
                TranslatorResult result = null;
                try {
                    result = sessionContext.getTranslator().getTranslation(clientInput, fromLang, toLang);
                } catch (UnsupportedOperationException exc) {
                    showErrorDialog(null, String.format("%s is not supported!", sessionContext.getTranslatorName()));
                    return;
                }
                anotherLangArea.setText(result.getText());
                Word newWord = sessionContext.getStorage().getCash().put(clientInput, result.getText(), fromLang);
                previousWordsList.add(0, new ListViewHBox(newWord));
            } else {
                anotherLangArea.setText(word.getTranslationsStr(sessionContext.getStorage().getCash()));
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
        //todo search count info
        private Button wordBtn;
        private List<Button> translationBtns = new ArrayList<>();
        private Button addBtn;
        private Button editBtn;
        private Button deleteBtn;
        private int mainWidth;

        private Word word;

        ListViewHBox(Word word) {
            super();
            this.word = word;
            this.mainWidth = (int) cashView.getPrefWidth();

            wordBtn = new Button(word.getChars());
            wordBtn.setId("wordBtn");
            wordBtn.setPrefWidth(150);
            this.getChildren().add(wordBtn);

            for (Word translation : word.getTranslations(sessionContext.getStorage().getCash())) {
                Button btn = new Button(translation.getChars());
                btn.setId("translationBtn");
                btn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                        mouseEvent -> {
                            if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                                //todo edit dialog
                            } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                                sessionContext.getStorage().getCash().removeWords(translation);
                                translationBtns.remove(btn);
                                this.getChildren().remove(btn);
                            }
                        });
                translationBtns.add(btn);
            }
            this.getChildren().addAll(translationBtns);

            Image addIcon = new Image(getClass().getClassLoader().getResourceAsStream(ADD_ICON));
            addBtn = new Button("", new ImageView(addIcon));
            addBtn.setPrefWidth(10);

            Image editIcon = new Image(getClass().getClassLoader().getResourceAsStream(EDIT_ICON));
            editBtn = new Button("", new ImageView(editIcon));
            editBtn.setPrefWidth(10);

            Image removeIcon = new Image(getClass().getClassLoader().getResourceAsStream(REMOVE_ICON));
            deleteBtn = new Button("", new ImageView(removeIcon));
            deleteBtn.setOnAction(event -> {
                sessionContext.getStorage().getCash().removeWords(word);
                for (UUID id : word.getTranslations()) {
                    sessionContext.getStorage().getCash().removeWords(sessionContext.getStorage().getCash().findWordById(id));
                }
                previousWordsList.remove(this);
            });
            deleteBtn.setPrefWidth(10);

            int buttonSize = (int) ((this.mainWidth - addBtn.getWidth() - editBtn.getWidth() - deleteBtn.getWidth() - wordBtn.getPrefWidth()) / translationBtns.size());
            for (Button button : translationBtns) {
                button.setPrefWidth(buttonSize);
            }

            this.getChildren().addAll(addBtn, editBtn, deleteBtn);
        }

        public Word getWord() {
            return word;
        }

        public void updateLabelText() {

        }
    }

    private void showErrorDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        if (StringUtils.isNotBlank(headerText)) {
            alert.setHeaderText(headerText);
        }
        alert.setContentText(contentText);

        alert.showAndWait();
    }
}
