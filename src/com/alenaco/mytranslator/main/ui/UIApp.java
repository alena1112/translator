package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.managers.CashManager;
import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.components.CashListViewHBox;
import com.alenaco.mytranslator.main.ui.components.WordButton;
import com.alenaco.mytranslator.main.ui.settings_window.SettingsWindowController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

//добавить логирование

public class UIApp extends Application {
    private SessionManager sessionManager;

    private Stage primaryStage;

    private MenuBar menuBar;

    private Button translateBtn;
    private TextArea oneLangArea;
    private TextArea anotherLangArea;

    private ListView<CashListViewHBox> cashView;
    private ObservableList<CashListViewHBox> previousWordsList;

    public static final String ADD_ICON = "resources/icons/add.png";
    public static final String REMOVE_ICON = "resources/icons/remove.png";
    public static final String EDIT_ICON = "resources/icons/edit.png";
    public static final String MAIN_ICON = "resources/icons/main.jpg";
    public static final String GARBAGE_ICON = "resources/icons/garbage.png";
    public static final String SAVE_ICON = "resources/icons/save.png";

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        prepareTranslator();

        createMenu(300, 150);
        createTranslateButton(400, 10);
        createTranslationAreas(400, 150);
        createCashList(400, 300);

        FlowPane langAreasHelpPane = new FlowPane(Orientation.VERTICAL);
        langAreasHelpPane.setVgap(2);
        createCleanLanguageAreasBtn(0, langAreasHelpPane);

        FlowPane cashHelpPane = new FlowPane(Orientation.VERTICAL);
        cashHelpPane.setVgap(2);
        createSaveCashBtn(0, cashHelpPane);

        VBox translationVBox = new VBox(menuBar, oneLangArea, translateBtn, anotherLangArea);
        HBox cashHBox = new HBox(langAreasHelpPane, translationVBox, cashHelpPane, cashView);
        VBox menuVBox = new VBox(menuBar, cashHBox);

        StackPane root = new StackPane();
        root.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        root.getChildren().add(menuVBox);

        primaryStage.setTitle("My Translator");
        primaryStage.getIcons().add(new Image(MAIN_ICON));
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
        List<Word> words = sessionManager.getCashManager().getWords();
        if (CollectionUtils.isNotEmpty(words)) {
            words.sort((o1, o2) -> ObjectUtils.compare(o1.getLastSearchDate(), o2.getLastSearchDate()));//убрать
            for (Word word : words) {
                previousWordsList.add(0, new CashListViewHBox(word, sessionManager, primaryStage));
            }
        }
        cashView.setItems(previousWordsList);
    }

    private void createMenu(int width, int height) {
        menuBar = new MenuBar();
        Menu menu = new Menu("Menu");
        Menu train = new Menu("Train");
        Menu tools = new Menu("Tools");

        MenuItem settings = new MenuItem("Settings");
        initSettingsDialogWindow(settings, width, height);
        tools.getItems().add(settings);

        menuBar.getMenus().addAll(menu, train, tools);
    }

    private void initSettingsDialogWindow(MenuItem settings, int width, int height) {
        settings.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("settings_window/settings-window.fxml"));
                loader.setController(new SettingsWindowController(sessionManager));
                Parent root = loader.load();
                Stage dialog = new Stage();
                dialog.setScene(new Scene(root, width, height));
                dialog.setTitle("Settings");
                dialog.getIcons().add(new Image(MAIN_ICON));
                dialog.initOwner(primaryStage);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.showAndWait();
            } catch (IOException e) {
                e.printStackTrace();
                showErrorDialog("Application Error!", e.getMessage());
            }
        });
    }

    private void prepareTranslator() throws IllegalAccessException {
        try {
            sessionManager = new SessionManager();
            sessionManager.getCashManager().addCashChangedListener(new UIAppCashChangedListener());
        } catch (StorageException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            showErrorDialog("Application Error!", e.getMessage());
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    private void translateWord() {
        String clientInput = oneLangArea.getText();
        try {
            Word word = sessionManager.translateWord(clientInput);
            anotherLangArea.setText(word.getChars());
        } catch (UnsupportedOperationException exc) {
            showErrorDialog(null, String.format("%s is not supported!",
                    SettingsHelper.getClassName(sessionManager.getSessionContext().getTranslator())));
        }
    }

    public static void showErrorDialog(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Dialog");
        if (StringUtils.isNotBlank(headerText)) {
            alert.setHeaderText(headerText);
        }
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    private void createSaveCashBtn(int position, FlowPane cashHelpPane) {
        Image saveImg = new Image(getClass().getClassLoader().getResourceAsStream(SAVE_ICON));
        Button saveBtn = new Button("", new ImageView(saveImg));
        saveBtn.setOnAction(event -> {
            try {
                sessionManager.saveCash();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Application Error!", e.getMessage());
            }
        });
        cashHelpPane.getChildren().add(position, saveBtn);
    }

    private void createCleanLanguageAreasBtn(int position, FlowPane langHelpPane) {
        Image garbageImg = new Image(getClass().getClassLoader().getResourceAsStream(GARBAGE_ICON));
        Button garbageBtn = new Button("", new ImageView(garbageImg));
        garbageBtn.setOnAction(event -> {
            oneLangArea.clear();
            anotherLangArea.clear();
        });
        langHelpPane.getChildren().add(position, garbageBtn);
    }

    private class UIAppCashChangedListener implements CashManager.CashChangedListener {

        @Override
        public void cashChanged(Word word, CashManager.CashChangingType changingType) {
            switch (changingType) {
                case ADD:
                    previousWordsList.add(0, new CashListViewHBox(word, sessionManager, primaryStage));
                    break;
                case CHANGE_CHARS:
                    List<WordButton> wordButtons = findWordButtons(word);
                    for (WordButton wB : wordButtons) {
                        wB.setText(word.getChars());
                    }
                    break;
                case CHANGE_COUNT:
                    break;
                case ADD_TRANSLATION:

                    break;
                case DELETE:
                    deleteWordButtons(word);
                    break;
            }
        }
    }

    private List<WordButton> findWordButtons(Word word) {
        List<WordButton> foundItems = new ArrayList<>();
        for (CashListViewHBox item : previousWordsList) {
            WordButton wB = item.getWordButton(word);
            if (wB != null) {
                foundItems.add(wB);
            }
        }
        return foundItems;
    }

    private void deleteWordButtons(Word word) {
        List<CashListViewHBox> toDelete = new ArrayList<>();
        for (CashListViewHBox item : previousWordsList) {
            WordButton wB = item.getWordButton(word);
            if (wB != null) {
                item.getChildren().remove(wB);
            }
            if (item.isEmpty()) {
                toDelete.add(item);
            }
        }
        previousWordsList.removeAll(toDelete);
    }
}
