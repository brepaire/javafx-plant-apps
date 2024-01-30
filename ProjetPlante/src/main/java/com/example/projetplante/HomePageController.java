package com.example.projetplante;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;


public class HomePageController {
    @FXML
    private AnchorPane homeAnchorPane;
    @FXML
    private GridPane calendrierGridPane;

    private double initialX;
    private LocalDate startDate;
    @FXML
    private Label monthYearLabel;

    private List<Plante> plantes;
    @FXML
    private VBox tasksContainer;
    @FXML
    private ScrollPane tasksScrollPane;
    private LocalDate actuDate;





    private void loadPage(String fxmlPath ) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            Stage primaryStage = (Stage) homeAnchorPane.getScene().getWindow();
            Scene newScene = new Scene(newView, 335, 600);
            primaryStage.setScene(newScene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadPageWithDate(String fxmlPath, LocalDate targetDate, Stage primaryStage) {
        System.out.println(targetDate);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent newView = loader.load();

            HomePageController controller = loader.getController();
            actuDate=targetDate;
            controller.buildCalendar(targetDate);

            Scene newScene = new Scene(newView, 335, 600);
            primaryStage.setScene(newScene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadHomePageWithDate(String fxmlPath, LocalDate targetDate, Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(HomePageController.class.getResource(fxmlPath));
        try {
            Parent newView = loader.load();
            HomePageController controller = loader.getController();
            controller.buildCalendar(targetDate);
            controller.loadPageWithDate(fxmlPath, targetDate, primaryStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void buildCalendar(LocalDate targetDate) {
        actuDate=targetDate;
        this.plantes = rPlantesForDay(targetDate);
        displayTasks(plantes, targetDate);
        calendrierGridPane.getChildren().clear();
        calendrierGridPane.getColumnConstraints().clear();
        calendrierGridPane.getRowConstraints().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault());
        String monthYearText = targetDate.format(formatter);
        monthYearLabel.setText(monthYearText);

        for (int i = 0; i < 4; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints(70);

            calendrierGridPane.getColumnConstraints().add(columnConstraints);
        }
        for (int i = 0; i < 2; i++) {
            RowConstraints rowConstraints = new RowConstraints(70);
            calendrierGridPane.getRowConstraints().add(rowConstraints);
        }

        LocalDate currentDate = targetDate;
        for (int col = 0; col < 4; col++) {
            String dayName = currentDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
            Label dayLabel = new Label(dayName);
            dayLabel.setStyle("-fx-font-weight: bold;");
            calendrierGridPane.add(dayLabel, col, 0);

            DateAnchorPane cellAnchorPane = new DateAnchorPane();
            cellAnchorPane.setPrefSize(70, 70);
            cellAnchorPane.setDate(currentDate);
            LocalDate finalDate =actuDate;
            cellAnchorPane.setOnMouseClicked(event -> {
                handleCellClick(cellAnchorPane);
            });


            Label dayOfMonthLabel = new Label(String.valueOf(currentDate.getDayOfMonth()));
            cellAnchorPane.getChildren().add(dayOfMonthLabel);
            if (currentDate.isBefore(LocalDate.now().plusDays(5)) && !currentDate.isBefore(LocalDate.now())) {
                addWeatherToCell(cellAnchorPane, currentDate);
            }


            calendrierGridPane.add(cellAnchorPane, col, 1);
            currentDate = currentDate.plusDays(1);
        }
        displayTasks(plantes ,targetDate);
    }

    private void addWeatherToCell(AnchorPane cellAnchorPane, LocalDate date) {
        double latitude = 48.8566; // Latitude de Paris, remplacez par la latitude de votre choix
        double longitude = 2.3522; // Longitude de Paris, remplacez par la longitude de votre choix

        WeatherService weatherService = new WeatherService();
        try {
            WeatherData weatherData = weatherService.getWeatherData(date, latitude, longitude);
            String iconUrl = "http://openweathermap.org/img/wn/" + weatherData.getIcon() + ".png";
            ImageView weatherIcon = new ImageView(new Image(iconUrl, 25, 25, true, true));
            Label temperatureLabel = new Label(String.format("%.1f °C", weatherData.getTemperature()));

            AnchorPane.setTopAnchor(weatherIcon, -10.0);
            AnchorPane.setRightAnchor(weatherIcon, 30.0);
            AnchorPane.setTopAnchor(temperatureLabel, 30.0);
            AnchorPane.setRightAnchor(temperatureLabel, 30.0);

            cellAnchorPane.getChildren().addAll(weatherIcon, temperatureLabel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayTasks(List<Plante> plantes, LocalDate date) {
        tasksContainer.getChildren().clear();
        for (Plante plante : plantes) {
            List<String> tasks = generateTasksForPlante(plante, date);
            for (String task : tasks) {
                CheckBox checkBox = new CheckBox(task);
                checkBox.setOnAction(event -> {
                    if (checkBox.isSelected()) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Confirmation");
                        alert.setHeaderText("Supprimer la tâche");
                        alert.setContentText("Avez vous effectué la tâche ?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            tasksContainer.getChildren().remove(checkBox);
                            removeTaskJSON(date, task,plante); // Ajoutez cette ligne pour supprimer la tâche du fichier JSON
                        } else {
                            checkBox.setSelected(false);
                        }
                    }
                });

                tasksContainer.getChildren().add(checkBox);
            }
        }
    }


    List<String> generateTasksForPlante(Plante plante, LocalDate date){
        List<String> taches = new ArrayList<>();
        String nom = plante.getNom();
        //System.out.println();
        JSONObject jsonObject = plante.getTask();
        //System.out.println(jsonObject);
        String dateString = date.toString();
        if (jsonObject !=null){
            if (jsonObject.has(dateString)) {
                JSONObject tacheObj = jsonObject.getJSONObject(dateString);
                for (String tache : tacheObj.keySet()) {
                    taches.add(tache + " " + nom + " : " + tacheObj.getString(tache));
                }
            }
        }

        return taches;
    }

    private List<Plante> rPlantesForDay(LocalDate date) {

        List<Plante> plantes = new ArrayList<>();
        try {
            Calendrier calendrier = new Calendrier(date);
            if (date == null) {
                date = LocalDate.now();
            }
            YearMonth yearMonth = YearMonth.from(date);


            Map<LocalDate, List<Plante>> plantesParJour = calendrier.readPlantesJSONForMonth(yearMonth);

            plantes = plantesParJour.get(date);
            if (plantes == null) {
                plantes = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plantes;
    }





    @FXML
    public void initialize() {
        this.startDate =LocalDate.now();
        buildCalendar(this.startDate);
        tasksScrollPane.setStyle("-fx-background: #F4BD7F; -fx-background-insets: 0; -fx-padding: 0;");


        tasksScrollPane.setFitToWidth(true);


        calendrierGridPane.setOnMousePressed(event -> {
            initialX = event.getSceneX();
        });
        calendrierGridPane.setOnMouseReleased(event -> {
            double finalX = event.getSceneX();
            double deltaX = finalX - initialX;

            if (Math.abs(deltaX) > 10) {
                if (deltaX > 0) {
                    handleSwipeLeft();
                } else {
                    handleSwipeRight();
                }
            }
        });
    }

    private void handleSwipeLeft() {
        LocalDate newStartDate = actuDate.minusDays(1);
        actuDate=newStartDate;
        buildCalendar(actuDate);
    }

    private void handleSwipeRight() {
        LocalDate newStartDate = actuDate.plusDays(1);
        actuDate=newStartDate;
        buildCalendar(actuDate);
    }

    private void handleCellClick(DateAnchorPane cellAnchorPane) {
        LocalDate targetDate = cellAnchorPane.getDate();
        Stage primaryStage = (Stage) homeAnchorPane.getScene().getWindow();
        loadPageWithDate("/home-page.fxml", targetDate, primaryStage);
    }

    private void removeTaskJSON(LocalDate targetDate, String taskToRemove, Plante plante) {
        String SEPARATEUR = " ";
        String mot = taskToRemove;
        String mots[] = mot.split(SEPARATEUR);
        taskToRemove = mots[0];
        System.out.println(taskToRemove);
        String filePath = "src/main/resources/Plantation.json";
        JSONArray jsonArray;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonArray = new JSONArray(content);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la lecture");
            return;
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println(jsonObject);
            if (jsonObject !=null) {
                if (jsonObject.getString("Nom").equals(plante.getNom())) {
                    String dateString = targetDate.toString();
                    if (jsonObject.has("Tache")) {
                        JSONObject tasksJson = jsonObject.getJSONObject("Tache");
                        System.out.println(tasksJson);
                        String targetDateString = targetDate.toString();
                        System.out.println(tasksJson.has(targetDateString));
                        if (tasksJson.has(targetDateString)) {
                            JSONObject taskJson = tasksJson.getJSONObject(targetDateString);
                            taskJson.remove(taskToRemove);
                            System.out.println(taskJson);

                        }
                    }
                }
            }
        }

        writeJSONToFile(jsonArray, filePath);
    }





    private void writeJSONToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toString(4));
            System.out.println("Mise a jour réussi");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur pendant la MAJ");
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

