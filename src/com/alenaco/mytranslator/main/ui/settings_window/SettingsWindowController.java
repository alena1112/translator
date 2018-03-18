package com.alenaco.mytranslator.main.ui.settings_window;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.storages.StorageException;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class SettingsWindowController {
    @FXML
    private ComboBox<String> translatorTypesComboBox;
    @FXML
    private ComboBox<String> storageTypesComboBox;
    @FXML
    private Button submitButton;

    private Map<Class, String> translators;
    private Map<Class, String> storages;
    private SessionManager sessionManager;

    public SettingsWindowController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @FXML
    public void initialize() {
        try {
            translators = SettingsHelper.getSettingsClasses(Translator.class);

            ObservableList<String> items = translatorTypesComboBox.getItems();
            items.addAll(translators.values());

            for (Class clazz : translators.keySet()) {
                if (sessionManager.getSessionContext().getTranslator().equals(clazz)) {
                    translatorTypesComboBox.setValue(translators.get(clazz));
                    break;
                }
            }
            storages = SettingsHelper.getSettingsClasses(Storage.class);

            items = storageTypesComboBox.getItems();
            items.addAll(storages.values());

            for (Class clazz : storages.keySet()) {
                if (sessionManager.getSessionContext().getStorage().equals(clazz)) {
                    storageTypesComboBox.setValue(storages.get(clazz));
                    break;
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        if (sessionManager.getSessionContext().getTranslator() == null ||
                !translators.get(sessionManager.getSessionContext().getTranslator()).equals(translatorTypesComboBox.getValue())) {
            Class translatorClass = null;
            for (Map.Entry<Class, String> entry : translators.entrySet()) {
                if (entry.getValue().equals(translatorTypesComboBox.getValue())) {
                    translatorClass = entry.getKey();
                    break;
                }
            }
            if (translatorClass != null) {
                sessionManager.getSessionContext().setTranslator(translatorClass);
            }
        }
        if (sessionManager.getSessionContext().getStorage() == null ||
                !storages.get(sessionManager.getSessionContext().getStorage()).equals(storageTypesComboBox.getValue())) {
            Class storageClass = null;
            for (Map.Entry<Class, String> entry : storages.entrySet()) {
                if (entry.getValue().equals(storageTypesComboBox.getValue())) {
                    storageClass = entry.getKey();
                    break;
                }
            }
            if (storageClass != null) {
                sessionManager.getSessionContext().setStorage(storageClass);
            }
        }
        try {
            sessionManager.restartSessionManager();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                IllegalAccessException | StorageException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
