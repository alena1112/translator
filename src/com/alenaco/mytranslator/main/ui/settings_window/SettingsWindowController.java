package com.alenaco.mytranslator.main.ui.settings_window;

import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.translator.TranslatorHelper;
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
    private Button submitButton;

    private Map<Class<Translator>, String> translators;
    private SessionContext context;

    public SettingsWindowController(SessionContext context) {
        this.context = context;
    }

    @FXML
    public void initialize() {
        try {
            translators = TranslatorHelper.getTranslators();

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

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        try {
            if (context.getTranslator() == null || !translators.get(context.getTranslator().getClass()).equals(translatorTypesComboBox.getValue())) {
                Class<Translator> translatorClass = null;
                for (Map.Entry<Class<Translator>, String> entry : translators.entrySet()) {
                    if (entry.getValue().equals(translatorTypesComboBox.getValue())) {
                        translatorClass = entry.getKey();
                        break;
                    }
                }
                if (translatorClass != null) {
                    Translator translator = TranslatorHelper.getTranslatorInstance(translatorClass);
                    context.setTranslator(translator, translatorTypesComboBox.getValue());
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
