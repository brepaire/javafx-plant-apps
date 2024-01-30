package com.example.projetplante;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Alert.AlertType;
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


public class PlantePageDetailController {

    /*****Caractéristique plante*****/
    @FXML
    private Label nomLabel;
    @FXML
    private Label saisonLabel;
    @FXML
    private Label vivaceLabel1;
    @FXML
    private Label dureePousseLabel;
    @FXML
    private Label finalPlantationLabel;
    @FXML
    private Label intervalleArrosageLabel;
    @FXML
    private Label miseEnTerreLabel;
    @FXML
    private Label recolteLabel;
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
    private DatePicker dateMiseEnTerrePicker;
    @FXML
    private DatePicker dateRecoltePicker;
    @FXML
    private TextField unitePousseTextField;
    @FXML
    private TextField uniteArrosageTextField;
    @FXML
    private Label vivaceLabel2;
    @FXML
    private Label imageImportStatusLabel;
    @FXML
    private ImageView addImageView;

    /******Caractéristique si on modifie la tache*****/

    @FXML
    private Text planteTask1;
    @FXML
    private Text planteTask2;
    @FXML
    private TextArea textAreaArrosageTask;
    @FXML
    private TextArea textAreaEntretienTask;
    @FXML
    private TextArea textAreaCoupeTask;
    @FXML
    private TextArea textAreaRecolteTask;
    @FXML
    private CheckBox checkBoxArrosageTask;
    @FXML
    private CheckBox checkBoxEntretienTask;
    @FXML
    private CheckBox checkBoxCoupeTask;
    @FXML
    private CheckBox checkBoxRecolteTask;
    @FXML
    private CheckBox checkBoxRecurrenceTask;
    @FXML
    private DatePicker datePickerRecurrenceTask;
    @FXML
    private SplitMenuButton splitMenuButtonRecurrenceTask;
    @FXML
    private SplitMenuButton splitMenuButtonDateEditTask;
    @FXML
    private Button saveTaskButton;
    @FXML
    private Text tacheText;

    /******Caractéristique de suivi de la plante*****/

    @FXML
    private Pane addSuiviDetailPane;
    @FXML
    private Pane suiviDetailPane;
    @FXML
    private Button editSuiviButton;
    @FXML
    private Button deleteSuiviButton;
    @FXML
    private Button addSuiviButton;
    @FXML
    private Button saveEditSuiviButton;
    @FXML
    private Button saveSuiviButton;
    @FXML
    private Text planteSuivi;
    @FXML
    private Text planteSuivi2;
    @FXML
    private Text suiviText;
    @FXML
    private Text dateID;
    @FXML
    private Text graphiqueText;
    @FXML
    private DatePicker suiviDatePicker;
    @FXML
    private TextArea suiviTextArea;

    /************Bouton************/
    @FXML
    private Button addTaskButton;
    @FXML
    private Button deleteTaskButton;
    @FXML
    private Button editTaskButton;
    @FXML
    private Button saveEditTaskButton;
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
    @FXML
    private Pane addTaskDetailPane;
    @FXML
    private Pane taskDetailPane;

    private boolean edit;
    public PlantePageDetailController(){

    }

