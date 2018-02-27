package com.alenaco.mytranslator.main.ui.settings_window;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.SessionContext;
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
    private SessionContext context;

    public SettingsWindowController(SessionContext context) {
        this.context = context;
    }

    @FXML
    public void initialize() {
        try {
            translators = SettingsHelper.getSettingsClasses(Translator.class);

            ObservableList<String> items = translatorTypesComboBox.getItems();
            items.addAll(translators.values());

            if (context.getTranslator() != null) {
                for (Class clazz : translators.keySet()) {
                    if (context.getTranslator().getClass().equals(clazz)) {
                        translatorTypesComboBox.setValue(translators.get(clazz));
                        break;
                    }
                }
            }

            storages = SettingsHelper.getSettingsClasses(Storage.class);

            items = storageTypesComboBox.getItems();
            items.addAll(storages.values());

            if (context.getStorage() != null) {
                for (Class clazz : storages.keySet()) {
                    if (context.getStorage().getClass().equals(clazz)) {
                        storageTypesComboBox.setValue(storages.get(clazz));
                        break;
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            if (context.getTranslator() == null || !translators.get(context.getTranslator().getClass()).equals(translatorTypesComboBox.getValue())) {
                Class translatorClass = null;
                for (Map.Entry<Class, String> entry : translators.entrySet()) {
                    if (entry.getValue().equals(translatorTypesComboBox.getValue())) {
                        translatorClass = entry.getKey();
                        break;
                    }
                }
                if (translatorClass != null) {
                    Translator translator = (Translator) SettingsHelper.getSettingsInstance(translatorClass);
                    context.setTranslator(translator, translatorTypesComboBox.getValue());
                }
            }
            if (context.getStorage() == null || !storages.get(context.getStorage().getClass()).equals(storageTypesComboBox.getValue())) {
                Class storageClass = null;
                for (Map.Entry<Class, String> entry : storages.entrySet()) {
                    if (entry.getValue().equals(storageTypesComboBox.getValue())) {
                        storageClass = entry.getKey();
                        break;
                    }
                }
                if (storageClass != null) {
                    Storage storage = (Storage) SettingsHelper.getSettingsInstance(storageClass);
                    context.setStorage(storage, storageTypesComboBox.getValue());
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
