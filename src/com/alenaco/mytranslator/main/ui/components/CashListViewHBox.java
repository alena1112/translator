package com.alenaco.mytranslator.main.ui.components;

import com.alenaco.mytranslator.main.model.Language;
import com.alenaco.mytranslator.main.model.SessionContext;
import com.alenaco.mytranslator.main.model.Word;
import com.alenaco.mytranslator.main.ui.UIApp;
import com.alenaco.mytranslator.main.ui.edit_word.EditWordWindowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import java.util.UUID;

/**
 * @author kovalenko
 * @version $Id$
 */
public class CashListViewHBox extends HBox {
    private SessionContext sessionContext;
    private Stage primaryStage;
    private ObservableList<CashListViewHBox> previousWordsList;

    //todo search count info
    private WordButton wordBtn;
    private ImageView addBtn;
    private ImageView deleteBtn;

    private Word word;

    public CashListViewHBox(Word word, SessionContext sessionContext, Stage primaryStage, ObservableList<CashListViewHBox> previousWordsList) {
        super();
        this.word = word;
        this.sessionContext = sessionContext;
        this.primaryStage = primaryStage;
        this.previousWordsList = previousWordsList;

        //todo переделать логику определения размеров компонентов
        BorderPane root = new BorderPane();
        FlowPane leftPane = new FlowPane();
        leftPane.setHgap(2);
        FlowPane rightPane = new FlowPane();
        rightPane.setHgap(3);
        rightPane.setPrefWidth(35);
        root.setLeft(leftPane);
        root.setRight(rightPane);

        leftPane.setPrefWidth(304);

        wordBtn = new WordButton(word, WordButton.WordButtonType.WORD);
        leftPane.getChildren().add(wordBtn);

        for (Word translation : word.getTranslations(sessionContext.getStorage().getCash())) {
            createTranslationTextButton(translation, leftPane);
        }

        Image addIcon = new Image(getClass().getClassLoader().getResourceAsStream(UIApp.ADD_ICON));
        addBtn = new ImageView(addIcon);
        rightPane.getChildren().add(addBtn);
        addBtn.setOnMouseClicked(event -> {
            Word newWord = new Word();
            newWord.setLanguage(word.getLanguage() == Language.EN ? Language.RU : Language.EN);
            newWord.getTranslations().add(word.getId());
            showEditWordWindow(newWord);
            createTranslationTextButton(newWord, leftPane);
        });

        Image removeIcon = new Image(getClass().getClassLoader().getResourceAsStream(UIApp.REMOVE_ICON));
        deleteBtn = new ImageView(removeIcon);
        rightPane.getChildren().add(deleteBtn);
        deleteBtn.setOnMouseClicked(event -> {
            sessionContext.getStorage().getCash().removeWords(word);
            for (UUID id : word.getTranslations()) {
                sessionContext.getStorage().getCash().removeWords(sessionContext.getStorage().getCash().findWordById(id));
            }
            previousWordsList.remove(this);
        });

        this.getChildren().add(root);
    }

    public Word getWord() {
        return word;
    }

    public void updateLabelText() {

    }

    private void createTranslationTextButton(Word translation, FlowPane flowPane) {
        if (StringUtils.isNotBlank(translation.getChars())) {
            WordButton btn = new WordButton(translation, WordButton.WordButtonType.TRANSLATION);
            btn.addEventHandler(MouseEvent.MOUSE_CLICKED,
                    mouseEvent -> {
                        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                            showEditWordWindow(translation);
                            btn.setText(translation.getChars());
                        } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            sessionContext.getStorage().getCash().removeWords(translation);
                            flowPane.getChildren().remove(btn);
                        }
                    });
            flowPane.getChildren().add(btn);
        }
    }

    private void showEditWordWindow(Word word) {
        try {
            FXMLLoader loader = new FXMLLoader(UIApp.class.getResource("edit_word/edit-word.fxml"));
            loader.setController(new EditWordWindowController(sessionContext, word));
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
