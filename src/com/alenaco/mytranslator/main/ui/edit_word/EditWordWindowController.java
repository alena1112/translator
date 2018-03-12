package com.alenaco.mytranslator.main.ui.edit_word;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

public class EditWordWindowController {
    @FXML
    private TextArea translationTextField;
    @FXML
    private Button submitButton;
    @FXML
    private GridPane gridPane;

    private SessionManager manager;
    private Word word;

    public EditWordWindowController(SessionManager manager, Word word) {
        this.manager = manager;
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
            manager.getCashManager().addNewTranslation(word);
        }
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }
}
