package com.example.projetplante;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.*;
import javafx.scene.control.Button;


import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Base64;
import java.util.Locale;
import java.util.function.UnaryOperator;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.embed.swing.SwingFXUtils;

public class EncyclopediePageAjoutController {

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
    private TextField nomTextField;
    @FXML
    private TextField unitePousseTextField;
    @FXML
    private TextField uniteArrosageTextField;
    @FXML
    private Button saveButton;
    /************************/
    @FXML
    private AnchorPane encyclopedieAddAnchorPane;

    @FXML
    private Button importImageButton;
    @FXML
    private ImageView imageView;
    private Plante plante = new Plante();
    @FXML
    private Label imageImportStatusLabel;

    public EncyclopediePageAjoutController(){

    }
    public void initialize() {
        encyclopedieAddAnchorPane.setPickOnBounds(false);
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
            plante.setNom(nomTextField.getText());
            plante.setPlantationDe(plantationDeSplitMenuButton.getText());
            plante.setPlantationA(plantationASplitMenuButton.getText());
            plante.setSaison(saisonSplitMenuButton.getText());
            plante.setVivace(vivaceSplitMenuButton.getText());
            String dureePousse = dureePousseTextField.getText() + " " + unitePousseTextField.getText();
            plante.setDureePousse(dureePousse);
            String intervalleArrosage = intervalleArrosageTextField.getText() + " " + uniteArrosageTextField.getText();
            plante.setIntervalleArrosage(intervalleArrosage);

            JSONObject planteJson = buildPlanteJSON();
            String nom = plante.getNom();
            createOrUpdateJSON(nom, planteJson);
        });

        importImageButton.setOnAction(event -> handleImportImageButtonAction());

    }

    private void createOrUpdateJSON(String nom, JSONObject plante) {
        String encyclopedieFilePath = getResourcePath("Encyclopedie.json");

        try {
            JSONArray encyclopedie = readOrCreateJSONArray(encyclopedieFilePath);
            boolean planteExisteDeja = false;
            for (int i = 0; i < encyclopedie.length(); i++) {
                if (encyclopedie.getJSONObject(i).getString("Nom").equals(nom)) {
                    planteExisteDeja = true;
                    break;
                }
            }

            if (!planteExisteDeja) {
                JSONObject planteEncyclopedie = buildEncyclopedieJSON();
                encyclopedie.put(planteEncyclopedie);
            }

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
        planteJson.put("Nom", plante.getNom());
        planteJson.put("PlantationDe", plante.getPlantationDe());
        planteJson.put("PlantationA", plante.getPlantationA());
        planteJson.put("Saison", plante.getSaison());
        planteJson.put("Vivace", plante.getVivace());
        planteJson.put("DureePousse", plante.getDureePousse());
        planteJson.put("IntervalleArrosage", plante.getIntervalleArrosage());
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
            printWriter.print(jsonArray.toString(4));
        }
    }
    private String getResourcePath(String fileName) {
        String absolutePath = "src/main/resources/" + fileName;
        File file = new File(absolutePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Problème : " + fileName, e);
            }
        }
        return absolutePath;
    }

    @FXML
    private void handleImportImageButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(encyclopedieAddAnchorPane.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                imageView.setImage(SwingFXUtils.toFXImage(image, null));

                plante.setImageURL(file.toURI().toString());

                imageImportStatusLabel.setText("Image importée avec succès");
            } catch (IOException e) {
                e.printStackTrace();
                imageImportStatusLabel.setText("Erreur pendant l'importation de l'image");
            }
        } else {
            imageImportStatusLabel.setText("Aucune image sélectionnée");
        }
    }
}
