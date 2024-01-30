package com.example.projetplante;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ParametrePageController {
    @FXML
    private AnchorPane parametreAnchorPane;

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            Stage primaryStage = (Stage) parametreAnchorPane.getScene().getWindow();
            Scene newScene = new Scene(newView, 335, 600);
            primaryStage.setScene(newScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleHomeClick(MouseEvent event) {
        loadPage("/home-page.fxml");
    }
    @FXML
    private void handlePlanteClick(MouseEvent event) {
        loadPage("/plante-page.fxml");
    }

    @FXML
    private void handleCalendrierClick(MouseEvent event) {
        loadPage("/calendrier-page.fxml");
    }

    @FXML
    private void handleEncyclopedieClick(MouseEvent event) {
        loadPage("/encyclopedie-page.fxml");
    }

    @FXML
    private void handleParametreClick(MouseEvent event) {
        loadPage("/parametre-page.fxml");
    }
}

