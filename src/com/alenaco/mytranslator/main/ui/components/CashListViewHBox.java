package com.alenaco.mytranslator.main.ui.components;

import com.alenaco.mytranslator.main.controller.managers.SessionManager;
import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.UIApp;
import com.alenaco.mytranslator.main.ui.edit_word.EditWordWindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
public class CashListViewHBox extends HBox {
    private SessionManager sessionManager;

    //todo search count info
    private Stage primaryStage;
    private WordButton wordBtn;
    private FlowPane leftPane;
    private FlowPane rightPane;

    public CashListViewHBox(Word word, SessionManager sessionManager, Stage primaryStage) {
        super();
        this.sessionManager = sessionManager;
        this.primaryStage = primaryStage;

        initHBox(word);
    }

    private void initHBox(Word word) {
        //todo переделать логику определения размеров компонентов

        leftPane = new FlowPane();
        leftPane.setHgap(2);
        leftPane.setPrefWidth(304);

        rightPane = new FlowPane();
        rightPane.setHgap(3);
        rightPane.setPrefWidth(35);

        BorderPane root = new BorderPane();
        root.setLeft(leftPane);
        root.setRight(rightPane);

        createBaseWordButton(word);

        for (UUID id : word.getTranslations()) {
            Word translation = sessionManager.getCashManager().findWordById(id);
            if (translation != null) {
                createTranslationTextButton(translation);
            }
        }

        createAddButton();
        createDeleteButton();

        this.getChildren().add(root);
    }

    public void updateLabelText() {

    }

    public WordButton getBaseWordBtn() {
        return wordBtn;
    }

    public WordButton getWordButton(Word word) {
        for (Node node : leftPane.getChildren()) {
            if (node instanceof WordButton) {
                WordButton btn = (WordButton) node;
                if (btn.getWord().equals(word)) {
                    return btn;
                }
            }
        }
        return null;
    }

    public void deleteWordButton(WordButton btn) {
        leftPane.getChildren().remove(btn);
    }

    public boolean isListViewEmpty() {
        return leftPane.getChildren().isEmpty();
    }

    public boolean isEmpty() {
        return leftPane.getChildren().isEmpty();
    }

    private void createBaseWordButton(Word word) {
        wordBtn = new WordButton(word, WordButton.WordButtonType.WORD);
        wordBtn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                mouseEvent -> {
                    if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                        showEditWordWindow(word);
                        wordBtn.setText(word.getChars());
                    }
                });
        leftPane.getChildren().add(wordBtn);
    }

    public void createTranslationTextButton(Word translation) {
        if (StringUtils.isNotBlank(translation.getChars())) {
            WordButton btn = new WordButton(translation, WordButton.WordButtonType.TRANSLATION);
            btn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            showEditWordWindow(translation);
                            btn.setText(translation.getChars());
                        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            sessionManager.getCashManager().removeWords(Collections.singletonList(translation));
                            leftPane.getChildren().remove(btn);
                        }
                    });
            leftPane.getChildren().add(btn);
        }
    }

    private void createAddButton() {
        Image addIcon = new Image(getClass().getClassLoader().getResourceAsStream(UIApp.ADD_ICON));
        ImageView addBtn = new ImageView(addIcon);
        rightPane.getChildren().add(addBtn);
        addBtn.setOnMouseClicked(event -> {
            Word newWord = new Word();
            newWord.setLanguage(wordBtn.getWord().getLanguage() == Language.EN ? Language.RU : Language.EN);
            newWord.getTranslations().add(wordBtn.getWord().getId());
            showEditWordWindow(newWord);
            createTranslationTextButton(newWord);
        });
    }

    private void createDeleteButton() {
        Image removeIcon = new Image(getClass().getClassLoader().getResourceAsStream(UIApp.REMOVE_ICON));
        ImageView deleteBtn = new ImageView(removeIcon);
        rightPane.getChildren().add(deleteBtn);
        deleteBtn.setOnMouseClicked(event -> {
            List<Word> toDelete = new ArrayList<>();
            for (Node node : leftPane.getChildren()) {
                if (node instanceof WordButton) {
                    WordButton btn = (WordButton) node;
                    toDelete.add(btn.getWord());
                }
            }
            sessionManager.getCashManager().removeWords(toDelete);
        });
    }

    private void showEditWordWindow(Word word) {
        try {
            FXMLLoader loader = new FXMLLoader(UIApp.class.getResource("edit_word/edit-word.fxml"));
            loader.setController(new EditWordWindowController(sessionManager, word));
            Parent root = loader.load();
            Stage dialog = new Stage();
            dialog.setScene(new Scene(root, 300, 100));
            dialog.setTitle("Edit word");
            dialog.getIcons().add(new Image(UIApp.MAIN_ICON));
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            UIApp.showErrorDialog("Application Error!", e.getMessage());
        }
    }
}
