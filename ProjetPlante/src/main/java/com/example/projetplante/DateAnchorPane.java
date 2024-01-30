package com.example.projetplante;

import javafx.scene.layout.AnchorPane;
import java.time.LocalDate;

public class DateAnchorPane extends AnchorPane {
    private LocalDate date;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}

