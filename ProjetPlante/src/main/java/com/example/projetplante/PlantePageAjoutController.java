package com.example.projetplante;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class PlantePageAjoutController {
    @FXML
    private Button closeButton;
    @FXML
    private AnchorPane planteAddAnchorPane;
    //Variable lié au formulaire .fxml
    @FXML
    private ChoiceBox<String> plantationDeChoiceBox;
    @FXML
    private ChoiceBox<String> plantationAChoiceBox;
    @FXML
    private ChoiceBox<String> saisonChoiceBox;
    @FXML
    private ChoiceBox<String> vivaceChoiceBox;
    @FXML
    private TextField dureePousseTextField;
    @FXML
    private TextField intervalleArrosageTextField;
    @FXML
    private DatePicker dateMiseEnTerrePicker;
    @FXML
    private DatePicker dateRecoltePicker;
    @FXML
    private TextField nomTextField;
    @FXML
    private Button saveButton;
    @FXML
    private SplitMenuButton plantChoiceButton;
    /************************/
    @FXML
    private Label nomLabel;
    @FXML
    private Label intervalleArrosageLabel;
    @FXML
    private Label plantationLabel;
    @FXML
    private Label dureePousseLabel;
    @FXML
    private Label saisonLabel;
    @FXML
    private Label vivaceLabel;
    @FXML
    private Label vivaceS;
    @FXML
    private ImageView imageView = new ImageView();

    /*******************/

    public PlantePageAjoutController(){

    }
    public void initialize() {
        planteAddAnchorPane.setPickOnBounds(false);
        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        dateRecoltePicker.setVisible(true);
        vivaceS.setVisible(false);
        fillPlantChoiceButton();
        saveButton.setOnAction(e -> handleSaveButtonClick());
    }
    private void handleSaveButtonClick() {
        String plantationFilePath = getResourcePath("Plantation.json");
        try {
            JSONArray plantationArray = readOrCreateJSONArray(plantationFilePath);
            JSONObject selectedPlant = getSelectedPlant();

            if (selectedPlant != null) {
                LocalDate miseEnTerre = dateMiseEnTerrePicker.getValue();
                LocalDate recolte = dateRecoltePicker.getValue();

                selectedPlant.put("DateMiseEnTerre", miseEnTerre.toString());
                if(recolte != null) {
                    selectedPlant.put("DateRecolte", recolte.toString());
                }
                plantationArray.put(selectedPlant);
                writeJSONArrayToFile(plantationArray, plantationFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleToggleButtonAction(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/plante-page-add-new.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Ajoutez une nouvelle plante");
            Node source = (Node) event.getSource();
            Window currentStage = source.getScene().getWindow();
            currentStage.hide();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlePlantChoice(String plantName) {
        String encyclopedieFilePath = getResourcePath("Encyclopedie.json");
        try {
            JSONArray encyclopedie = readOrCreateJSONArray(encyclopedieFilePath);

            for (int i = 0; i < encyclopedie.length(); i++) {
                JSONObject plant = encyclopedie.getJSONObject(i);
                if (plant.getString("Nom").equals(plantName)) {
                    updatePlantInfo(plant);
                    plantChoiceButton.setText(plantName);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void fillPlantChoiceButton() {
        String encyclopedieFilePath = getResourcePath("Encyclopedie.json");
        String plantationFilePath = getResourcePath("Plantation.json");
        try {
            JSONArray encyclopedie = readOrCreateJSONArray(encyclopedieFilePath);
            JSONArray plantation = readOrCreateJSONArray(plantationFilePath);

            for (int i = 0; i < encyclopedie.length(); i++) {
                String nom = encyclopedie.getJSONObject(i).getString("Nom");
                boolean isPresentInPlantation = false;
                for (int j = 0; j < plantation.length(); j++) {
                    String nomPlantation = plantation.getJSONObject(j).getString("Nom");
                    if (nomPlantation.equals(nom)) {
                        isPresentInPlantation = true;
                        break;
                    }
                }
                if (!isPresentInPlantation) {
                    MenuItem menuItem = new MenuItem(nom);
                    menuItem.setOnAction(e -> handlePlantChoice(nom));
                    plantChoiceButton.getItems().add(menuItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getSelectedPlant() {
        String encyclopedieFilePath = getResourcePath("Encyclopedie.json");
        try {
            JSONArray encyclopedie = readOrCreateJSONArray(encyclopedieFilePath);
            String selectedPlantName = nomLabel.getText();

            for (int i = 0; i < encyclopedie.length(); i++) {
                JSONObject plant = encyclopedie.getJSONObject(i);
                if (plant.getString("Nom").equals(selectedPlantName)) {
                    return plant;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private void updatePlantInfo(JSONObject plant) {
        nomLabel.setText(plant.getString("Nom"));
        intervalleArrosageLabel.setText(plant.getString("IntervalleArrosage"));
        plantationLabel.setText(plant.getString("PlantationDe") + " - " + plant.getString("PlantationA"));
        dureePousseLabel.setText(plant.getString("DureePousse"));
        saisonLabel.setText(plant.getString("Saison"));
        vivaceLabel.setText(plant.getString("Vivace"));

        if(vivaceLabel.getText().equals("Oui")){
            vivaceS.setVisible(true);
            dateRecoltePicker.setVisible(false);
        } else {
            vivaceS.setVisible(false);
            dateRecoltePicker.setVisible(true);
        }

        Image image;
        if(plant.has("imageUrl")) {
            String imageUrl = plant.getString("imageUrl");
            if (imageUrl != null) {
                image = new Image(imageUrl);
                System.out.println(imageUrl);
                imageView.setImage(image);
            } else {
                String notFoundImagePath = "src/main/resources/bankImage/NOT_FOUND.png";
                image = new Image(notFoundImagePath);
                System.out.println(imageUrl);
                imageView.setImage(image);
            }
        } else {
            String notFoundImagePath = "src/main/resources/bankImage/NOT_FOUND.png";
            image = new Image(notFoundImagePath);
            imageView.setImage(image);
        }

    }


    private JSONArray readOrCreateJSONArray(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write("[]");
            }
        }
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        if (content.isEmpty() || content.charAt(0) != '[') {
            return new JSONArray();
        }

        return new JSONArray(content);
    }


    private void writeJSONArrayToFile(JSONArray jsonArray, String filePath) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(filePath)) {
            printWriter.print(jsonArray.toString(4));
        }
    }
    private String getResourcePath(String fileName) {
        String relativePath = "src/main/resources/" + fileName;
        File file = new File(relativePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Problème : " + fileName, e);
            }
        }
        return relativePath;
    }
}