    @FXML
    public void initialize() {
        edit = false;
        infoDetailPane.setVisible(true);
        editDetailPane.setVisible(false);

        taskDetailPane.setVisible(true);
        addTaskDetailPane.setVisible(false);
        addTaskButton.setText("Ajouté une tache");

        addSuiviButton.setText("Ajouté un suivi");
        graphiqueText.setVisible(false);
        addSuiviDetailPane.setVisible(false);
        suiviDetailPane.setVisible(true);

        editDetailPane.setPickOnBounds(false);
        dateRecoltePicker.setVisible(!vivaceSplitMenuButton.getText().equals("Oui"));
        vivaceLabel2.setVisible(vivaceSplitMenuButton.getText().equals("Oui"));

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
            if (newValue != null && newValue.equals("Oui")) {
                dateRecoltePicker.setVisible(false);
                vivaceLabel2.setVisible(true);
            } else {
                dateRecoltePicker.setVisible(true);
                vivaceLabel2.setVisible(false);
            }
        });

        textAreaArrosageTask.setDisable(true);
        textAreaEntretienTask.setDisable(true);
        textAreaCoupeTask.setDisable(true);
        textAreaRecolteTask.setDisable(true);
        splitMenuButtonRecurrenceTask.setDisable(true);

        checkBoxArrosageTask.setDisable(true);
        checkBoxEntretienTask.setDisable(true);
        checkBoxCoupeTask.setDisable(true);
        checkBoxRecolteTask.setDisable(true);

        checkBoxRecurrenceTask.setDisable(true);

        datePickerRecurrenceTask.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                checkBoxArrosageTask.setDisable(false);
                checkBoxEntretienTask.setDisable(false);
                checkBoxCoupeTask.setDisable(false);
                checkBoxRecolteTask.setDisable(false);
            } else {
                checkBoxArrosageTask.setDisable(true);
                checkBoxEntretienTask.setDisable(true);
                checkBoxCoupeTask.setDisable(true);
                checkBoxRecolteTask.setDisable(true);
            }
        });


        checkBoxArrosageTask.setOnAction(event -> {
            if (checkBoxArrosageTask.isSelected()) {
                textAreaArrosageTask.setDisable(false);
            } else {
                textAreaArrosageTask.setDisable(true);
            }
        });
        checkBoxEntretienTask.setOnAction(event -> {
            if (checkBoxEntretienTask.isSelected()) {
                textAreaEntretienTask.setDisable(false);
            } else {
                textAreaEntretienTask.setDisable(true);
            }
        });
        checkBoxCoupeTask.setOnAction(event -> {
            if (checkBoxCoupeTask.isSelected()) {
                textAreaCoupeTask.setDisable(false);
            } else {
                textAreaCoupeTask.setDisable(true);
            }
        });
        checkBoxRecolteTask.setOnAction(event -> {
            if (checkBoxRecolteTask.isSelected()) {
                textAreaRecolteTask.setDisable(false);
            } else {
                textAreaRecolteTask.setDisable(true);
            }
        });

        checkBoxRecurrenceTask.setOnAction(event -> {
            if (checkBoxRecurrenceTask.isSelected()) {
                splitMenuButtonRecurrenceTask.setDisable(false);
            } else {
                splitMenuButtonRecurrenceTask.setDisable(true);
            }
        });

        addTaskButton.setOnAction(event ->{
            textAreaArrosageTask.setText("");
            textAreaEntretienTask.setText("");
            textAreaCoupeTask.setText("");
            textAreaRecolteTask.setText("");

            splitMenuButtonDateEditTask.setVisible(false);
            datePickerRecurrenceTask.setVisible(true);
            saveTaskButton.setVisible(true);
            saveEditTaskButton.setVisible(false);
            switchTaskPane();
        });

        editTaskButton.setOnAction(event ->{
            if(plante.getTask() != null) {
                if (plante.getTask().has("Arrosage")) {
                    textAreaArrosageTask.setText(plante.getTask().getString("Arrosage"));
                }
                if (plante.getTask().has("Entretien")) {
                    textAreaEntretienTask.setText(plante.getTask().getString("Entretien"));
                }
                if (plante.getTask().has("Coupe")) {
                    textAreaCoupeTask.setText(plante.getTask().getString("Coupe"));
                }
                if (plante.getTask().has("Recolte")) {
                    textAreaRecolteTask.setText(plante.getTask().getString("Recolte"));
                }
            }
            splitMenuButtonDateEditTask.setVisible(true);
            datePickerRecurrenceTask.setVisible(false);
            saveTaskButton.setVisible(false);
            saveEditTaskButton.setVisible(true);
            switchTaskPane();
        });

        editSuiviButton.setOnAction(event ->{
            if(plante.getSuivi() != null) {
                if (plante.getSuivi().has("Remarque")) {
                    suiviTextArea.setText(plante.getSuivi().getString("Remarque"));
                }
            }
            saveSuiviButton.setVisible(false);
            saveEditSuiviButton.setVisible(true);
            suiviDatePicker.setVisible(false);
            graphiqueText.setVisible(true);
            dateID.setVisible(false);
            switchSuiviPane();
        });

        addSuiviButton.setOnAction(event ->{
            saveSuiviButton.setVisible(true);
            saveEditSuiviButton.setVisible(false);
            suiviDatePicker.setVisible(true);
            graphiqueText.setVisible(false);
            dateID.setVisible(true);
            switchSuiviPane();

        });


        saveButton.setOnAction(event -> handleSaveButtonClick(event));

        importImageButton.setOnAction(event -> handleImportImageButtonAction());

    }

    public void setPlante(Plante plante) {
        this.plante = plante;

        planteTask1.setText(plante.getNom());
        planteTask2.setText(plante.getNom());
        planteSuivi.setText(plante.getNom());
        planteSuivi2.setText(plante.getNom());

        nomLabel.setText(plante.getNom());
        saisonLabel.setText(plante.getSaison());
        vivaceLabel1.setText(plante.getVivace());
        dureePousseLabel.setText(plante.getDureePousse() + " " + plante.getUnitePousse());
        finalPlantationLabel.setText(plante.getPlantationDe() + " à " + plante.getPlantationA());
        intervalleArrosageLabel.setText(plante.getIntervalleArrosage() + " " +plante.getUniteArrosage());
        miseEnTerreLabel.setText( plante.getDateMiseEnTerre());
        if (plante.getDateRecolte() != null) {
            recolteLabel.setText(plante.getDateRecolte());
        } else {
            recolteLabel.setText("Ne se récolte pas");
        }
        /**
         * permet de charger les données dans les containers
         */
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        plantationDeSplitMenuButton.setText(plante.getPlantationDe());
        plantationASplitMenuButton.setText(plante.getPlantationA());
        saisonSplitMenuButton.setText(plante.getSaison());
        vivaceSplitMenuButton.setText(plante.getVivace());
        dureePousseTextField.setText(plante.getDureePousse());
        intervalleArrosageTextField.setText(plante.getIntervalleArrosage());
        if (plante.getDateMiseEnTerre() != null && !plante.getDateMiseEnTerre().isEmpty()) {
            LocalDate dateMiseEnTerre = LocalDate.parse(plante.getDateMiseEnTerre(), formatter);
            dateMiseEnTerrePicker.setValue(dateMiseEnTerre);
        }

        if (plante.getDateRecolte() != null && !plante.getDateRecolte().isEmpty()) {
            LocalDate dateRecolte = LocalDate.parse(plante.getDateRecolte(), formatter);
            dateRecoltePicker.setValue(dateRecolte);
        }
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
        if (plante.getTask() == null) {
            System.out.println("Pas de tache pour cette plante");
        } else {
            JSONObject tacheObj = plante.getTask();
            System.out.println(tacheObj);
            StringBuilder tacheBuilder = new StringBuilder();

            for (String dateKey : tacheObj.keySet()) {
                JSONObject dateObj = tacheObj.getJSONObject(dateKey);
                tacheBuilder.append("-Date: ").append(dateKey).append("\n");

                if (dateObj.has("Arrosage") && !dateObj.isNull("Arrosage")) {
                    tacheBuilder.append("Arrosage : ");
                    tacheBuilder.append("\n");
                    tacheBuilder.append(dateObj.getString("Arrosage"));
                    tacheBuilder.append("\n");
                }
                if (dateObj.has("Entretien") && !dateObj.isNull("Entretien")) {
                    tacheBuilder.append("Entretien : ");
                    tacheBuilder.append("\n");
                    tacheBuilder.append(dateObj.getString("Entretien"));
                    tacheBuilder.append("\n");
                }
                if (dateObj.has("Coupe") && !dateObj.isNull("Coupe")) {
                    tacheBuilder.append("Coupe : ");
                    tacheBuilder.append("\n");
                    tacheBuilder.append(dateObj.getString("Coupe"));
                    tacheBuilder.append("\n");
                }
                if (dateObj.has("Recolte") && !dateObj.isNull("Recolte")) {
                    tacheBuilder.append("Recolte : ");
                    tacheBuilder.append("\n");
                    tacheBuilder.append(dateObj.getString("Recolte"));
                    tacheBuilder.append("\n");
                }
            }

            String tacheTextString = tacheBuilder.toString();
            if (!tacheTextString.isEmpty()) {
                tacheText.setText(tacheTextString);
            } else {
                System.out.println("Pas de tache pour cette plante");
            }

            for (String dateKey : tacheObj.keySet()) {
                MenuItem dateMenuItem = new MenuItem(dateKey);

                dateMenuItem.setOnAction(e -> {
                    splitMenuButtonDateEditTask.setText(dateKey);
                    JSONObject dateObj = tacheObj.getJSONObject(dateKey);

                    if (dateObj.has("Arrosage") && !dateObj.isNull("Arrosage")) {
                        textAreaArrosageTask.setText(dateObj.getString("Arrosage"));
                    } else {
                        textAreaArrosageTask.setText("");
                    }
                    if (dateObj.has("Entretien") && !dateObj.isNull("Entretien")) {
                        textAreaEntretienTask.setText(dateObj.getString("Entretien"));
                    } else {
                        textAreaEntretienTask.setText("");
                    }
                    if (dateObj.has("Coupe") && !dateObj.isNull("Coupe")) {
                        textAreaCoupeTask.setText(dateObj.getString("Coupe"));
                    } else {
                        textAreaCoupeTask.setText("");
                    }
                    if (dateObj.has("Recolte") && !dateObj.isNull("Recolte")) {
                        textAreaRecolteTask.setText(dateObj.getString("Recolte"));
                    } else {
                        textAreaRecolteTask.setText("");
                    }

                    if (dateKey != null) {
                        checkBoxArrosageTask.setDisable(false);
                        checkBoxEntretienTask.setDisable(false);
                        checkBoxCoupeTask.setDisable(false);
                        checkBoxRecolteTask.setDisable(false);
                    } else {
                        checkBoxArrosageTask.setDisable(true);
                        checkBoxEntretienTask.setDisable(true);
                        checkBoxCoupeTask.setDisable(true);
                        checkBoxRecolteTask.setDisable(true);
                    }

                });

                splitMenuButtonDateEditTask.getItems().add(dateMenuItem);
            }
        }

        if (plante.getSuivi() == null) {
            System.out.println("Pas de suivi pour cette plante");
        } else {
            JSONObject suiviObj = plante.getSuivi();
            System.out.println(suiviObj);
            StringBuilder suiviBuilder = new StringBuilder();

            if (suiviObj.has("Remarque") && !suiviObj.isNull("Remarque")) {
                suiviBuilder.append("-Remarque : ");
                suiviBuilder.append("\n");
                suiviBuilder.append(suiviObj.getString("Remarque"));
                suiviBuilder.append("\n");
            } else {
                System.out.println("Pas de remarque pour cette plante");
            }

            String suiviTextString = suiviBuilder.toString();
            if (!suiviTextString.isEmpty()) {
                suiviText.setText(suiviTextString);
            } else {
                System.out.println("Pas de suivi pour cette plante");
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
        plante.setDateMiseEnTerre(dateMiseEnTerrePicker.getValue().toString());
        if (!plante.getVivace().equals("Oui")) {
            plante.setDateRecolte(dateRecoltePicker.getValue().toString());
        } else {
            plante.setDateRecolte(null);
        }
        plante.setImageURL(imageURL);
    }
    private void updateJSON() {
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
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                String intervalleArrosage = intervalleArrosageTextField.getText() + " " + uniteArrosageTextField.getText();
                jsonObject.put("IntervalleArrosage", intervalleArrosage);
                jsonObject.put("PlantationDe", plante.getPlantationDe());
                jsonObject.put("PlantationA", plante.getPlantationA());
                jsonObject.put("DateMiseEnTerre", plante.getDateMiseEnTerre());
                if (!plante.getVivace().equals("Oui")) {
                    jsonObject.put("DateRecolte", plante.getDateRecolte());
                }
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

    private void removeJSON() {
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
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                jsonArray.remove(i);
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

    /****TAB-PANE EDIT DETAIL****/


    @FXML
    public void handleSaveButtonClick(ActionEvent event) {
        updatePlante();
        updateJSON();
        switchEditPane();
    }

    @FXML
    public void handleEditButtonClick(ActionEvent event) {
        switchEditPane();
    }

    public void switchEditPane(){
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
    public void handleDeleteButtonClick(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer cette plante?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            removeJSON();
        }
    }

    @FXML
    public void handleCloseButtonClick(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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

    /****TAB-PANE TACHE****/

    private void saveTaskToPlantationJson() {
        String filePath = "src/main/resources/Plantation.json";
        JSONArray jsonArray;
        String taskArrosageText = textAreaArrosageTask.getText();
        String taskEntretienText = textAreaEntretienTask.getText();
        String taskCoupeText = textAreaCoupeTask.getText();
        String taskRecolteText = textAreaRecolteTask.getText();
        LocalDate selectedDate = datePickerRecurrenceTask.getValue(); // Get the selected date
        String dateString = selectedDate.toString(); // Convert the selected date to string

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
            System.out.println("Nom objet : " + jsonObject.getString("Nom") + "----- Nom Plante : " + plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                JSONObject tacheObj;
                if (jsonObject.has("Tache")) {
                    tacheObj = jsonObject.getJSONObject("Tache");
                } else {
                    tacheObj = new JSONObject();
                    jsonObject.put("Tache", tacheObj);
                }

                JSONObject dateObj;
                if (tacheObj.has(dateString)) {
                    dateObj = tacheObj.getJSONObject(dateString);
                } else {
                    dateObj = new JSONObject();
                    tacheObj.put(dateString, dateObj);
                }

                if (!taskArrosageText.isEmpty()) {
                    dateObj.put("Arrosage", taskArrosageText);
                }
                if (!taskEntretienText.isEmpty()) {
                    dateObj.put("Entretien", taskEntretienText);
                }
                if (!taskCoupeText.isEmpty()) {
                    dateObj.put("Coupe", taskCoupeText);
                }
                if (!taskRecolteText.isEmpty()) {
                    dateObj.put("Recolte", taskRecolteText);
                }
            }
        }
        writeJSONToFile(jsonArray, filePath);
    }

    @FXML
    private void handleSaveTaskButtonClick() {
        String taskArrosageText = textAreaArrosageTask.getText();
        String taskEntretienText = textAreaEntretienTask.getText();
        String taskCoupeText = textAreaCoupeTask.getText();
        String taskRecolteText = textAreaRecolteTask.getText();
        String taskText = "";
        if(!taskArrosageText.isEmpty()){
            taskText =  taskText + taskArrosageText;
        }
        if(!taskEntretienText.isEmpty()){
            taskText = taskText + taskEntretienText;
        }
        if(!taskCoupeText.isEmpty()){
            taskText = taskText + taskCoupeText;
        }
        if(!taskRecolteText.isEmpty()){
            taskText = taskText + taskRecolteText;
        }
        if (!taskText.isEmpty()) {
            saveTaskToPlantationJson();
            switchTaskPane();
        } else {
            System.out.println("TextArea vide");
        }
    }



    private void updateTaskJSON() {
        String filePath = "src/main/resources/Plantation.json";
        JSONArray jsonArray;
        String taskArrosageText = textAreaArrosageTask.getText();
        String taskEntretienText = textAreaEntretienTask.getText();
        String taskCoupeText = textAreaCoupeTask.getText();
        String taskRecolteText = textAreaRecolteTask.getText();
        String dateString = splitMenuButtonDateEditTask.getText();

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
            System.out.println("Nom objet : " + jsonObject.getString("Nom") + "----- Nom Plante : " + plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                JSONObject tacheObj;
                if (jsonObject.has("Tache")) {
                    tacheObj = jsonObject.getJSONObject("Tache");
                } else {
                    tacheObj = new JSONObject();
                    jsonObject.put("Tache", tacheObj);
                }

                JSONObject dateObj;
                if (tacheObj.has(dateString)) {
                    dateObj = tacheObj.getJSONObject(dateString);
                } else {
                    dateObj = new JSONObject();
                    tacheObj.put(dateString, dateObj);
                }

                if (!taskArrosageText.isEmpty()) {
                    dateObj.put("Arrosage", taskArrosageText);
                }
                if (!taskEntretienText.isEmpty()) {
                    dateObj.put("Entretien", taskEntretienText);
                }
                if (!taskCoupeText.isEmpty()) {
                    dateObj.put("Coupe", taskCoupeText);
                }
                if (!taskRecolteText.isEmpty()) {
                    dateObj.put("Recolte", taskRecolteText);
                }
                break;
            }
        }

        writeJSONToFile(jsonArray, filePath);
    }



    private void removeTaskJSON() {
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
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                jsonObject.remove("Tache");
            }
        }
        writeJSONToFile(jsonArray, filePath);
    }
    @FXML
    public void handleSaveEditTaskButtonClick(ActionEvent event) {
        updateTaskJSON();
    }
    @FXML
    public void handleEditTaskButtonClick(ActionEvent event) {
        switchTaskPane();
    }
    @FXML
    public void handleDeleteTaskButtonClick(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer toutes les taches?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            removeTaskJSON();
        }
    }
    @FXML
    public void handleAddTaskButtonClick(ActionEvent event) {
        switchTaskPane();
    }

    public void switchTaskPane(){
        if(edit == false){
            edit = true;
        } else {
            edit = false;
        }
        if(edit){
            taskDetailPane.setVisible(false);
            addTaskButton.setText("Annuler l'ajout de tache");
            addTaskButton.setLayoutX(115);
            addTaskButton.setLayoutY(381);
            addTaskDetailPane.setVisible(true);
            datePickerRecurrenceTask.setValue(null);
            textAreaArrosageTask.setText("");
            textAreaEntretienTask.setText("");
            textAreaCoupeTask.setText("");
            textAreaRecolteTask.setText("");
            textAreaArrosageTask.setDisable(true);
            textAreaEntretienTask.setDisable(true);
            textAreaCoupeTask.setDisable(true);
            textAreaRecolteTask.setDisable(true);
            checkBoxArrosageTask.setSelected(false);
            checkBoxEntretienTask.setSelected(false);
            checkBoxCoupeTask.setSelected(false);
            checkBoxRecolteTask.setSelected(false);

        } else {
            taskDetailPane.setVisible(true);
            addTaskButton.setText("Ajouté une tache");
            addTaskButton.setLayoutX(160);
            addTaskButton.setLayoutY(10);
            addTaskDetailPane.setVisible(false);
            splitMenuButtonDateEditTask.setText("Date des taches..");
        }
    }

    /****TAB-PANE SUIVI****/

    //variable


    private void saveSuiviToPlantationJson() {
        String filePath = "src/main/resources/Plantation.json";
        JSONArray jsonArray;
        String dp = suiviDatePicker.getValue().toString();
        String suivi = suiviTextArea.getText();
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
            System.out.println("Nom objet : " + jsonObject.getString("Nom") + "----- Nom Plante : " + plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                JSONObject tacheObj;
                if (jsonObject.has("Suivi")) {
                    tacheObj = jsonObject.getJSONObject("Suivi");
                } else {
                    tacheObj = new JSONObject();
                }
                if (!dp.isEmpty() && !suivi.isEmpty()) {
                    String remarqueKey = "Remarque";
                    if (tacheObj.has("Remarque")) {
                        String finalRemarque = dp + " : " + suivi + "\n" + tacheObj.getString(remarqueKey);
                        tacheObj.put(remarqueKey, finalRemarque);
                    } else {
                        String finalRemarque = dp + " : " + suivi;
                        tacheObj.put(remarqueKey, finalRemarque);
                    }
                }
                if(true){
                    String grapheKey = "Graphe";
                    String finalGraphe = "";
                    tacheObj.put(grapheKey, finalGraphe);
                }
                jsonObject.put("Suivi", tacheObj);
                break;
            }
        }
        writeJSONToFile(jsonArray, filePath);
    }
    @FXML
    private void handleSaveSuiviButtonClick(){
        saveSuiviToPlantationJson();
        switchSuiviPane();
    }

    private void updateSuiviJSON() {
        String filePath = "src/main/resources/Plantation.json";
        JSONArray jsonArray;
        String suivi = suiviTextArea.getText();
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
                JSONObject suiviObj = new JSONObject();
                if (!suivi.isEmpty()) {
                    String remarqueKey = "Remarque";
                    suiviObj.put(remarqueKey, suivi);
                }
                if (true) {
                    String grapheKey = "Graphe";
                    suiviObj.put(grapheKey, "");
                }
                jsonObject.put("Suivi", suiviObj);
                break;
            }
        }

        writeJSONToFile(jsonArray, filePath);
    }

    private void removeSuiviJSON() {
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
            System.out.println("Nom objet : "+jsonObject.getString("Nom")+ "----- Nom Plante : "+plante.getNom());
            if (jsonObject.getString("Nom").equals(plante.getNom())) {
                jsonObject.remove("Suivi");
            }
        }
        writeJSONToFile(jsonArray, filePath);
    }

    @FXML
    private void handleDeleteSuiviButtonClick(){
        Alert alert = new Alert(AlertType.CONFIRMATION, "Êtes-vous sûr de vouloir supprimer tous les suivis?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            removeSuiviJSON();
        }
    }
    @FXML
    private void handleAddSuiviButtonClick(){
        switchSuiviPane();
    }
    public void switchSuiviPane(){
        if(edit == false){
            edit = true;
        } else {
            edit = false;
        }
        if(edit){
            suiviDetailPane.setVisible(false);
            addSuiviButton.setText("Annuler l'ajout du suivi");
            addSuiviButton.setLayoutX(66);
            addSuiviButton.setLayoutY(390);
            addSuiviDetailPane.setVisible(true);
        } else {
            suiviDetailPane.setVisible(true);
            addSuiviButton.setText("Ajouté un suivi");
            addSuiviButton.setLayoutX(180);
            addSuiviButton.setLayoutY(10);
            addSuiviDetailPane.setVisible(false);
        }
    }


    @FXML
    private void handleEditSuiviButtonClick(){

    }
    @FXML
    private void handleSaveEditSuiviButtonClick(){
        updateSuiviJSON();
    }

}
