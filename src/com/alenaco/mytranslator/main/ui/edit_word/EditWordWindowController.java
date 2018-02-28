package com.alenaco.mytranslator.main.ui.edit_word;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class EditWordWindowController {
    @FXML
    private TextField translationTextField;
    @FXML
    private Button submitButton;

    private SessionContext context;
    private Word word;

    public EditWordWindowController(SessionContext context, Word word) {
        this.context = context;
        this.word = word;
    }

    @FXML
    public void initialize() {
        if (word.getChars() != null) {
            translationTextField.setText(word.getChars());
        }
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        word.setChars(translationTextField.getText());
        context.getStorage().getCash().getWords().add(word);
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
