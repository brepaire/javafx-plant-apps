package com.example.projetplante;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.net.URL;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Plante {
    @JsonProperty("IntervalleArrosage")
    private String IntervalleArrosage;
    @JsonProperty("PlantationDe")
    private String PlantationDe;
    @JsonProperty("PlantationA")
    private String PlantationA;
    @JsonProperty("DateMiseEnTerre")
    private String DateMiseEnTerre;
    @JsonProperty("DateRecolte")
    private String DateRecolte;
    @JsonProperty("DureePousse")
    private String DureePousse;
    @JsonProperty("Nom")
    private String Nom;
    @JsonProperty("Saison")
    private String Saison;
    @JsonProperty("Vivace")
    private String Vivace;
    @JsonProperty("imageUrl")
    private String imageURL;
    @JsonProperty("Tache")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private JSONObject Task;
    public boolean hasTaches() {
        if(this.getTask() != null) {
            return true;
        } else {
            return false;
        }
    }
    @JsonProperty("Arrosage")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String Arrosage;
    @JsonProperty("Entretien")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String Entretien;
    @JsonProperty("Coupe")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String Coupe;
    @JsonProperty("Recolte")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String Recolte;
    @JsonProperty("Suivi")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private JSONObject suivi;
    @JsonProperty("Remarque")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String remarque;
    @JsonProperty("Graphe")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String graphe;

    public Plante(){

    }

    public String getVivace() {
        return Vivace;
    }
    public void setVivace(String vivace) {
        Vivace = vivace;
    }

    public String getPlantationDe() {
        return PlantationDe;
    }

    public void setPlantationDe(String plantationDe) {
        PlantationDe = plantationDe;
    }

    public String getPlantationA() {
        return PlantationA;
    }

    public void setPlantationA(String plantationA) {
        PlantationA = plantationA;
    }

    public String getDateMiseEnTerre() {
        return DateMiseEnTerre;
    }

    public void setDateMiseEnTerre(String dateMiseEnTerre) {
        DateMiseEnTerre = dateMiseEnTerre;
    }

    public String getDateRecolte() {
        return DateRecolte;
    }

    public void setDateRecolte(String dateRecolte) {
        DateRecolte = dateRecolte;
    }

    public String getIntervalleArrosage() {
        if (IntervalleArrosage != null) {
            String[] parts = IntervalleArrosage.split(" ");
            if (parts.length > 1) {
                return parts[0];
            }
        }
        return null;
    }
    public void setIntervalleArrosage(String intervalleArrosage) {
        String[] parts = intervalleArrosage.split(" ");
        this.IntervalleArrosage = parts[0];
    }
    public String getUniteArrosage() {
        if (IntervalleArrosage != null) {
            String[] parts = IntervalleArrosage.split(" ");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }

    public void setUniteArrosage(String intervalleArrosage) {
        String[] parts = intervalleArrosage.split(" ");
        if (parts.length > 1) {
            this.IntervalleArrosage = parts[0] + " " + parts[1];
        }
    }

    public String getDureePousse() {
        if (DureePousse != null) {
            String[] parts = DureePousse.split(" ");
            if (parts.length > 1) {
                return parts[0];
            }
        }
        return null;
    }

    public void setDureePousse(String dureePousse) {
        String[] parts = dureePousse.split(" ");
        this.DureePousse = parts[0];
    }
    public String getUnitePousse() {
        if (DureePousse != null) {
            String[] parts = DureePousse.split(" ");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return null;
    }

    public void setUnitePousse(String dureePousse) {
        String[] parts = dureePousse.split(" ");
        if (parts.length > 1) {
            this.DureePousse = parts[0] + " " + parts[1];
        }
    }

    public String getNom() {
        return Nom;
    }

    public void setNom(String nom) {
        Nom = nom;
    }

    public String getSaison() {
        return Saison;
    }

    public void setSaison(String saison) {
        Saison = saison;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public JSONObject getTask() {
        return Task;
    }

    public void setTask(JSONObject task) {
        Task = task;
    }

    public String getArrosage() {
        return Arrosage;
    }

    public void setArrosage(String arrosage) {
        Arrosage = arrosage;
    }

    public String getEntretien() {
        return Entretien;
    }

    public void setEntretien(String entretien) {
        Entretien = entretien;
    }

    public String getCoupe() {
        return Coupe;
    }

    public void setCoupe(String coupe) {
        Coupe = coupe;
    }

    public String getRecolte() {
        return Recolte;
    }

    public void setRecolte(String recolte) {
        Recolte = recolte;
    }

    public JSONObject getSuivi() {
        return suivi;
    }

    public void setSuivi(JSONObject suivi) {
        this.suivi = suivi;
    }

    public String getRemarque() {
        return remarque;
    }

    public void setRemarque(String remarque) {
        this.remarque = remarque;
    }

    public String getGraphe() {
        return graphe;
    }

    public void setGraphe(String graphe) {
        this.graphe = graphe;
    }
}
