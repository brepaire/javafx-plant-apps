package com.example.projetplante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Calendrier {

    @JsonProperty("Date")
    private LocalDate date;
    private List<Plante> plantes;



    public Calendrier(LocalDate date) {
        this.date = date;
        this.plantes = chargerPlantesDepuisJson();
        generateJSONFilesForCurrentAndNextYear();
    }


    public void generatePlantesJSONForMonth(YearMonth yearMonth) {

        Map<LocalDate, List<Plante>> plantesParJour = new HashMap<>();

        for (Plante plante : plantes) {
            LocalDate dateMiseEnTerre = LocalDate.parse(plante.getDateMiseEnTerre(), DateTimeFormatter.ISO_DATE);
            LocalDate dateCourante = dateMiseEnTerre;
            String SEPARATEUR = " ";
            String mot = plante.getDureePousse();
            String mots[] = mot.split(SEPARATEUR);
            int duree = Integer.parseInt(mots[0]);
            LocalDate dateRecolte;
            if(plante.getDateRecolte()!=null){
                dateRecolte = LocalDate.parse(plante.getDateRecolte(), DateTimeFormatter.ISO_DATE);
            } else {
                dateRecolte = dateCourante.plusDays(duree);
            }
            while (dateCourante.isBefore(dateRecolte) || dateCourante.isEqual(dateRecolte)) {
                if (dateCourante.getMonth().equals(yearMonth.getMonth()) && dateCourante.getYear() == yearMonth.getYear()) {
                    if (!plantesParJour.containsKey(dateCourante)) {
                        plantesParJour.put(dateCourante, new ArrayList<>());
                    }
                    plantesParJour.get(dateCourante).add(plante);
                }
                dateCourante = dateCourante.plusDays(1);
            }
        }

        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<LocalDate, List<Plante>> entry : plantesParJour.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", entry.getKey().toString());
            JSONArray plantesJSONArray = new JSONArray();
            for (Plante plante : entry.getValue()) {
                JSONObject planteJSONObject = new JSONObject();
                planteJSONObject.put("Nom", plante.getNom());
                planteJSONObject.put("IntervalleArrosage", plante.getIntervalleArrosage());
                planteJSONObject.put("PlantationDe", plante.getPlantationDe());
                planteJSONObject.put("PlantationA", plante.getPlantationA());
                planteJSONObject.put("DateMiseEnTerre", plante.getDateMiseEnTerre());
                planteJSONObject.put("DureePousse", plante.getDureePousse());
                planteJSONObject.put("Saison", plante.getSaison());
                planteJSONObject.put("Vivace", plante.getVivace());
                planteJSONObject.put("Suivi", plante.getSuivi());
                planteJSONObject.put("Tache", plante.getTask());
                planteJSONObject.put("imageUrl", plante.getImageURL());

                plantesJSONArray.put(planteJSONObject);
            }
            jsonObject.put("plantes", plantesJSONArray);
            jsonArray.put(jsonObject);
        }

        // Écriture du fichier JSON
        String json = jsonArray.toString(4);
        File file = new File("src/main/resources/calendrier/plantes_" + yearMonth.toString() + ".json");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(json);
        } catch (IOException e){

        e.printStackTrace();
        }
    }


    public void generateJSONFilesForCurrentAndNextYear() {
        Year currentYear = Year.now();
        Year nextYear = currentYear.plusYears(1);

        for (int i = 1; i <= 12; i++) {
            YearMonth currentYearMonth = YearMonth.of(currentYear.getValue(), i);
            File file = new File("src/main/resources/plantes_" + currentYearMonth.toString() + ".json");
            //if (!file.exists()) {
                generatePlantesJSONForMonth(currentYearMonth);
            //}

            YearMonth nextYearMonth = YearMonth.of(nextYear.getValue(), i);
            file = new File("src/main/resources/plantes_" + nextYearMonth.toString() + ".json");
            if (!file.exists()) {
                generatePlantesJSONForMonth(nextYearMonth);
            }
        }
    }

        public LocalDate getDate() {
        return date;
    }

    public Map<LocalDate, List<Plante>> readPlantesJSONForMonth(YearMonth yearMonth) {
        String filePath = "src/main/resources/calendrier/plantes_" + yearMonth.toString() + ".json";
        Map<LocalDate, List<Plante>> plantesParJour = new HashMap<>();

        try {
            JSONArray jsonArray = new JSONArray(new String(Files.readAllBytes(Paths.get(filePath))));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                LocalDate date = LocalDate.parse(jsonObject.getString("date"));

                List<Plante> plantesDuJour = new ArrayList<>();
                JSONArray plantesJSONArray = jsonObject.getJSONArray("plantes");
                for (int j = 0; j < plantesJSONArray.length(); j++) {
                    JSONObject planteJSONObject = plantesJSONArray.getJSONObject(j);
                    Plante plante = new Plante();
                    plante.setNom(planteJSONObject.getString("Nom"));
                    plante.setIntervalleArrosage(planteJSONObject.getString("IntervalleArrosage"));
                    plante.setPlantationDe(planteJSONObject.getString("PlantationDe"));
                    plante.setPlantationA(planteJSONObject.getString("PlantationA"));
                    plante.setDateMiseEnTerre(planteJSONObject.getString("DateMiseEnTerre"));
                    plante.setDureePousse(planteJSONObject.getString("DureePousse"));
                    plante.setSaison(planteJSONObject.getString("Saison"));
                    plante.setVivace(planteJSONObject.getString("Vivace"));
                    if (planteJSONObject.has("Suivi")) {
                        plante.setSuivi(planteJSONObject.getJSONObject("Suivi"));
                    }
                    plante.setImageURL(planteJSONObject.getString("imageUrl"));
                    if (planteJSONObject.has("Tache")) {
                        plante.setTask(planteJSONObject.getJSONObject("Tache"));
                    }
                    plantesDuJour.add(plante);
                }

                plantesParJour.put(date, plantesDuJour);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return plantesParJour;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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




}