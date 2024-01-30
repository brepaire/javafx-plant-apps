package com.example.projetplante;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.net.URL;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.UnaryOperator;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PlantePageAjoutNewController {
    @FXML
    private Button closeButton;

    //Variable lié au formulaire .fxml
    @FXML
    private SplitMenuButton plantationDeSplitMenuButton;
    @FXML
    private SplitMenuButton plantationASplitMenuButton;
    @FXML
    private SplitMenuButton saisonSplitMenuButton;
    @FXML
    private SplitMenuButton vivaceSplitMenuButton;
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
    private TextField unitePousseTextField;
    @FXML
    private TextField uniteArrosageTextField;
    @FXML
    private Label vivaceLabel;


    @FXML
    private Button saveButton;
    /************************/
    @FXML
    private AnchorPane planteAddNewAnchorPane;

    private static Popup currentPopup;

    private Plante plante = new Plante();
    @FXML
    private Label imageImportStatusLabel;

    @FXML
    private Label isItAdd;
    @FXML
    private Button importImageButton;
    @FXML
    private ImageView imageView;
    public PlantePageAjoutNewController(){

    }
    public void initialize() {
        planteAddNewAnchorPane.setPickOnBounds(false);
        dateRecoltePicker.setVisible(!vivaceSplitMenuButton.getText().equals("Oui"));
        vivaceLabel.setVisible(vivaceSplitMenuButton.getText().equals("Oui"));

        for (Month month : Month.values()) {
            MenuItem deItem = new MenuItem(month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRENCH));
            deItem.setOnAction(e -> plantationDeSplitMenuButton.setText(deItem.getText()));
            plantationDeSplitMenuButton.getItems().add(deItem);

            MenuItem toItem = new MenuItem(month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.FRENCH));
            toItem.setOnAction(e -> plantationASplitMenuButton.setText(toItem.getText()));
            plantationASplitMenuButton.getItems().add(toItem);
        }

        String[] saisons = {"Printemps", "Été", "Automne", "Hiver"};
        for (String saison : saisons) {
            MenuItem saisonItem = new MenuItem(saison);
            saisonItem.setOnAction(e -> saisonSplitMenuButton.setText(saisonItem.getText()));
            saisonSplitMenuButton.getItems().add(saisonItem);
        }

        String[] vivaces = {"Oui", "Non"};
        for (String vivace : vivaces) {
            MenuItem vivaceItem = new MenuItem(vivace);
            vivaceItem.setOnAction(e -> vivaceSplitMenuButton.setText(vivaceItem.getText()));
            vivaceSplitMenuButton.getItems().add(vivaceItem);
        }
        vivaceSplitMenuButton.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Oui")) {
                dateRecoltePicker.setVisible(false);
                vivaceLabel.setVisible(true);
            } else {
                dateRecoltePicker.setVisible(true);
                vivaceLabel.setVisible(false);
            }
        });


        UnaryOperator<TextFormatter.Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter1 = new TextFormatter<>(integerFilter);
        TextFormatter<String> textFormatter2 = new TextFormatter<>(integerFilter);
        dureePousseTextField.setTextFormatter(textFormatter1);
        intervalleArrosageTextField.setTextFormatter(textFormatter2);

        saveButton.setOnAction(event -> {
            JSONObject plante = buildPlanteJSON();
            String nom = nomTextField.getText();
            createOrUpdateJSON(nom, plante);
        });

        importImageButton.setOnAction(event -> handleImportImageButtonAction());

    }
    @FXML
    private void handleToggleButtonAction(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/plante-page-add.fxml"));
            Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Ajoutez une plante");
            Node source = (Node) event.getSource();
            Window currentStage = source.getScene().getWindow();
            currentStage.hide();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void createOrUpdateJSON(String nom, JSONObject plante) {
        String plantationFilePath = getResourcePath("Plantation.json");
        String encyclopedieFilePath = getResourcePath("Encyclopedie.json");

        boolean planteExistDansPlantation = false;
        boolean planteExistDansEncyclopedie = false;
        try {
            JSONArray plantation = readOrCreateJSONArray(plantationFilePath);
            JSONArray encyclopedie = readOrCreateJSONArray(encyclopedieFilePath);
            for (int i = 0; i < plantation.length(); i++) {
                if (plantation.getJSONObject(i).getString("Nom").equals(nom)) {
                    planteExistDansPlantation = true;
                    break;
                }
            }
            for (int i = 0; i < encyclopedie.length(); i++) {
                if (encyclopedie.getJSONObject(i).getString("Nom").equals(nom)) {
                    planteExistDansEncyclopedie = true;
                    break;
                }
            }
            if (planteExistDansPlantation && planteExistDansEncyclopedie) {
                isItAdd.setText("Plante non ajouté car déjà existante et renseignée");
            } else if (planteExistDansPlantation) {
                JSONObject planteEncyclopedie = buildEncyclopedieJSON();
                encyclopedie.put(planteEncyclopedie);
                isItAdd.setText("Plante ajoutée à l'encyclopédie");
            } else if (planteExistDansEncyclopedie) {
                plantation.put(plante);
                isItAdd.setText("Plante ajoutée à la plantation");
            } else {
                plantation.put(plante);
                JSONObject planteEncyclopedie = buildEncyclopedieJSON();
                encyclopedie.put(planteEncyclopedie);
                isItAdd.setText("Plante ajoutée à la plantation et à l'encyclopédie");
            }

            writeJSONArrayToFile(plantation, plantationFilePath);
            writeJSONArrayToFile(encyclopedie, encyclopedieFilePath);
        } catch (IOException e) {
            e.printStackTrace();
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

    private JSONObject buildPlanteJSON() {
        JSONObject planteJson = new JSONObject();
        planteJson.put("Nom", nomTextField.getText());
        planteJson.put("PlantationDe", plantationDeSplitMenuButton.getText());
        planteJson.put("PlantationA", plantationASplitMenuButton.getText());
        planteJson.put("Saison", saisonSplitMenuButton.getText());
        planteJson.put("Vivace", vivaceSplitMenuButton.getText());
        String dureePousse = dureePousseTextField.getText() + " " + unitePousseTextField.getText();
        planteJson.put("DureePousse", dureePousse);
        String intervalleArrosage = intervalleArrosageTextField.getText() + " " + uniteArrosageTextField.getText();
        planteJson.put("IntervalleArrosage", intervalleArrosage);
        if (dateMiseEnTerrePicker.getValue() != null) {
            planteJson.put("DateMiseEnTerre", dateMiseEnTerrePicker.getValue().toString());
        }
        if (!vivaceSplitMenuButton.getText().equals("Oui") && dateRecoltePicker.getValue() != null) {
            planteJson.put("DateRecolte", dateRecoltePicker.getValue().toString());
        }
        planteJson.put("imageUrl", plante.getImageURL());
        return planteJson;
    }


    private JSONObject buildEncyclopedieJSON() {
        JSONObject encyclopedie = new JSONObject();
        encyclopedie.put("Nom", nomTextField.getText());
        encyclopedie.put("PlantationDe", plantationDeSplitMenuButton.getText());
        encyclopedie.put("PlantationA", plantationASplitMenuButton.getText());
        encyclopedie.put("Saison", saisonSplitMenuButton.getText());
        encyclopedie.put("Vivace", vivaceSplitMenuButton.getText());
        String dureePousse = dureePousseTextField.getText() + " " + unitePousseTextField.getText();
        encyclopedie.put("DureePousse", dureePousse);
        String intervalleArrosage = intervalleArrosageTextField.getText() + " " + uniteArrosageTextField.getText();
        encyclopedie.put("IntervalleArrosage", intervalleArrosage);
        encyclopedie.put("imageUrl", plante.getImageURL());
        return encyclopedie;
    }
    private void writeJSONArrayToFile(JSONArray jsonArray, String filePath) throws IOException {
        try (PrintWriter printWriter = new PrintWriter(filePath)) {
            printWriter.print(jsonArray.toString(4)); // Le 4 pour l'indentation
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
    @FXML
    private void handleImportImageButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(planteAddNewAnchorPane.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                imageView.setImage(SwingFXUtils.toFXImage(image, null));

                plante.setImageURL(file.toURI().toString());

                imageImportStatusLabel.setText("Image importée avec succès!");
            } catch (IOException e) {
                e.printStackTrace();
                imageImportStatusLabel.setText("Erreur pendant l'importation de l'image");
            }
        } else {
            imageImportStatusLabel.setText("Aucune image sélectionnée");
        }
    }
}
