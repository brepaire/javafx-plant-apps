package com.example.projetplante;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public class CalendrierPageDetailController {
    @FXML
    private Label dateLabel;
    private LocalDate date;
    @FXML
    private ListView<String> plantesListView;
    @FXML
    private Label plantesAssocieesLabel;
    @FXML
    private Button homeButton;
    @FXML
    private AnchorPane anchorPaneWeather;
    @FXML
    private Text weatherText;


    @FXML
    public void initialize() {
        plantesListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        plantesListView.setOnMouseClicked(this::handlePlanteSelection);
    }



    private void addWeatherToCell(AnchorPane cellAnchorPane, LocalDate date) {
        double latitude = 48.8566;
        double longitude = 2.3522;

        WeatherService weatherService = new WeatherService();
        try {
            WeatherData weatherData = weatherService.getWeatherData(date, latitude, longitude);
            String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getIcon() + ".png";
            ImageView weatherIcon = new ImageView(new Image(iconUrl, 50, 50, true, true));
            Label temperatureLabel = new Label(String.format("%.1f °C", weatherData.getTemperature()));
            temperatureLabel.setFont(new Font(13));
            AnchorPane.setTopAnchor(weatherIcon, 8.0);

            cellAnchorPane.getChildren().addAll(weatherIcon, temperatureLabel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDateAndPlantes(LocalDate date, List<Plante> plantes) {
        this.date = date;
        dateLabel.setText(date.toString());

        if (plantes.isEmpty()) {
            plantesAssocieesLabel.setText("Aucune plante associée à ce jour.");
        } else {
            plantesAssocieesLabel.setText(plantes.size() + " plantes associées à ce jour.");
            for (Plante plante : plantes) {
                plantesListView.getItems().add(plante.getNom());
            }
        }
        if (date != null && date.isBefore(LocalDate.now().plusDays(5)) && !date.isBefore(LocalDate.now())) {
            addWeatherToCell(anchorPaneWeather, date);
        } else {
            weatherText.setText("Pas de météo disponible pour ce jour");
        }
    }
    @FXML
    private void handleHomeButtonClick(ActionEvent event) {
        LocalDate date = LocalDate.parse(dateLabel.getText());
        Stage detailStage = (Stage) homeButton.getScene().getWindow();
        detailStage.close();
        CalendrierPageController calendrierPageController = CalendrierPageController.getInstance();
        if (calendrierPageController != null) {
            Stage primaryStage = (Stage) calendrierPageController.getScene().getWindow();
            HomePageController.loadHomePageWithDate("/home-page.fxml", date, primaryStage);
        }
    }
    @FXML
    private void handlePlanteSelection(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedPlante = plantesListView.getSelectionModel().getSelectedItem();
            if (selectedPlante != null) {
                PlantePageController.openPlanteDetailsWindow(selectedPlante);
            }
        }
    }
}
