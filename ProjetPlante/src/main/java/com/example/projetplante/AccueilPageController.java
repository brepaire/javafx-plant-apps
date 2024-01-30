package com.example.projetplante;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;

public class AccueilPageController {

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    public void initialize() {
        Image backgroundImage = new Image(getClass().getResource("/bankImage/fondAccueil.png").toString());
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT
        );
        mainAnchorPane.setBackground(new Background(background));
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        loadNavigationPage();
    }

    private void loadNavigationPage() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/home-page.fxml"));
            AnchorPane pane = fxmlLoader.load();
            mainAnchorPane.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
