package com.alenaco.mytranslator.main.ui;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.components.CashListViewHBox;
import com.alenaco.mytranslator.main.ui.listeners.UIAppCashChangedListener;
import com.alenaco.mytranslator.main.ui.settings_window.SettingsWindowController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

//todo добавить логирование

public class UIApp extends Application {
    private SessionManager sessionManager;

    private Stage primaryStage;

    private MenuBar menuBar;

    private Button translateBtn;
    private TextArea oneLangArea;
    private TextArea anotherLangArea;

    private ListView<CashListViewHBox> cashView;
    private ObservableList<CashListViewHBox> previousWordsList;
    private List<CashListViewHBox> filteredWordsList = new ArrayList<>();

    private FilterEvent ruFilterEvent;
    private FilterEvent enFilterEvent;

    public static final String ADD_ICON = "resources/icons/add.png";
    public static final String REMOVE_ICON = "resources/icons/remove.png";
    public static final String EDIT_ICON = "resources/icons/edit.png";
    public static final String MAIN_ICON = "resources/icons/main.jpg";
    public static final String GARBAGE_ICON = "resources/icons/garbage.png";
    public static final String SAVE_ICON = "resources/icons/save.png";
    public static final String UK_ICON = "resources/icons/uk.png";
    public static final String RUSSIA_ICON = "resources/icons/russia.png";

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
        ruFilterEvent = createCashFilterBtn(1, cashHelpPane, Language.RU, RUSSIA_ICON);
        enFilterEvent = createCashFilterBtn(2, cashHelpPane, Language.EN, UK_ICON);

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
        List<Word> values = new ArrayList<>(sessionManager.getCashManager().getWords());
        if (CollectionUtils.isNotEmpty(values)) {
            values.sort((o1, o2) -> ObjectUtils.compare(o1.getLastSearchDate(), o2.getLastSearchDate()));
            for (Word word : values) {
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
            sessionManager.getCashManager().addCashChangedListener(new UIAppCashChangedListener(this, sessionManager));
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
            String translations = sessionManager.translateWord(clientInput);
            anotherLangArea.setText(translations);
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
                sessionManager.getCashManager().saveCash();
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Application Error!", e.getMessage());
            }
        });
        cashHelpPane.getChildren().add(position, saveBtn);
    }

    private FilterEvent createCashFilterBtn(int position, FlowPane cashHelpPane, Language language, String icon) {
        Image img = new Image(getClass().getClassLoader().getResourceAsStream(icon));
        Button btn = new Button("", new ImageView(img));
        FilterEvent filterEvent = new FilterEvent(btn, language);
        btn.setOnAction(filterEvent);
        cashHelpPane.getChildren().add(position, btn);
        return filterEvent;
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public List<CashListViewHBox> getPreviousWordsList() {
        List<CashListViewHBox> list = new ArrayList<>();
        list.addAll(previousWordsList);
        list.addAll(filteredWordsList);
        return list;
    }

    public void addListInPreviousWordsList(CashListViewHBox list) {
        Word word = list.getBaseWordBtn().getWord();
        if (word.getLanguage() == Language.EN) {
            if (ruFilterEvent.isActive()) {
                filteredWordsList.add(list);
            } else {
                previousWordsList.add(0, list);
            }
        } else if (word.getLanguage() == Language.RU) {
            if (enFilterEvent.isActive()) {
                filteredWordsList.add(list);
            } else {
                previousWordsList.add(0, list);
            }
        }
    }

    public void removeListsFromPreviousWordsList(List<CashListViewHBox> items) {
        previousWordsList.removeAll(items);
        filteredWordsList.removeAll(items);
    }

    private class FilterEvent implements EventHandler<ActionEvent> {
        private boolean active = false;
        private Button btn;
        private Language language;

        public FilterEvent(Button btn, Language language) {
            this.btn = btn;
            this.language = language;
        }

        @Override
        public void handle(ActionEvent event) {
            active = !active;
            if (!filteredWordsList.isEmpty()) {
                previousWordsList.addAll(filteredWordsList);
                filteredWordsList.clear();
                if (this.equals(ruFilterEvent) && enFilterEvent.isActive()) {
                    enFilterEvent.setActive(false);
                } else if (this.equals(enFilterEvent) && ruFilterEvent.isActive()) {
                    ruFilterEvent.setActive(false);
                }
            }
            if (active) {
                for (CashListViewHBox list : previousWordsList) {
                    if (list.getBaseWordBtn().getWord().getLanguage() != language) {
                        filteredWordsList.add(list);
                    }
                }
                previousWordsList.removeAll(filteredWordsList);
            }
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}
