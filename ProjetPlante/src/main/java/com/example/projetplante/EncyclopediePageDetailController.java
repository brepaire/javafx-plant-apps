package com.example.projetplante;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;


public class EncyclopediePageDetailController {
    @FXML
    private Label nomLabel;
    @FXML
    private Label saisonLabel;
    @FXML
    private Label vivaceLabel;
    @FXML
    private Label dureePousseLabel;
    @FXML
    private Label finalPlantationLabel;
    @FXML
    private Label intervalleArrosageLabel;
    @FXML
    private ImageView imageView;
    @FXML
    private String imageURL;

    /******Caractéristique si on modifie la plante*****/

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
    private TextField unitePousseTextField;
    @FXML
    private TextField uniteArrosageTextField;
    @FXML
    private Label imageImportStatusLabel;
    @FXML
    private ImageView addImageView;

    /************Bouton************/
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button importImageButton;

    /********Autre*******/
    private Plante plante;
    @FXML
    private Pane infoDetailPane;
    @FXML
    private Pane editDetailPane;
    private boolean edit;


    public EncyclopediePageDetailController(){

    }
    @FXML
    public void initialize() {
        edit = false;
        infoDetailPane.setVisible(true);
        editDetailPane.setVisible(false);

        editDetailPane.setPickOnBounds(false);

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

        saveButton.setOnAction(event -> handleSaveButtonClick(event));

        importImageButton.setOnAction(event -> handleImportImageButtonAction());

    }

    public void setPlante(Plante plante) {
        this.plante = plante;

        nomLabel.setText(plante.getNom());
        saisonLabel.setText(plante.getSaison());
        vivaceLabel.setText(plante.getVivace());
        dureePousseLabel.setText(plante.getDureePousse() + " " + plante.getUnitePousse());
        finalPlantationLabel.setText(plante.getPlantationDe() + " à " + plante.getPlantationA());
        intervalleArrosageLabel.setText(plante.getIntervalleArrosage() + " " +plante.getUniteArrosage());
        /**
         * permet de charger les données dans les containers
         */
        plantationDeSplitMenuButton.setText(plante.getPlantationDe());
        plantationASplitMenuButton.setText(plante.getPlantationA());
        saisonSplitMenuButton.setText(plante.getSaison());
        vivaceSplitMenuButton.setText(plante.getVivace());
        dureePousseTextField.setText(plante.getDureePousse());
        intervalleArrosageTextField.setText(plante.getIntervalleArrosage());
        unitePousseTextField.setText(plante.getUnitePousse());
        uniteArrosageTextField.setText(plante.getUniteArrosage());


        if (plante.getImageURL() != null && !plante.getImageURL().isEmpty()) {
            String imageUrl = plante.getImageURL();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                imageView.setImage(new Image(plante.getImageURL()));
                addImageView.setImage(new Image(plante.getImageURL()));
                imageURL = plante.getImageURL();
            } else if (imageUrl != null && imageUrl.isEmpty()) {
                String notFoundImagePath = "src/main/resources/bankImage/NOT_FOUND.png";
                imageView.setImage(new Image(notFoundImagePath));
                addImageView.setImage(new Image(notFoundImagePath));
                imageURL = notFoundImagePath;
            }
        }
    }


    private void updatePlante() {
        plante.setSaison(saisonSplitMenuButton.getText());
        plante.setVivace(vivaceSplitMenuButton.getText());
        plante.setDureePousse(dureePousseTextField.getText());
        plante.setIntervalleArrosage(intervalleArrosageTextField.getText());
        plante.setUnitePousse(unitePousseTextField.getText());
        plante.setUniteArrosage(uniteArrosageTextField.getText());
        plante.setPlantationDe(plantationDeSplitMenuButton.getText());
        plante.setPlantationA(plantationASplitMenuButton.getText());
        plante.setImageURL(imageURL);
    }
    private void updateJSON() {
        String filePath = "src/main/resources/Encyclopedie.json";
        JSONArray jsonArray;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            jsonArray = new JSONArray(content);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la lecture du fichier JSON.");
            return;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                String intervalleArrosage = intervalleArrosageTextField.getText() + " " + uniteArrosageTextField.getText();
                jsonObject.put("IntervalleArrosage", intervalleArrosage);
                jsonObject.put("PlantationDe", plante.getPlantationDe());
                jsonObject.put("PlantationA", plante.getPlantationA());
                String dureePousse = dureePousseTextField.getText() + " " + unitePousseTextField.getText();
                jsonObject.put("DureePousse",dureePousse);
                jsonObject.put("Saison", plante.getSaison());
                jsonObject.put("Vivace", plante.getVivace());
                jsonObject.put("imageUrl", plante.getImageURL());
                break;
            }
        }

        writeJSONToFile(jsonArray, filePath);
    }
    private void writeJSONToFile(JSONArray jsonArray, String filePath) {
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(jsonArray.toString(4));
            System.out.println("Fichier JSON mis à jour avec succès!");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la mise à jour du fichier JSON.");
        }
    }
    @FXML
    public void handleSaveButtonClick(ActionEvent event) {
        updatePlante();
        updateJSON();
        switchPane();
    }
    @FXML
    public void handleEditButtonClick(ActionEvent event) {
        switchPane();
    }

    public void switchPane(){
        if(edit == false){
            edit = true;
        } else {
            edit = false;
        }
        if(edit){
            infoDetailPane.setVisible(false);
            editDetailPane.setVisible(true);
        } else {
            infoDetailPane.setVisible(true);
            editDetailPane.setVisible(false);
        }
    }
    @FXML
    public void handleCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void handleDeleteButtonClick(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette plante?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            removeJSON();

        }
    }

    private void removeJSON() {
        String filePath = "src/main/resources/Encyclopedie.json";
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
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                jsonArray.remove(i);
            }
        }
        writeJSONToFile(jsonArray, filePath);
        initialize();

    }
    @FXML
    private void handleImportImageButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );
        File file = fileChooser.showOpenDialog(editDetailPane.getScene().getWindow());
        if (file != null) {
            try {
                BufferedImage image = ImageIO.read(file);
                addImageView.setImage(SwingFXUtils.toFXImage(image, null));

                imageURL = file.toURI().toString();
                plante.setImageURL(imageURL);
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
