package com.example.projetplante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.IOException;
import java.util.List;

public class EncyclopediePageController {
    @FXML
    private AnchorPane encyclopedieAnchorPane;
    @FXML
    private GridPane encyclopedieGridPane;

    private int totalPlantes;
    private int currentPage = 0;
    private int plantesPerPage = 6;
    @FXML
    private Pane swipeRight;
    @FXML
    private Pane swipeLeft;
    int pageIndex = 0;
    int rowIndex = 0;
    int columnIndex = 0;
    @FXML
    private HBox paginationHBox;

    public EncyclopediePageController(){}



    @FXML
    public void initialize() {
        List<Plante> plantes = chargerPlantesDepuisJson();
        totalPlantes = plantes.size();

        int totalPages = (int) Math.ceil((double) totalPlantes / plantesPerPage);
        createPaginationIndicators(totalPages);
        updatePaginationIndicators(currentPage);

        for (int i = currentPage * plantesPerPage; i < (currentPage + 1) * plantesPerPage && i < plantes.size(); i++) {
            Plante plante = plantes.get(i);
            encyclopedieGridPane.add(creerPlantePane(plante), columnIndex, rowIndex);

            columnIndex++;

            if (columnIndex > 1) {
                columnIndex = 0;
                rowIndex++;
            }
        }
        encyclopedieGridPane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
    }

    private List<Plante> chargerPlantesDepuisJson() {
        List<Plante> plantes = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getResourceAsStream("/Encyclopedie.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            String jsonString = builder.toString();

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Plante plante = new Plante();
                plante.setNom(jsonObject.getString("Nom"));
                plante.setSaison(jsonObject.getString("Saison"));
                plante.setVivace(jsonObject.getString("Vivace"));
                plante.setPlantationDe(jsonObject.getString("PlantationDe"));
                plante.setPlantationA(jsonObject.getString("PlantationA"));
                plante.setImageURL(jsonObject.getString("imageUrl"));
                plante.setIntervalleArrosage(jsonObject.getString("IntervalleArrosage"));
                plante.setUniteArrosage(jsonObject.getString("IntervalleArrosage"));
                plante.setDureePousse(jsonObject.getString("DureePousse"));
                plante.setUnitePousse(jsonObject.getString("DureePousse"));

                plantes.add(plante);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return plantes;
    }

    private Pane creerPlantePane(Plante plante) {
        Pane pane = new Pane();
        pane.setPrefHeight(210.0);
        pane.setPrefWidth(150.0);

        Text nom = new Text();
        Text saison = new Text();
        Text vivace = new Text();
        Text dureePousse = new Text();
        Text finalPlantation = new Text();
        Text intervalleArrosage = new Text();

        nom.setText(plante.getNom());
        nom.setTextAlignment(TextAlignment.CENTER);
        nom.setLayoutX((pane.getPrefWidth() - nom.getBoundsInLocal().getWidth()) / 2.0);
        nom.setLayoutY(20.0);

        Image image = null;
        String imageUrl = plante.getImageURL();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            image = new Image(imageUrl);
        } else if (imageUrl != null && imageUrl.isEmpty()) {
            image = new Image("src/main/resources/bankImage/NOT_FOUND.png");
        }
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX(20.0);
        imageView.setLayoutY(25.0);
        imageView.setFitHeight(100.0);
        imageView.setFitWidth(115.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        pane.getChildren().add(nom);
        pane.getChildren().add(saison);
        pane.getChildren().add(vivace);
        pane.getChildren().add(dureePousse);
        pane.getChildren().add(finalPlantation);
        pane.getChildren().add(intervalleArrosage);
        pane.getChildren().add(imageView);
        pane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
        pane.setOnMouseClicked(event -> openPlanteDetailsWindow(plante));

        return pane;
    }

    private void openPlanteDetailsWindow(Plante plante) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/encyclopedie-page-detail.fxml"));
            Parent root = fxmlLoader.load();
            EncyclopediePageDetailController controller = fxmlLoader.getController();
            controller.setPlante(plante);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Détails de la plante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void updateEncyclopedieGridPane() {
        encyclopedieGridPane.getChildren().clear();
        List<Plante> plantes = chargerPlantesDepuisJson();

        rowIndex = 0;
        columnIndex = 0;

        updatePaginationIndicators(currentPage);

        for (int i = currentPage * plantesPerPage; i < (currentPage + 1) * plantesPerPage && i < plantes.size(); i++) {
            Plante plante = plantes.get(i);
            encyclopedieGridPane.add(creerPlantePane(plante), columnIndex, rowIndex);

            columnIndex++;

            if (columnIndex > 1) {
                columnIndex = 0;
                rowIndex++;
            }
        }
    }

    private void createPaginationIndicators(int totalPages) {
        paginationHBox.getChildren().clear();

        for (int i = 0; i < totalPages; i++) {
            Circle circle = new Circle(3);
            circle.setFill(Color.WHITE);
            paginationHBox.getChildren().add(circle);
        }
    }

    private void updatePaginationIndicators(int currentPage) {
        for (int i = 0; i < paginationHBox.getChildren().size(); i++) {
            Circle circle = (Circle) paginationHBox.getChildren().get(i);
            if (i == currentPage) {
                circle.setStroke(Color.BLACK);
                circle.setStrokeWidth(1.5);
            } else {
                circle.setStroke(Color.TRANSPARENT);
            }
        }
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            Stage primaryStage = (Stage) encyclopedieAnchorPane.getScene().getWindow();
            Scene newScene = new Scene(newView, 335, 600);
            primaryStage.setScene(newScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAddButtonClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/encyclopedie-page-add.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Ajoutez une nouvelle plante dans l'encyclopédie");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void handleSwipeRight(MouseEvent event) {
        if ((currentPage + 1) * plantesPerPage < totalPlantes) {
            currentPage++;
            updateEncyclopedieGridPane();
        }
    }

    @FXML
    public void handleSwipeLeft(MouseEvent event) {
        if (currentPage > 0) {
            currentPage--;
            updateEncyclopedieGridPane();
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

