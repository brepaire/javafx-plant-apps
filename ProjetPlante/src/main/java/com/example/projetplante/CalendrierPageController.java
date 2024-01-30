package com.example.projetplante;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendrierPageController {
    @FXML
    private AnchorPane calendrierAnchorPane;
    @FXML
    private GridPane calendrierGridPane;
    @FXML
    private Label monthYearLabel;

    private YearMonth startMonth;
    private YearMonth endMonth;

    private double initialX;
    private static CalendrierPageController instance;

    public CalendrierPageController() {
        instance = this;
    }

    public static CalendrierPageController getInstance() {
        return instance;
    }
    public Scene getScene() {
        return calendrierAnchorPane.getScene();
    }


    @FXML
    public void initialize() {
        calendrierGridPane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
        startMonth = YearMonth.now();
        endMonth = startMonth.plusYears(2).minusMonths(1);
        buildCalendar(startMonth);

        calendrierGridPane.setOnMousePressed(event -> {
            initialX = event.getSceneX();
        });
        calendrierGridPane.setOnMouseReleased(event -> {
            double finalX = event.getSceneX();
            double deltaX = finalX - initialX;

            if (Math.abs(deltaX) > 10) {
                if (deltaX > 0) {
                    handlePreviousMonth();
                } else {
                    handleNextMonth();
                }
            }
        });
    }

    public void buildCalendar(YearMonth targetMonth) {
        calendrierGridPane.getChildren().clear();

        LocalDate firstDayOfMonth = targetMonth.atDay(1);
        int firstDayOfWeekValue = (firstDayOfMonth.getDayOfWeek().getValue() - 1) % 7;

        monthYearLabel.setText(targetMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + targetMonth.getYear());

        for (int i = 0; i < 7; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints(40);
            calendrierGridPane.getColumnConstraints().add(columnConstraints);
            if (i < 6) {
                RowConstraints rowConstraints = new RowConstraints(60);
                calendrierGridPane.getRowConstraints().add(rowConstraints);
            }
        }

        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
            String dayName = DayOfWeek.of(dayOfWeek + 1).getDisplayName(TextStyle.SHORT, Locale.getDefault());
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-weight: bold;");
            calendrierGridPane.add(dayLabel, dayOfWeek, 0);
        }

        LocalDate date = firstDayOfMonth.minusDays(firstDayOfWeekValue);
        Calendrier calendrier = new Calendrier(targetMonth.atDay(1));
        Map<LocalDate, List<Plante>> plantesParJour = calendrier.readPlantesJSONForMonth(targetMonth);
        for (int row = 1; row < 7; row++) {
            for (int col = 0; col < 7; col++) {
                AnchorPane cellAnchorPane = new AnchorPane();
                if(row!=7) {
                    cellAnchorPane.setStyle("-fx-grid-lines-visible: true; -fx-border-color: black;");
                }
                if (date.equals(LocalDate.now())) {
                    cellAnchorPane.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
                    cellAnchorPane.toFront();
                }
                cellAnchorPane.setPrefSize(38, 58);

                if (date.isBefore(LocalDate.now().plusDays(5)) && !date.isBefore(LocalDate.now())) {
                    addWeatherToCell(cellAnchorPane, date);
                }

                Label dayOfMonthLabel = new Label(String.valueOf(date.getDayOfMonth()));
                cellAnchorPane.getChildren().add(dayOfMonthLabel);

                List<Plante> plantesDuJour = plantesParJour.get(date);
                if (plantesDuJour != null && !plantesDuJour.isEmpty()) {
                    addPlanteToCell(cellAnchorPane);
                    boolean hasTaches = false;
                    for (Plante plante : plantesDuJour) {
                        if (plante.hasTaches()) {
                            hasTaches = true;
                            break;
                        }
                    }


                    if (hasTaches) {
                        addLivreToCell(cellAnchorPane);
                    }
                }

                LocalDate finalDate = date;
                cellAnchorPane.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) { // Double-click
                        showPlantesForDay(finalDate);
                    }
                });

                if (!date.getMonth().equals(targetMonth.getMonth())) {
                    dayOfMonthLabel.setStyle("-fx-opacity: 0.5;");
                }
                dayOfMonthLabel.setLayoutX(2);
                dayOfMonthLabel.setLayoutY(2);

                calendrierGridPane.add(cellAnchorPane, col, row);
                date = date.plusDays(1);
            }
        }
    }

    private void addPlanteToCell(AnchorPane cellAnchorPane) {
        String imagePath = "/bankImage/planteLogo.png";
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream, 20, 20, true, true);
        ImageView plante = new ImageView(image);
        AnchorPane.setTopAnchor(plante, 30.0);
        AnchorPane.setRightAnchor(plante, 0.0);
        cellAnchorPane.getChildren().add(plante);
    }
    private void addLivreToCell(AnchorPane cellAnchorPane) {
        String imagePath = "/bankImage/taskLogo.png";
        InputStream imageStream = getClass().getResourceAsStream(imagePath);
        Image image = new Image(imageStream, 17, 17, true, true);
        ImageView plante = new ImageView(image);
        AnchorPane.setTopAnchor(plante, 33.0);
        AnchorPane.setLeftAnchor(plante, 1.0);
        cellAnchorPane.getChildren().add(plante);
    }

    private void addWeatherToCell(AnchorPane cellAnchorPane, LocalDate date) {
        double latitude = 48.8566;
        double longitude = 2.3522;

        WeatherService weatherService = new WeatherService();
        try {
            WeatherData weatherData = weatherService.getWeatherData(date, latitude, longitude);
            String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getIcon() + ".png";
            ImageView weatherIcon = new ImageView(new Image(iconUrl, 30, 30, true, true));
            AnchorPane.setTopAnchor(weatherIcon, 0.0);
            AnchorPane.setRightAnchor(weatherIcon, 0.0);
            cellAnchorPane.getChildren().add(weatherIcon);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void showPlantesForDay(LocalDate date) {
        try {
            Calendrier calendrier = new Calendrier(date);
            YearMonth yearMonth = YearMonth.from(date);
            Map<LocalDate, List<Plante>> plantesParJour = calendrier.readPlantesJSONForMonth(yearMonth);

            List<Plante> plantes = plantesParJour.get(date);
            if (plantes == null) {
                plantes = new ArrayList<>();
            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/calendrier-page-detail.fxml"));
            Parent root = fxmlLoader.load();
            CalendrierPageDetailController controller = fxmlLoader.getController();
            controller.setDateAndPlantes(date, plantes);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Plantes pour " + date.toString());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handlePreviousMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
        YearMonth currentMonth = YearMonth.parse(monthYearLabel.getText(), formatter);
        if (currentMonth.isAfter(startMonth)) {
            buildCalendar(currentMonth.minusMonths(1));
        }
    }

    @FXML
    private void handleNextMonth() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
        YearMonth currentMonth = YearMonth.parse(monthYearLabel.getText(), formatter);
        if (currentMonth.isBefore(endMonth)) {
            buildCalendar(currentMonth.plusMonths(1));
        }
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            Stage primaryStage = (Stage) calendrierAnchorPane.getScene().getWindow();
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

