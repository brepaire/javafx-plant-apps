package com.example.projetplante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PlantePageController {
    @FXML
    private AnchorPane planteAnchorPane;
    @FXML
    private GridPane planteGridPane;

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

    public PlantePageController(){

    }

    @FXML
    public void initialize() {
        List<Plante> plantes = chargerPlantesDepuisJson();
        totalPlantes = plantes.size();

        int totalPages = (int) Math.ceil((double) totalPlantes / plantesPerPage);
        createPaginationIndicators(totalPages);
        updatePaginationIndicators(currentPage);

        for (int i = currentPage * plantesPerPage; i < (currentPage + 1) * plantesPerPage && i < plantes.size(); i++) {
            Plante plante = plantes.get(i);
            planteGridPane.add(creerPlantePane(plante), columnIndex, rowIndex);

            columnIndex++;

            if (columnIndex > 1) {
                columnIndex = 0;
                rowIndex++;
            }
        }
        planteGridPane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
    }

    private static List<Plante> chargerPlantesDepuisJson() {
        List<Plante> plantes = new ArrayList<>();
        try {
            InputStream inputStream = PlantePageController.class.getResourceAsStream("/Plantation.json");
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
                plante.setIntervalleArrosage(jsonObject.getString("IntervalleArrosage"));
                plante.setUniteArrosage(jsonObject.getString("IntervalleArrosage"));
                plante.setDureePousse(jsonObject.getString("DureePousse"));
                plante.setUnitePousse(jsonObject.getString("DureePousse"));

                plante.setPlantationDe(jsonObject.getString("PlantationDe"));
                plante.setPlantationA(jsonObject.getString("PlantationA"));
                plante.setDateMiseEnTerre(jsonObject.getString("DateMiseEnTerre"));
                if(jsonObject.has("DateRecolte")) {
                    plante.setDateRecolte(jsonObject.getString("DateRecolte"));
                }
                plante.setImageURL(jsonObject.getString("imageUrl"));

                if(jsonObject.has("Tache")) {
                    plante.setTask(jsonObject.getJSONObject("Tache"));
                    if(jsonObject.has("Arrosage")) {
                        plante.setArrosage(jsonObject.getString("Arrosage"));
                    }
                    if(jsonObject.has("Entretien")) {
                        plante.setEntretien(jsonObject.getString("Entretien"));
                    }
                    if(jsonObject.has("Coupe")) {
                        plante.setCoupe(jsonObject.getString("Coupe"));
                    }
                    if(jsonObject.has("Recolte")) {
                        plante.setRecolte(jsonObject.getString("Recolte"));
                    }
                }

                if(jsonObject.has("Suivi")) {
                    plante.setSuivi(jsonObject.getJSONObject("Suivi"));
                    if(jsonObject.has("Remarque")) {
                        plante.setRemarque(jsonObject.getString("Remarque"));
                    }
                    if(jsonObject.has("Graphe")) {
                        plante.setGraphe(jsonObject.getString("Graphe"));
                    }
                }

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
        Text dateMiseEnTerre = new Text();
        Text dateRecolte = new Text();

        nom.setText(plante.getNom());
        nom.setTextAlignment(TextAlignment.CENTER);
        nom.setLayoutX((pane.getPrefWidth() - nom.getBoundsInLocal().getWidth()) / 2.0);
        nom.setLayoutY(20.0);

        /*dateMiseEnTerre.setText("Mise en terre : " + plante.getDateMiseEnTerre());
        dateMiseEnTerre.setTextAlignment(TextAlignment.CENTER);
        dateMiseEnTerre.setLayoutX((pane.getPrefWidth() - dateMiseEnTerre.getBoundsInLocal().getWidth()) / 2.0);
        dateMiseEnTerre.setLayoutY(64.0);

        if(plante.getDateRecolte() != null){
            dateRecolte.setText("Date de récolte : " + plante.getDateRecolte());
            dateRecolte.setTextAlignment(TextAlignment.CENTER);
            dateRecolte.setLayoutX((pane.getPrefWidth() - dateRecolte.getBoundsInLocal().getWidth()) / 2.0);
            dateRecolte.setLayoutY(78.0);
        }else{
            dateRecolte.setText("Date de récolte :\n Ne se récolte pas");
            dateRecolte.setTextAlignment(TextAlignment.CENTER);
            dateRecolte.setLayoutX((pane.getPrefWidth() - dateRecolte.getBoundsInLocal().getWidth()) / 2.0);
            dateRecolte.setLayoutY(78.0);
        }*/

        Image image = null;
        String imageUrl = plante.getImageURL();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            image = new Image(imageUrl);
        } else if (imageUrl != null && imageUrl.isEmpty()) {
            String notFoundImagePath = "src/main/resources/NOT_FOUND.png";
            image = new Image(notFoundImagePath);
        }
        ImageView imageView = new ImageView(image);
        imageView.setLayoutX(17.0);
        imageView.setLayoutY(25.0);
        imageView.setFitHeight(100.0);
        imageView.setFitWidth(115.0);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        /*saison.setText("Saison : " + plante.getSaison());
        saison.setTextAlignment(TextAlignment.CENTER);
        saison.setLayoutX((pane.getPrefWidth() - saison.getBoundsInLocal().getWidth()) / 2.0);
        saison.setLayoutY(64.0);

        vivace.setText("Vivace : " + plante.getVivace());
        vivace.setTextAlignment(TextAlignment.CENTER);
        vivace.setLayoutX((pane.getPrefWidth() - vivace.getBoundsInLocal().getWidth()) / 2.0);
        vivace.setLayoutY(78.0);

        dureePousse.setText("Durée de pousse : " + plante.getDureePousse());
        dureePousse.setTextAlignment(TextAlignment.CENTER);
        dureePousse.setLayoutX((pane.getPrefWidth() - dureePousse.getBoundsInLocal().getWidth()) / 2.0);
        dureePousse.setLayoutY(92.0);

        finalPlantation.setText("Plantation de " + plante.getPlantationDe() + " à " + plante.getPlantationA());
        finalPlantation.setTextAlignment(TextAlignment.CENTER);
        finalPlantation.setLayoutX((pane.getPrefWidth() - finalPlantation.getBoundsInLocal().getWidth()) / 2.0);
        finalPlantation.setLayoutY(106.0);

        intervalleArrosage.setText("Arrosage : " + plante.getIntervalleArrosage());
        intervalleArrosage.setTextAlignment(TextAlignment.CENTER);
        intervalleArrosage.setLayoutX((pane.getPrefWidth() - intervalleArrosage.getBoundsInLocal().getWidth()) / 2.0);
        intervalleArrosage.setLayoutY(120.0);

        if(plante.getDateRecolte() != null){
            dateRecolte.setText("Date de récolte : " + plante.getDateRecolte());
            dateRecolte.setTextAlignment(TextAlignment.CENTER);
            dateRecolte.setLayoutX((pane.getPrefWidth() - dateRecolte.getBoundsInLocal().getWidth()) / 2.0);
            dateRecolte.setLayoutY(148.0);
        }else{
            dateRecolte.setText("Date de récolte :\n Ne se récolte pas");
            dateRecolte.setTextAlignment(TextAlignment.CENTER);
            dateRecolte.setLayoutX((pane.getPrefWidth() - dateRecolte.getBoundsInLocal().getWidth()) / 2.0);
            dateRecolte.setLayoutY(148.0);
        }*/



        pane.getChildren().add(nom);
        /*pane.getChildren().add(saison);
        pane.getChildren().add(vivace);
        pane.getChildren().add(dureePousse);
        pane.getChildren().add(finalPlantation);
        pane.getChildren().add(intervalleArrosage);
        pane.getChildren().add(dateMiseEnTerre);
        pane.getChildren().add(dateRecolte);*/

        pane.getChildren().add(imageView);
        pane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
        pane.setOnMouseClicked(event -> openPlanteDetailsWindow(plante.getNom()));

        return pane;
    }

    public static Plante getPlanteByName(List<Plante> plantesList, String planteName) {
        for (Plante plante : plantesList) {
            if (plante.getNom().equalsIgnoreCase(planteName)) {
                return plante;
            }
        }
        return null;
    }

    static void openPlanteDetailsWindow(String planteName) {
        List<Plante> plantes = chargerPlantesDepuisJson();
        Plante plante = getPlanteByName(plantes, planteName);

        if (plante != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(PlantePageController.class.getResource("/plante-page-detail.fxml"));
                Parent root = fxmlLoader.load();
                PlantePageDetailController controller = fxmlLoader.getController();
                controller.setPlante(plante);

                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.setTitle("Détails de la plante");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Plante non trouvée: " + planteName);
        }
    }

    private void updatePlanteGridPane() {
        planteGridPane.getChildren().clear();
        List<Plante> plantes = chargerPlantesDepuisJson();

        rowIndex = 0;
        columnIndex = 0;
        updatePaginationIndicators(currentPage);

        for (int i = currentPage * plantesPerPage; i < (currentPage + 1) * plantesPerPage && i < plantes.size(); i++) {
            Plante plante = plantes.get(i);
            planteGridPane.add(creerPlantePane(plante), columnIndex, rowIndex);

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

            Stage primaryStage = (Stage) planteAnchorPane.getScene().getWindow();
            Scene newScene = new Scene(newView, 335, 600);
            primaryStage.setScene(newScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void handleAddButtonClick(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/plante-page-add-new.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Ajoutez une nouvelle plante");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @FXML
    public void handleSwipeRight(MouseEvent event) {
        if ((currentPage + 1) * plantesPerPage < totalPlantes) {
            currentPage++;
            updatePlanteGridPane();
        }
    }

    @FXML
    public void handleSwipeLeft(MouseEvent event) {
        if (currentPage > 0) {
            currentPage--;
            updatePlanteGridPane();
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

