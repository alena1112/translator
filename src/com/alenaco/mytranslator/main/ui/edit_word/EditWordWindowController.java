package com.alenaco.mytranslator.main.ui.edit_word;

import com.alenaco.mytranslator.main.controller.storages.Storage;
import com.alenaco.mytranslator.main.controller.translator.Translator;
import com.alenaco.mytranslator.main.controller.utils.SettingsHelper;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

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
        translationTextField.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.ENTER)) {
                handleSubmitButtonAction(null);
            }
        });
    }

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        String text = translationTextField.getText();
        if (StringUtils.isNotBlank(text)) {
            word.setChars(text);
            context.getStorage().getCash().addNewTranslation(word);
        }
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
